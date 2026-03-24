package net.trashelemental.artificers_armory.entity.ai.familiar.combat_abilities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarRole;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarTask;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import net.trashelemental.artificers_armory.junkyard_lib.util.UtilMethods;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.util.event.FirebrandEvents;

import java.util.List;

/**
 * Bruiser familiars can do a more powerful single melee attack that deals heavy damage and knockback, and leaves enemies
 * with a short debuff.
 */

public class BruiserSuperPunchTask implements FamiliarTask {

    private LivingEntity target;
    private int tickCounter = 0;
    private boolean punched = false;

    @Override
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public boolean canRun(FamiliarEntity familiar) {
        punched = false;
        target = familiar.getTarget();
        if (familiar.getRole() != FamiliarRole.BRUISER) return false;

        return target != null && target.isAlive();
    }

    @Override
    public int getWeight(FamiliarEntity familiar) {
        return 5;
    }

    @Override
    public void start(FamiliarEntity familiar) {
        if (target == null || !target.isAlive()) return;
        ParticleMethods.ParticlesAroundServerSide(familiar.level(), ParticleTypes.ANGRY_VILLAGER,
                familiar.getX(), familiar.getEyeY(), familiar.getZ(), 3, 1.5);
        tickCounter = 0;
    }


    @Override
    public void tick(FamiliarEntity familiar) {
        if (target == null || !target.isAlive()) return;
        familiar.getLookControl().setLookAt(target);
        double distance = familiar.distanceTo(target);

        if (!punched && distance > 2.2) {
            familiar.getNavigation().moveTo(target, 2);
            return;
        }

        if (!punched && tickCounter == 0) {
            familiar.getNavigation().stop();
            familiar.freezeMovement();
            familiar.noPhysics = true;
            familiar.setInvulnerable(true);
            familiar.triggerAnim("behavior", "punch");
            familiar.level().playSound(null, familiar.blockPosition(),
                    SoundEvents.VEX_CHARGE, SoundSource.NEUTRAL, 0.5f, 1f);
        }

        tickCounter++;

        if (!punched && tickCounter == 6) {
            doPunch(familiar);
            punched = true;
        }
    }

    private void doPunch(FamiliarEntity familiar) {
        if (target == null || !target.isAlive()) return;
        if (!(familiar.getOwner() instanceof Player owner)) return;
        float damage = (float) familiar.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2;
        double knockback = 1;
        double dx = familiar.getX() - target.getX();
        double dz = familiar.getZ() - target.getZ();
        int duration = familiar.getLevel() * 20;
        int amplifier = Math.max(0, familiar.getEnchantLevel() - 1);

        target.knockback(knockback, dx, dz);
        target.setDeltaMovement(target.getDeltaMovement().add(0, 0.05, 0));
        target.hurt(familiar.damageSources().mobAttack(familiar), damage);

        UtilMethods.applyEffectWithParticles(target, MobEffects.WEAKNESS, duration, amplifier);
        UtilMethods.applyEffectWithParticles(target, MobEffects.MOVEMENT_SLOWDOWN, duration, amplifier);

        ParticleMethods.ParticlesAroundServerSide(familiar.level(), ParticleTypes.CRIT,
                target.getX(), target.getEyeY(), target.getZ(), 5, 1.5);
        familiar.level().playSound(null, familiar.blockPosition(),
                SoundEvents.PLAYER_ATTACK_CRIT, SoundSource.NEUTRAL, 1, 1f);

        AABB hitbox = familiar.getBoundingBox().inflate(1.8);
        List<LivingEntity> victims = familiar.level().getEntitiesOfClass(LivingEntity.class, hitbox);

        for (LivingEntity entity : victims) {
            if (entity == target) continue;
            if (!(entity instanceof Mob)) continue;
            if (entity == familiar) continue;
            if (!entity.isAlive()) continue;
            if (FirebrandEvents.isAlly(owner, entity)) continue;

            entity.knockback(knockback / 2, dx, dz);
            entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.05, 0));
            entity.hurt(familiar.damageSources().mobAttack(familiar), damage / 2);

            ParticleMethods.ParticlesAroundServerSide(familiar.level(), ParticleTypes.CRIT,
                    entity.getX(), entity.getEyeY(), entity.getZ(), 3, 1.5);
        }

        familiar.getCurrentTask().stop(familiar);

    }

    @Override
    public int getDuration(FamiliarEntity familiar) {
        return 140;
    }

    @Override
    public void stop(FamiliarEntity familiar) {
        familiar.noPhysics = false;
        familiar.setInvulnerable(false);
        target = null;
    }
}

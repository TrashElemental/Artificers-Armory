package net.trashelemental.artificers_armory.entity.ai.familiar.combat_abilities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Shulker;
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
 * Bruiser familiars can do a more powerful single melee attack that deals heavy damage and knocks enemies into
 * the air.
 */

public class BruiserUppercutTask implements FamiliarTask {

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
        if (target == null || !target.isAlive()) return false;

        return !isFallImmune(target);
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
            familiar.triggerAnim("behavior", "uppercut");
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
        float damage = (float) familiar.getAttributeValue(Attributes.ATTACK_DAMAGE);
        double launch = 0.9 + (familiar.getEnchantLevel() * 0.15);

        target.hurt(familiar.damageSources().mobAttack(familiar), damage);
        target.setDeltaMovement(target.getDeltaMovement().x, launch, target.getDeltaMovement().z);
        target.hurtMarked = true;

        ParticleMethods.ParticlesBurst(familiar.level(), ParticleTypes.CRIT,
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

            entity.hurt(familiar.damageSources().mobAttack(familiar), damage / 2);
            entity.setDeltaMovement(entity.getDeltaMovement().x, launch, entity.getDeltaMovement().z);
            entity.hurtMarked = true;

            ParticleMethods.ParticlesBurst(familiar.level(), ParticleTypes.CRIT,
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

    private boolean isFallImmune(LivingEntity entity) {
        if (entity.getType().is(EntityTypeTags.FALL_DAMAGE_IMMUNE)) return true;
        if (entity instanceof FlyingMob) return true;
        return entity instanceof Blaze || entity instanceof Ghast || entity instanceof Phantom
                || entity instanceof Shulker || entity instanceof IronGolem;
    }
}

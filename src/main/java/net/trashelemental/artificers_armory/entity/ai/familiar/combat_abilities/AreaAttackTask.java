package net.trashelemental.artificers_armory.entity.ai.familiar.combat_abilities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarRole;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarTask;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.util.event.FirebrandEvents;

import java.util.List;

public class AreaAttackTask implements FamiliarTask {

    private int tickCounter = 0;

    @Override
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public boolean canRun(FamiliarEntity familiar) {
        if (familiar.getLevel() < 5) return false;
        if (familiar.getRole() == FamiliarRole.PRANKSTER) return false;
        if (familiar.getRole() == FamiliarRole.HEALER) return false;
        if (!(familiar.getOwner() instanceof Player owner)) return false;

        List<LivingEntity> nearby = familiar.level().getEntitiesOfClass(
                LivingEntity.class, familiar.getBoundingBox().inflate(3));

        for (LivingEntity entity : nearby) {
            if (!(entity instanceof Mob)) continue;
            if (entity == familiar) continue;
            if (!entity.isAlive()) continue;
            if (FirebrandEvents.isAlly(owner, entity)) continue;
            return true;
        }

        return false;
    }

    @Override
    public int getWeight(FamiliarEntity familiar) {
        return 3;
    }

    @Override
    public void start(FamiliarEntity familiar) {
        tickCounter = 0;
        familiar.freezeMovement();
        familiar.noPhysics = true;
        familiar.setInvulnerable(true);

        familiar.level().playSound(null, familiar.blockPosition(),
                SoundEvents.ALLAY_ITEM_TAKEN, SoundSource.NEUTRAL, 0.5f, 1f);
        familiar.triggerAnim("behavior", "swirl");

        doAreaDamage(familiar);
    }

    @Override
    public void tick(FamiliarEntity familiar) {
        tickCounter++;
        familiar.freezeMovement();
        if (tickCounter == 15) {
            doAreaDamage(familiar);
        }
    }

    private void doAreaDamage(FamiliarEntity familiar) {
        if (!(familiar.getOwner() instanceof Player owner)) return;
        List<LivingEntity> nearby = familiar.level().getEntitiesOfClass(LivingEntity.class, familiar.getBoundingBox().inflate(3));
        float damage = (float) familiar.getAttributeValue(Attributes.ATTACK_DAMAGE);

        for (LivingEntity entity : nearby) {
            if (!(entity instanceof Mob)) continue;
            if (entity == familiar) continue;
            if (!entity.isAlive()) continue;
            if (FirebrandEvents.isAlly(owner, entity)) continue;
            entity.hurt(familiar.damageSources().mobAttack(familiar), damage);
            ParticleMethods.ParticlesAroundServerSide(familiar.level(), ParticleTypes.ENCHANTED_HIT,
                    entity.getX(), entity.getEyeY(), entity.getZ(), 3, 1.5);
        }


    }

    @Override
    public int getDuration(FamiliarEntity familiar) {
        return 20;
    }

    @Override
    public void stop(FamiliarEntity familiar) {
        familiar.noPhysics = false;
        familiar.setInvulnerable(false);
    }
}
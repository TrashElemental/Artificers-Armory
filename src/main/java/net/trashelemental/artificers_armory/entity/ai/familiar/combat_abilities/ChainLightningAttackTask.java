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

public class ChainLightningAttackTask implements FamiliarTask {

    private LivingEntity target;
    private int tickCounter = 0;

    @Override
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public boolean canRun(FamiliarEntity familiar) {
        if (familiar.getLevel() < 6) return false;
        if (familiar.getRole() == FamiliarRole.PRANKSTER) return false;
        if (familiar.getRole() == FamiliarRole.HEALER) return false;
        if (!(familiar.getTarget() == null) && familiar.getTarget().isAlive()) {
            target = familiar.getTarget();
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
        if (target == null || !target.isAlive()) return;
        familiar.freezeMovement();
        familiar.noPhysics = true;
        familiar.setInvulnerable(true);
        familiar.getLookControl().setLookAt(target);

        familiar.level().playSound(null, familiar.blockPosition(),
                SoundEvents.ALLAY_ITEM_TAKEN, SoundSource.NEUTRAL, 0.5f, 1f);
        familiar.triggerAnim("behavior", "minorSupport");
    }

    @Override
    public void tick(FamiliarEntity familiar) {
        tickCounter++;
        if (target == null || !target.isAlive()) return;
        familiar.getLookControl().setLookAt(target);
        familiar.freezeMovement();
        if (tickCounter == 10) {
            doChainLightning(familiar);
        }
    }

    private void doChainLightning(FamiliarEntity familiar) {
        if (!(familiar.getOwner() instanceof Player owner)) return;
        if (target == null || !target.isAlive()) return;
        float damage = (float) familiar.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2;

        ParticleMethods.ParticleTrailEntityToEntity(familiar.level(), ParticleTypes.ELECTRIC_SPARK, familiar, target, 15);
        target.hurt(familiar.damageSources().mobAttack(familiar), damage);

        List<LivingEntity> nearby = familiar.level().getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(3));

        int chains = 0;

        for (LivingEntity entity : nearby) {
            if (chains >= 5) break;
            if (!(familiar.getRandom().nextFloat() < 0.75f)) continue;
            if (entity == target) continue;
            if (!(entity instanceof Mob)) continue;
            if (entity == familiar) continue;
            if (!entity.isAlive()) continue;
            if (FirebrandEvents.isAlly(owner, entity)) continue;

            entity.hurt(familiar.damageSources().mobAttack(familiar), damage / 2);
            familiar.level().playSound(null, familiar.blockPosition(),
                    SoundEvents.EVOKER_CAST_SPELL, SoundSource.NEUTRAL, 0.5f, 1.5f);
            ParticleMethods.ParticleTrailEntityToEntity(familiar.level(), ParticleTypes.ELECTRIC_SPARK, target, entity, 8);
            chains++;
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
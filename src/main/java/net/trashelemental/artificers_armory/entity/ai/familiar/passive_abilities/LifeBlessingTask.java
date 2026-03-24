package net.trashelemental.artificers_armory.entity.ai.familiar.passive_abilities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarRole;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarTask;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.util.event.FirebrandEvents;

import java.util.List;

/**
 *  Healer Familiar exclusive: Grant owner and all nearby allies Health Boost for 3 + level minutes, with the amplifier
 *  corresponding to the enchantment level.
 */

public class LifeBlessingTask implements FamiliarTask {

    @Override
    public boolean canRun(FamiliarEntity familiar) {
        LivingEntity livingEntity = familiar.getOwner();
        if (!(livingEntity instanceof Player owner)) return false;
        if (owner.hasEffect(MobEffects.HEALTH_BOOST)) return false;
        return familiar.getRole() == FamiliarRole.HEALER;
    }


    @Override
    public int getWeight(FamiliarEntity familiar) {
        return 4;
    }

    @Override
    public void start(FamiliarEntity familiar) {
        LivingEntity livingEntity = familiar.getOwner();
        if (!(livingEntity instanceof Player owner)) return;
        familiar.getNavigation().stop();
        familiar.getLookControl().setLookAt(owner);

        int healAmount = 4 * familiar.getEnchantLevel();
        int duration = 3600 + (familiar.getLevel() * 1200);
        int amplifier = Math.max(0, familiar.getEnchantLevel() - 1);

        owner.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, duration, amplifier, false, false));
        owner.heal(healAmount);
        ParticleMethods.ParticleTrailEntityToEntity(familiar.level(), ParticleTypes.HEART, familiar, owner, 5);

        List<LivingEntity> nearby = familiar.level().getEntitiesOfClass(LivingEntity.class, familiar.getBoundingBox().inflate(12));

        for (LivingEntity entity : nearby) {
            if (entity.hasEffect(MobEffects.HEALTH_BOOST)) continue;
            if (entity == familiar) continue;
            if (FirebrandEvents.isAlly(owner, entity)) {
                entity.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, duration, amplifier, false, false));
                entity.heal(healAmount);
                ParticleMethods.ParticleTrailEntityToEntity(familiar.level(), ParticleTypes.HEART, familiar, entity, 5);
            }
        }

        familiar.level().playSound(null, familiar.blockPosition(),
                SoundEvents.ALLAY_AMBIENT_WITH_ITEM, SoundSource.NEUTRAL, 0.5f, 1f);
        familiar.triggerAnim("behavior", "support");
    }

    @Override
    public void tick(FamiliarEntity familiar) {
        LivingEntity livingEntity = familiar.getOwner();
        if (!(livingEntity instanceof Player owner)) return;
        familiar.getLookControl().setLookAt(owner);
        familiar.freezeMovement();
    }


    @Override
    public int getDuration(FamiliarEntity familiar) {
        return 20;
    }

    @Override
    public void stop(FamiliarEntity familiar) {
    }
}

package net.trashelemental.artificers_armory.entity.ai.familiar.passive_abilities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarEventHandlers;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarRole;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarTask;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.particle.ModParticles;

/**
 * The familiar can give the owner a random positive effect.
 */

public class RandomSupportOwnerTask implements FamiliarTask {

    private Player owner;
    private int duration;
    private int amplifier;

    @Override
    public boolean canRun(FamiliarEntity familiar) {
        owner = null;
        duration = 0;
        if (!(familiar.getOwner() instanceof Player player)) return false;
        if (familiar.getLevel() < 6) return false;
        if (familiar.getRole() == FamiliarRole.BRUISER) return false;
        owner = player;
        duration = (familiar.getLevel() * 20) + 100;
        amplifier = 0;
        return true;
    }

    @Override
    public int getWeight(FamiliarEntity familiar) {
        if (familiar.getRole() == FamiliarRole.HEALER) return 3;
        return 1;
    }

    @Override
    public void start(FamiliarEntity familiar) {
        if (!(familiar.getOwner() instanceof Player player)) return;
        owner = player;
        familiar.freezeMovement();
        familiar.getLookControl().setLookAt(owner);

        MobEffect effect = switch (familiar.getRandom().nextInt(3)) {
            case 0 -> MobEffects.MOVEMENT_SPEED;
            case 1 -> MobEffects.REGENERATION;
            default -> MobEffects.DIG_SPEED;
        };

        MobEffectInstance current = owner.getEffect(effect);

        if (current == null || current.getAmplifier() <= amplifier) {
            owner.addEffect(new MobEffectInstance(effect, duration, amplifier, false, false));
        }

        ParticleMethods.ParticleTrailEntityToEntity(familiar.level(), ParticleTypes.HAPPY_VILLAGER, familiar, owner, 5);
        ParticleMethods.ParticlesAroundServerSide(familiar.level(), ParticleTypes.HAPPY_VILLAGER,
                owner.getX(), owner.getEyeY(), owner.getZ(), 5, 1.2);
        familiar.level().playSound(null, familiar.blockPosition(),
                SoundEvents.ALLAY_AMBIENT_WITH_ITEM, SoundSource.NEUTRAL, 0.5f, 1f);
        familiar.triggerAnim("behavior", "support");

        FamiliarEventHandlers.giveHealerBonusEffects(familiar, player);
        FamiliarEventHandlers.giveProtectorBonusEffects(familiar, player);
    }

    @Override
    public void tick(FamiliarEntity familiar) {
        if (owner == null) return;
        familiar.freezeMovement();
        familiar.getLookControl().setLookAt(owner);
    }

    @Override
    public int getDuration(FamiliarEntity familiar) {
        return 20;
    }

    @Override
    public void stop(FamiliarEntity familiar) {

    }
}

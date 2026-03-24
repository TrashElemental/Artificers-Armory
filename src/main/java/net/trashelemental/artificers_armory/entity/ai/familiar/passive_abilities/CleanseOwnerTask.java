package net.trashelemental.artificers_armory.entity.ai.familiar.passive_abilities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarEventHandlers;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarRole;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarTask;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;

import java.util.List;

/**
 * The familiar can reduce the duration of negative effects.
 * Healer role boosts this ability to reduce/remove multiple negative effects at once and also decrease the amplifier.
 */

public class CleanseOwnerTask implements FamiliarTask {

    private Player owner;
    private int reduction;

    @Override
    public boolean canRun(FamiliarEntity familiar) {
        owner = null;
        reduction = 0;
        if (!(familiar.getOwner() instanceof Player player)) return false;
        if (familiar.getLevel() < 4) return false;
        boolean hasNegative = player.getActiveEffects().stream()
                .anyMatch(effect -> effect.getEffect().getCategory() == MobEffectCategory.HARMFUL);
        if (!hasNegative) return false;
        owner = player;
        reduction = familiar.getLevel() * 300;
        return true;
    }

    @Override
    public int getWeight(FamiliarEntity familiar) {
        if (familiar.getRole() == FamiliarRole.HEALER) return 5;
        return 3;
    }

    @Override
    public void start(FamiliarEntity familiar) {
        if (!(familiar.getOwner() instanceof Player player)) return;
        owner = player;
        familiar.freezeMovement();
        familiar.getLookControl().setLookAt(owner);

        List<MobEffectInstance> negativeEffects = owner.getActiveEffects()
                .stream().filter(effect -> effect.getEffect().getCategory() == MobEffectCategory.HARMFUL).toList();
        if (negativeEffects.isEmpty()) return;

        if (familiar.getRole() == FamiliarRole.HEALER) {
            int amplifierReduction = familiar.getEnchantLevel();

            for (MobEffectInstance effect : negativeEffects) {
                int newDuration = Math.max(0, effect.getDuration() - reduction);
                int newAmplifier = Math.max(0, effect.getAmplifier() - amplifierReduction);
                owner.removeEffect(effect.getEffect());
                if (newDuration > 0 && newAmplifier > 0) {
                    owner.addEffect(new MobEffectInstance(effect.getEffect(), newDuration, newAmplifier,
                            effect.isAmbient(), effect.isVisible(), effect.showIcon()));
                }
            }
        } else {
            MobEffectInstance chosen = negativeEffects.get(familiar.getRandom().nextInt(negativeEffects.size()));
            int newDuration = Math.max(0, chosen.getDuration() - reduction);
            owner.removeEffect(chosen.getEffect());
            if (newDuration > 0) {
                owner.addEffect(new MobEffectInstance(chosen.getEffect(), newDuration, chosen.getAmplifier(), chosen.isAmbient(), chosen.isVisible(), chosen.showIcon()));
            }
        }

        ParticleMethods.ParticleTrailEntityToEntity(familiar.level(), ParticleTypes.HAPPY_VILLAGER, familiar, owner, 5);
        ParticleMethods.ParticlesAroundServerSide(familiar.level(), ParticleTypes.HAPPY_VILLAGER,
                owner.getX(), owner.getEyeY(), owner.getZ(), 5, 1.2);
        familiar.level().playSound(null, familiar.blockPosition(),
                SoundEvents.ALLAY_ITEM_TAKEN, SoundSource.NEUTRAL, 0.5f, 1f);
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

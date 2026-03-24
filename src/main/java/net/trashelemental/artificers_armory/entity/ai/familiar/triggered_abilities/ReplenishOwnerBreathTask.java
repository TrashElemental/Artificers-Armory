package net.trashelemental.artificers_armory.entity.ai.familiar.triggered_abilities;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarEventHandlers;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarTask;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;

/**
 * If the owner takes damage from drowning, restore some of their breath meter. At level four+, give a few seconds of
 * water breathing.
 */

public class ReplenishOwnerBreathTask implements FamiliarTask {

    private Player owner;
    private int timer;

    @Override
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public boolean canTrigger(FamiliarEntity familiar, DamageSource source) {
        if (familiar.getLevel() < 3) return false;
        if (!(familiar.getOwner() instanceof Player player)) return false;
        if (player.hasEffect(MobEffects.WATER_BREATHING)) return false;
        timer = 0;
        return source.is(DamageTypes.DROWN);
    }

    @Override
    public boolean canRun(FamiliarEntity familiar) {
        return false;
    }

    @Override
    public int getWeight(FamiliarEntity familiar) {
        return 0;
    }

    @Override
    public void start(FamiliarEntity familiar) {
        if (!(familiar.getOwner() instanceof Player player)) return;
        owner = player;
        familiar.noPhysics = true;
        familiar.setInvulnerable(true);
        familiar.teleportTo(owner.getX(), owner.getY() + 0.5, owner.getZ());
        int refillAmount = 30 + (familiar.getLevel() * 30);
        int newAir = Math.min(player.getMaxAirSupply(), player.getAirSupply() + refillAmount);
        player.setAirSupply(newAir);
        familiar.triggerAnim("behavior", "swirl");
        familiar.level().playSound(null, owner.blockPosition(), SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM, SoundSource.NEUTRAL, 0.4f, 1f);

        FamiliarEventHandlers.giveHealerBonusEffects(familiar, player);
        FamiliarEventHandlers.giveProtectorBonusEffects(familiar, player);

        if (familiar.getLevel() >= 7) {
            owner.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 20 * (familiar.getLevel() + 2)));
        }
        else if (familiar.getLevel() >= 4) {
            owner.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 20 * (familiar.getLevel() + 2)));
        }
    }

    @Override
    public void tick(FamiliarEntity familiar) {
        timer++;
        if (owner != null) {
            familiar.setPos(owner.getX(), owner.getY() + 0.5, owner.getZ());
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
        owner = null;
    }
}

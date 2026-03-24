package net.trashelemental.artificers_armory.entity.ai.familiar.triggered_abilities;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarEventHandlers;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarTask;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;

/**
 * If the owner takes damage from being on fire, the familiar will extinguish the fire. At level 3+, it can also react
 * to lava and in-fire damage, giving the player fire resistance for a short time.
 */

public class ExtinguishOwnerTask implements FamiliarTask {

    private Player owner;
    private int timer;

    @Override
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public boolean canTrigger(FamiliarEntity familiar, DamageSource source) {
        if (familiar.getLevel() < 2) return false;
        if (!(familiar.getOwner() instanceof Player player)) return false;
        if (player.hasEffect(MobEffects.FIRE_RESISTANCE)) return false;
        timer = 0;

        if (familiar.getLevel() >= 3) {
            return source.is(DamageTypes.IN_FIRE) ||
                    source.is(DamageTypes.ON_FIRE) ||
                    source.is(DamageTypes.LAVA);
        }
        return source.is(DamageTypes.ON_FIRE);
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
        owner.clearFire();

        FamiliarEventHandlers.giveHealerBonusEffects(familiar, player);
        FamiliarEventHandlers.giveProtectorBonusEffects(familiar, player);

        familiar.triggerAnim("behavior", "swirl");
        familiar.level().playSound(null, owner.blockPosition(), SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM, SoundSource.NEUTRAL, 0.4f, 1f);

        if (familiar.getLevel() >= 3) {
            owner.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 20 * (familiar.getLevel() + 2)));
        }
    }

    @Override
    public void tick(FamiliarEntity familiar) {
        timer++;
        if (owner != null) {
            familiar.setPos(owner.getX(), owner.getY() + 0.5, owner.getZ());
            owner.clearFire();
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

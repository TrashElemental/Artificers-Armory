package net.trashelemental.artificers_armory.entity.ai.familiar.triggered_abilities;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarEventHandlers;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarTask;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;

public class SaveOwnerFromFallingTask implements FamiliarTask {

    private Player owner;
    private int timer;

    @Override
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public boolean canTriggerFall(FamiliarEntity familiar, float distance) {
        if (familiar.getLevel() < 2) return false;
        if (!(familiar.getOwner() instanceof Player player)) return false;
        if (player.hasEffect(MobEffects.SLOW_FALLING)) return false;
        return distance >= 5;
    }

    @Override
    public void start(FamiliarEntity familiar) {
        if (!(familiar.getOwner() instanceof Player player)) return;
        owner = player;
        timer = 0;
        familiar.noPhysics = true;
        familiar.setInvulnerable(true);
        familiar.teleportTo(owner.getX(), owner.getY() + 0.5, owner.getZ());
        owner.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20 * (5 + familiar.getLevel())));

        FamiliarEventHandlers.giveHealerBonusEffects(familiar, player);
        FamiliarEventHandlers.giveProtectorBonusEffects(familiar, player);

        familiar.triggerAnim("behavior", "swirl");
        familiar.level().playSound(null, owner.blockPosition(),
                SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM, SoundSource.NEUTRAL, 0.4f, 1f);
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

    @Override
    public boolean canRun(FamiliarEntity familiar) {
        return false;
    }

    @Override
    public int getWeight(FamiliarEntity familiar) {
        return 0;
    }
}

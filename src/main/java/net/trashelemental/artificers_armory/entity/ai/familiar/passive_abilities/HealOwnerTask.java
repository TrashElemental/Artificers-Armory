package net.trashelemental.artificers_armory.entity.ai.familiar.passive_abilities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarEventHandlers;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarRole;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarTask;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;

/**
 * The familiar can heal the owner if it is in need of healing. Is much more likely to do this if the owner seriously
 * needs healing.
 */

public class HealOwnerTask implements FamiliarTask {

    private Player owner;
    private float healAmount;

    @Override
    public boolean canRun(FamiliarEntity familiar) {
        owner = null;
        if (!(familiar.getOwner() instanceof Player player)) return false;
        owner = player;
        float missingHealth = owner.getMaxHealth() - owner.getHealth();
        if (missingHealth < 1.0f) return false;
        int level = familiar.getLevel();
        healAmount = 1 + (2 * level);
        return true;
    }

    @Override
    public int getWeight(FamiliarEntity familiar) {
        if (owner == null) return 0;
        float healthPercent = owner.getHealth() / owner.getMaxHealth();
        if (healthPercent <= 0.25f) return 40;
        if (healthPercent <= 0.5f) return 25;
        if (healthPercent <= 0.75f) return 10;
        if (familiar.getRole() == FamiliarRole.HEALER) return 7;
        return 3;
    }

    @Override
    public void start(FamiliarEntity familiar) {
        if (owner == null) return;
        familiar.freezeMovement();
        familiar.getLookControl().setLookAt(owner);
        owner.heal(healAmount);
        ParticleMethods.ParticleTrailEntityToEntity(familiar.level(), ParticleTypes.HEART, familiar, owner, 5);
        familiar.level().playSound(null, familiar.blockPosition(),
                SoundEvents.ALLAY_AMBIENT_WITH_ITEM, SoundSource.NEUTRAL, 0.5f, 1f);

        familiar.triggerAnim("behavior", "minorSupport");
        FamiliarEventHandlers.giveHealerBonusEffects(familiar, owner);
        FamiliarEventHandlers.giveProtectorBonusEffects(familiar, owner);
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

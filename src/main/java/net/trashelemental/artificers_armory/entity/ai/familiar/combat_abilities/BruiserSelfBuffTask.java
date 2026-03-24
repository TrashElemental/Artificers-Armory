package net.trashelemental.artificers_armory.entity.ai.familiar.combat_abilities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarEventHandlers;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarRole;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarTask;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import net.trashelemental.artificers_armory.junkyard_lib.util.UtilMethods;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.util.event.FirebrandEvents;

import java.util.List;

/**
 * Bruiser familiars can boost their own attack damage and movement speed in combat.
 */

public class BruiserSelfBuffTask implements FamiliarTask {

    @Override
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public boolean canRun(FamiliarEntity familiar) {
        if (familiar.getRole() != FamiliarRole.BRUISER) return false;
        if (familiar.hasEffect(MobEffects.DAMAGE_BOOST)) return false;
        List<Mob> valid = familiar.getAwareness().nearbyMobs;

        return !valid.isEmpty();
    }

    @Override
    public int getWeight(FamiliarEntity familiar) {
        return 4;
    }

    @Override
    public void start(FamiliarEntity familiar) {
        if (!(familiar.getOwner() instanceof Player player)) return;
        familiar.freezeMovement();
        familiar.getLookControl().setLookAt(player);
        familiar.noPhysics = true;
        familiar.setInvulnerable(true);

        int duration = 60 + (familiar.getLevel() * 20);
        int amplifier = Math.max(0, familiar.getEnchantLevel() - 1);

        UtilMethods.applyEffectWithParticles(familiar, MobEffects.DAMAGE_BOOST, duration, amplifier);
        UtilMethods.applyEffectWithParticles(familiar, MobEffects.DAMAGE_RESISTANCE, duration, amplifier);
        UtilMethods.applyEffectWithParticles(familiar, MobEffects.MOVEMENT_SPEED, duration, amplifier);

        ParticleMethods.ParticlesAroundServerSide(familiar.level(), ParticleTypes.ANGRY_VILLAGER,
                familiar.getX(), familiar.getEyeY(), familiar.getZ(), 5, 1.2);
        familiar.level().playSound(null, familiar.blockPosition(),
                SoundEvents.VEX_CHARGE, SoundSource.NEUTRAL, 0.5f, 1f);
        familiar.triggerAnim("behavior", "support");
    }


    @Override
    public void tick(FamiliarEntity familiar) {
        familiar.freezeMovement();
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

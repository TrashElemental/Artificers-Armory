package net.trashelemental.artificers_armory.entity.ai.familiar.combat_abilities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarRole;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarTask;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import net.trashelemental.artificers_armory.junkyard_lib.util.UtilMethods;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.util.event.FirebrandEvents;

import java.util.*;

/**
 * In combat, protector familiars can 'taunt' enemies, compelling them to target it. When it does this, it gains
 * absorption and resistance to help keep it alive.
 */

public class ProtectorTauntTask implements FamiliarTask {

    @Override
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public boolean canRun(FamiliarEntity familiar) {
        if (familiar.getRole() != FamiliarRole.PROTECTOR) return false;
        List<Mob> valid = familiar.getAwareness().nearbyMobs;

        return !valid.isEmpty();
    }

    @Override
    public int getWeight(FamiliarEntity familiar) {
        return 3;
    }

    @Override
    public void start(FamiliarEntity familiar) {
        familiar.freezeMovement();
        familiar.noPhysics = true;
        familiar.setInvulnerable(true);
        UtilMethods.applyEffectWithParticles(familiar, MobEffects.ABSORPTION, 200, familiar.getEnchantLevel());
        UtilMethods.applyEffectWithParticles(familiar, MobEffects.DAMAGE_RESISTANCE, 200, familiar.getEnchantLevel());

        ParticleMethods.ParticlesAroundServerSide(familiar.level(), ParticleTypes.ANGRY_VILLAGER,
                familiar.getX(), familiar.getEyeY(), familiar.getZ(), 6, 1.2);
        familiar.level().playSound(null, familiar.blockPosition(),
                SoundEvents.ALLAY_ITEM_TAKEN, SoundSource.NEUTRAL, 1f, 1f);

        familiar.triggerAnim("behavior", "swirl");
    }

    @Override
    public void tick(FamiliarEntity familiar) {
        if (!(familiar.getOwner() instanceof Player owner)) return;
        familiar.freezeMovement();

        List<Mob> nearby = familiar.getAwareness().nearbyMobs;

        for (Mob mob : nearby) {
            if (mob == familiar) continue;
            if (!mob.isAlive()) continue;
            if (FirebrandEvents.isAlly(owner, mob)) continue;
            LivingEntity target = mob.getTarget();

            if (target == owner || (target != null && FirebrandEvents.isAlly(owner, target))) {
                mob.setTarget(familiar);
            }
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
package net.trashelemental.artificers_armory.entity.ai.familiar.combat_abilities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarAI;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarEventHandlers;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarRole;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarTask;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.util.event.FirebrandEvents;

import java.util.List;

/**
 * Apply a positive effect to the owner while in combat. Prankster has lower priority, healer has higher and boosted
 * effect.
 */

public class CombatSupportOwnerTask implements FamiliarTask {

    private Player owner;
    private int duration;
    private int amplifier;

    @Override
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public boolean canRun(FamiliarEntity familiar) {
        owner = null;
        duration = 0;
        amplifier = 0;
        if (familiar.getRole() == FamiliarRole.BRUISER) return false;
        if (!(familiar.getOwner() instanceof Player)) return false;
        return familiar.getLevel() >= 4;
    }

    @Override
    public int getWeight(FamiliarEntity familiar) {
        if (familiar.getRole() == FamiliarRole.PRANKSTER) return 1;
        if (familiar.getRole() == FamiliarRole.HEALER) return 5;
        if (familiar.getRole() == FamiliarRole.PROTECTOR) return 4;

        return 3;
    }

    @Override
    public void start(FamiliarEntity familiar) {
        if (!(familiar.getOwner() instanceof Player player)) return;
        owner = player;
        familiar.freezeMovement();
        familiar.getLookControl().setLookAt(owner);
        familiar.noPhysics = true;
        familiar.setInvulnerable(true);
        duration = (familiar.getLevel() + 3) * 20;
        amplifier = familiar.getRole() == FamiliarRole.HEALER ? familiar.getEnchantLevel() : 0;
        MobEffect effect = getRandomEffect(familiar);

        applyEffectIfStronger(owner, effect, duration, amplifier);
        FamiliarEventHandlers.giveHealerBonusEffects(familiar, player);
        FamiliarEventHandlers.giveProtectorBonusEffects(familiar, player);

        if (familiar.getRole() == FamiliarRole.HEALER) {
            List<LivingEntity> nearby = familiar.level().getEntitiesOfClass(LivingEntity.class, familiar.getBoundingBox().inflate(12));

            for (LivingEntity ally : nearby) {
                if (ally == familiar || ally == owner) continue;
                if (!FirebrandEvents.isAlly(owner, ally)) continue;
                applyEffectIfStronger(ally, effect, duration, amplifier);
                ParticleMethods.ParticleTrailEntityToEntity(familiar.level(), ParticleTypes.HAPPY_VILLAGER, familiar, ally, 5);
            }
        }

        ParticleMethods.ParticleTrailEntityToEntity(familiar.level(), ParticleTypes.HAPPY_VILLAGER, familiar, owner, 5);
        ParticleMethods.ParticlesAroundServerSide(familiar.level(), ParticleTypes.HAPPY_VILLAGER,
                owner.getX(), owner.getEyeY(), owner.getZ(), 5, 1.2);
        familiar.level().playSound(null, familiar.blockPosition(),
                SoundEvents.ALLAY_AMBIENT_WITH_ITEM, SoundSource.NEUTRAL, 0.5f, 1f);
        familiar.triggerAnim("behavior", "support");
    }

    private MobEffect getRandomEffect(FamiliarEntity familiar) {
        int level = familiar.getLevel();
        if (familiar.getRole() == FamiliarRole.HEALER) {
            return switch (familiar.getRandom().nextInt(5)) {
                case 0 -> MobEffects.DAMAGE_BOOST;
                case 1 -> MobEffects.DAMAGE_RESISTANCE;
                case 2 -> MobEffects.MOVEMENT_SPEED;
                case 3 -> MobEffects.DIG_SPEED;
                default -> MobEffects.REGENERATION;
            };
        }
        if (level >= 6) {
            return switch (familiar.getRandom().nextInt(4)) {
                case 0 -> MobEffects.DAMAGE_BOOST;
                case 1 -> MobEffects.DAMAGE_RESISTANCE;
                case 2 -> MobEffects.MOVEMENT_SPEED;
                default -> MobEffects.REGENERATION;
            };
        } else {
            return switch (familiar.getRandom().nextInt(3)) {
                case 0 -> MobEffects.DAMAGE_BOOST;
                case 1 -> MobEffects.MOVEMENT_SPEED;
                default -> MobEffects.REGENERATION;
            };
        }
    }

    private void applyEffectIfStronger(LivingEntity entity, MobEffect effect, int duration, int amplifier) {
        MobEffectInstance current = entity.getEffect(effect);
        if (current == null || current.getAmplifier() <= amplifier) {
            entity.addEffect(new MobEffectInstance(effect, duration, amplifier, false, false));
        }
    }

    @Override
    public void tick(FamiliarEntity familiar) {
        if (owner != null) {
            familiar.freezeMovement();
            familiar.getLookControl().setLookAt(owner);
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

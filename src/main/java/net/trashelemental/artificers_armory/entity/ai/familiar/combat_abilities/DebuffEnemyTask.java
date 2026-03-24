package net.trashelemental.artificers_armory.entity.ai.familiar.combat_abilities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarRole;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarTask;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.particle.ModParticles;
import net.trashelemental.artificers_armory.util.event.FirebrandEvents;

import java.util.Collections;
import java.util.List;

public class DebuffEnemyTask implements FamiliarTask {

    private LivingEntity target;
    private int duration;
    private int amplifier;

    @Override
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public boolean canRun(FamiliarEntity familiar) {
        duration = 0;
        amplifier = 0;
        target = null;
        if (familiar.getRole() == FamiliarRole.BRUISER) return false;
        if (familiar.getRole() == FamiliarRole.PROTECTOR) return false;
        if (familiar.getRole() == FamiliarRole.HEALER) return false;
        if (familiar.getLevel() < 4) return false;
        if (!(familiar.getOwner() instanceof Player owner)) return false;
        List<LivingEntity> nearby = familiar.level().getEntitiesOfClass(LivingEntity.class, familiar.getBoundingBox().inflate(12));
        LivingEntity best = null;
        double bestDistance = Double.MAX_VALUE;

        for (LivingEntity entity : nearby) {
            if (entity == owner) continue;
            if (entity == familiar) continue;
            if (!entity.isAlive()) continue;
            if (entity.getHealth() <= 3) continue;
            if (FirebrandEvents.isAlly(owner, entity)) continue;
            double dist = entity.distanceToSqr(owner);
            if (dist < bestDistance) {
                bestDistance = dist;
                best = entity;
            }
        }
        target = best;
        return best != null;
    }

    @Override
    public int getWeight(FamiliarEntity familiar) {
        if (familiar.getRole() == FamiliarRole.PRANKSTER) return 5;
        return 3;
    }

    @Override
    public void start(FamiliarEntity familiar) {
        if (target == null || !target.isAlive()) return;
        if (!(familiar.getOwner() instanceof Player owner)) return;
        familiar.freezeMovement();
        familiar.getLookControl().setLookAt(target);
        familiar.noPhysics = true;
        familiar.setInvulnerable(true);
        duration = (familiar.getLevel() + 3) * 20;
        amplifier = 0;
        int extraEntities = 0;
        if (familiar.getRole() == FamiliarRole.PRANKSTER) {
            amplifier = familiar.getEnchantLevel();
            extraEntities = 1 + familiar.getEnchantLevel();
        }

        MobEffect effect = getRandomEffect(familiar);

        applyEffectIfStronger(target, effect, duration, amplifier);

        if (familiar.getRole() == FamiliarRole.PRANKSTER) {
            List<LivingEntity> nearby = familiar.level().getEntitiesOfClass(LivingEntity.class, familiar.getBoundingBox().inflate(12));
            for (LivingEntity enemy : nearby) {
                if (extraEntities <= 0) break;
                if (enemy == target) continue;
                if (enemy == familiar || enemy == owner) continue;
                if (FirebrandEvents.isAlly(owner, enemy)) continue;
                if (!(enemy instanceof Mob mob && isEnemy(mob, familiar))) continue;
                applyEffectIfStronger(enemy, effect, duration, amplifier);
                ParticleMethods.ParticleTrailEntityToEntity(familiar.level(), ParticleTypes.SMOKE, familiar, enemy, 5);
                extraEntities--;
            }
        }

        ParticleMethods.ParticleTrailEntityToEntity(familiar.level(), ParticleTypes.SMOKE, familiar, target, 5);

        if (familiar.getRole() == FamiliarRole.PRANKSTER) {
            ParticleMethods.ParticlesAroundServerSide(familiar.level(), ModParticles.IMP.get(),
                    target.getX(), target.getEyeY(), target.getZ(), 5, 1.2);
        } else {
            ParticleMethods.ParticlesAroundServerSide(familiar.level(), ParticleTypes.SMOKE,
                    target.getX(), target.getEyeY(), target.getZ(), 5, 1.2);
        }

        familiar.level().playSound(null, familiar.blockPosition(),
                SoundEvents.ALLAY_ITEM_TAKEN, SoundSource.NEUTRAL, 0.5f, 1f);
        familiar.triggerAnim("behavior", "support");
    }

    private MobEffect getRandomEffect(FamiliarEntity familiar) {
        if (familiar.getRole() == FamiliarRole.PRANKSTER) {
            return switch (familiar.getRandom().nextInt(4)) {
                case 0 -> MobEffects.WEAKNESS;
                case 1 -> MobEffects.MOVEMENT_SLOWDOWN;
                case 2 -> MobEffects.POISON;
                default -> MobEffects.WITHER;
            };
        }
        else {
            return switch (familiar.getRandom().nextInt(3)) {
                case 0 -> MobEffects.WEAKNESS;
                case 1 -> MobEffects.MOVEMENT_SLOWDOWN;
                default -> MobEffects.POISON;
            };
        }
    }

    private void applyEffectIfStronger(LivingEntity entity, MobEffect effect, int duration, int amplifier) {
        MobEffectInstance current = entity.getEffect(effect);
        if (current == null || current.getAmplifier() <= amplifier) {
            entity.addEffect(new MobEffectInstance(effect, duration, amplifier, false, true));
        }
    }

    private boolean isEnemy(Mob mob, FamiliarEntity familiar) {
        LivingEntity target = mob.getTarget();
        return mob.isAggressive() &&
                (target == familiar.getOwner() ||
                        target instanceof OwnableEntity ownable && ownable.getOwner() == familiar.getOwner());
    }

    @Override
    public void tick(FamiliarEntity familiar) {
        if (target != null && target.isAlive()) {
            familiar.freezeMovement();
            familiar.getLookControl().setLookAt(target);
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
        target = null;
    }
}
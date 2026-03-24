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
import net.trashelemental.artificers_armory.junkyard_lib.util.UtilMethods;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.particle.ModParticles;
import net.trashelemental.artificers_armory.util.event.FirebrandEvents;

import java.util.ArrayList;
import java.util.List;

/**
 * Prankster familiars can 'possess' a target, continually hijacking their target to be other enemies and providing them
 * with a boost to damage and attack speed. If it runs out of alternate targets, it leaves the target with a debuff.
 * For players, apply nausea and occasionally hijack their movement.
 */

public class PranksterPossessionTask implements FamiliarTask {

    private LivingEntity target;

    @Override
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public boolean canRun(FamiliarEntity familiar) {
        target = null;
        if (familiar.getRole() != FamiliarRole.PRANKSTER) return false;
        if (!(familiar.getOwner() instanceof Player owner)) return false;

        List<LivingEntity> nearby = familiar.level().getEntitiesOfClass(LivingEntity.class, familiar.getBoundingBox().inflate(12));

        LivingEntity closestTarget = null;
        double closestDistance = Double.MAX_VALUE;
        int validCount = 0;

        for (LivingEntity entity : nearby) {
            if (entity == familiar || entity == owner) continue;
            if (!entity.isAlive() || entity.getHealth() <= 3) continue;
            if (FirebrandEvents.isAlly(owner, entity)) continue;

            validCount++;
            double dist = entity.distanceToSqr(owner);
            if (dist < closestDistance) {
                closestDistance = dist;
                closestTarget = entity;
            }
        }

        target = closestTarget;

        return target != null && validCount >= 2;
    }

    @Override
    public int getWeight(FamiliarEntity familiar) {
        if (familiar.getRole() == FamiliarRole.PRANKSTER) return 5;
        return 3;
    }

    @Override
    public void start(FamiliarEntity familiar) {
        if (target == null || !target.isAlive()) return;
        familiar.freezeMovement();
        familiar.getLookControl().setLookAt(target);
        familiar.noPhysics = true;
        familiar.setInvulnerable(true);
        familiar.moveTo(target.getOnPos().getCenter());

        ParticleMethods.ParticlesAroundServerSide(familiar.level(), ParticleTypes.ANGRY_VILLAGER,
                target.getX(), target.getEyeY(), target.getZ(), 5, 1.2);
        familiar.level().playSound(null, familiar.blockPosition(),
                SoundEvents.VEX_CHARGE, SoundSource.NEUTRAL, 0.5f, 1f);
        familiar.triggerAnim("behavior", "swirl");
    }


    @Override
    public void tick(FamiliarEntity familiar) {
        if (target == null || !target.isAlive()) {
            familiar.getCurrentTask().stop(familiar);
            return;
        }

        familiar.freezeMovement();
        familiar.moveTo(target.getX(), target.getEyeY() + 0.5, target.getZ());
        familiar.getLookControl().setLookAt(target);
        UtilMethods.applyEffectNoParticles(familiar, MobEffects.INVISIBILITY, 10, 1);

        if (target.tickCount % 20 == 0) {
            ParticleMethods.ParticlesAroundServerSide(familiar.level(), ModParticles.IMP.get(),
                    target.getX(), target.getEyeY(), target.getZ(), 3, 2);
        }

        if (target instanceof Mob mob) {
            List<Mob> enemies = getNearbyEnemies(familiar, mob);
            if (!enemies.isEmpty()) {
                applyBuff(familiar, mob);
                if (mob.getTarget() == null || !mob.getTarget().isAlive()) {
                    Mob newTarget = enemies.get(familiar.getRandom().nextInt(enemies.size()));
                    mob.setTarget(newTarget);
                }
            } else {
                applyDebuff(familiar, mob);
                familiar.getCurrentTask().stop(familiar);
            }
        }

        if (target instanceof Player enemyPlayer) {
            applyPlayerPossession(familiar, enemyPlayer);
        }
    }

    @Override
    public int getDuration(FamiliarEntity familiar) {
        return 200;
    }

    @Override
    public void stop(FamiliarEntity familiar) {
        if (target != null && target.isAlive()) {
            applyDebuff(familiar, target);
        }
        familiar.noPhysics = false;
        familiar.setInvulnerable(false);
        target = null;
    }

    private List<Mob> getNearbyEnemies(FamiliarEntity familiar, LivingEntity possessed) {
        if (!(familiar.getOwner() instanceof Player owner)) return List.of();
        List<Mob> enemies = new ArrayList<>();

        List<LivingEntity> nearby = familiar.level().getEntitiesOfClass(LivingEntity.class,
                possessed.getBoundingBox().inflate(10));

        for (LivingEntity entity : nearby) {
            if (!(entity instanceof Mob mob)) continue;
            if (entity == possessed) continue;
            if (!entity.isAlive()) continue;
            if (FirebrandEvents.isAlly(owner, entity)) continue;
            enemies.add(mob);
        }
        return enemies;
    }

    private void applyBuff(FamiliarEntity familiar, LivingEntity entity) {
        int amplifier = familiar.getEnchantLevel() + 1;

        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 5, amplifier, false, true));
        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 5, amplifier, false, true));
        entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 5, 2, false, true));
    }

    private void applyDebuff(FamiliarEntity familiar, LivingEntity entity) {
        int duration = (3 + familiar.getLevel()) * 20;
        int amplifier = familiar.getEnchantLevel();

        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, amplifier));
        entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, amplifier));
    }

    private void applyPlayerPossession(FamiliarEntity familiar, Player player) {
        int amplifier = familiar.getEnchantLevel();

        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 40, amplifier, false, true));
        if (familiar.getRandom().nextInt(20) == 0) {
            double x = (familiar.getRandom().nextDouble() - 0.5) * 0.8;
            double z = (familiar.getRandom().nextDouble() - 0.5) * 0.8;
            player.push(x, 0, z);
        }
    }
}
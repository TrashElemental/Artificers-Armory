package net.trashelemental.artificers_armory.entity.ai.familiar.passive_abilities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarRole;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarTask;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.particle.ModParticles;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * The familiar will alert its owner to nearby hostile mobs. It will prioritize alerting them to Creepers, then
 * mobs that are currently targeting the player, as well as mobs that the player can't see.
 * Improved by the protector role.
 */

public class AlertOwnerToMonsterTask implements FamiliarTask {

    private List<Mob> targets = new ArrayList<>();
    private int effectDuration = 500;

    @Override
    public boolean canRun(FamiliarEntity familiar) {
        targets.clear();
        List<Mob> mobs = familiar.getAwareness().nearbyMobs;
        if (mobs.isEmpty()) return false;
        if (!(familiar.getOwner() instanceof Player owner)) return false;
        int maxTargets = getMaxTargets(familiar);

        List<Mob> monsters = mobs.stream()
                .filter(m -> m instanceof Monster).filter(m -> !(m instanceof OwnableEntity ownable && ownable.getOwner() == owner))
                .filter(m -> !m.hasEffect(MobEffects.GLOWING)).filter(LivingEntity::isAlive).toList();

        if (monsters.isEmpty()) return false;

        List<Mob> creepers = monsters.stream()
                .filter(m -> m instanceof Creeper).sorted(Comparator.comparingDouble(owner::distanceToSqr)).limit(maxTargets).toList();

        if (!creepers.isEmpty()) {
            targets.addAll(creepers);
            return true;
        }

        List<Mob> targetingOwner = monsters.stream()
                .filter(m -> m.getTarget() == owner).sorted(Comparator.comparingDouble(owner::distanceToSqr)).limit(maxTargets).toList();

        if (!targetingOwner.isEmpty()) {
            targets.addAll(targetingOwner);
            return true;
        }

        List<Mob> hiddenMonsters = monsters.stream().filter(m -> !playerCanClearlySee(owner, m) || isBehindPlayer(owner, m))
                .sorted(Comparator.comparingDouble(owner::distanceToSqr)).limit(maxTargets).toList();

        if (!hiddenMonsters.isEmpty()) {
            targets.addAll(hiddenMonsters);
            effectDuration = 300;
            return true;
        }

        targets.addAll(monsters.stream().sorted(Comparator.comparingDouble(owner::distanceToSqr)).limit(maxTargets).toList());

        effectDuration = 200;
        if (familiar.getRole() == FamiliarRole.PROTECTOR) {
            effectDuration += familiar.getEnchantLevel() * 100;
        }

        return !targets.isEmpty();
    }

    @Override
    public int getWeight(FamiliarEntity familiar) {
        if (targets.isEmpty()) return 1;
        Mob primary = targets.get(0);
        if (!(familiar.getOwner() instanceof Player owner)) return 5;
        if (primary.getTarget() == owner || primary instanceof Creeper) {
            return 20;
        }
        if (familiar.getRole() == FamiliarRole.PROTECTOR) {
            return 10;
        }
        return 8;
    }

    @Override
    public void start(FamiliarEntity familiar) {
        if (targets.isEmpty()) return;
        familiar.freezeMovement();
        Mob primary = targets.get(0);
        familiar.getLookControl().setLookAt(primary);
        familiar.triggerAnim("wave", "wave");
        for (Mob mob : targets) {
            mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, effectDuration, 0, false, false));
            ParticleMethods.ParticleTrailEntityToEntity(familiar.level(), ModParticles.FAMILIAR_ATTENTION.get(), familiar, mob, 10);
        }
        familiar.level().playSound(null, familiar.blockPosition(),
                SoundEvents.ALLAY_ITEM_TAKEN, SoundSource.NEUTRAL, 0.6f, 1.3f);
    }

    @Override
    public void tick(FamiliarEntity familiar) {
        if (targets.isEmpty()) return;
        Mob primary = targets.get(0);
        if (!primary.isAlive()) return;

        familiar.freezeMovement();
        familiar.getLookControl().setLookAt(primary);
    }

    @Override
    public int getDuration(FamiliarEntity familiar) {
        return 40;
    }

    private boolean isBehindPlayer(Player player, Entity target) {
        Vec3 playerLook = player.getLookAngle().normalize();
        Vec3 toMob = target.position().subtract(player.position()).normalize();
        double dot = playerLook.dot(toMob);
        return dot < 0;
    }

    private boolean playerCanClearlySee(Player player, Mob mob) {
        if (!player.hasLineOfSight(mob)) {
            return false;
        }
        Vec3 playerLook = player.getLookAngle().normalize();
        Vec3 toMob = mob.position().subtract(player.position()).normalize();
        double dot = playerLook.dot(toMob);
        return dot > 0.6;
    }

    @Override
    public void stop(FamiliarEntity familiar) {
        targets.clear();
    }

    private int getMaxTargets(FamiliarEntity familiar) {
        if (familiar.getRole() == FamiliarRole.PROTECTOR) {
            return 1 + (familiar.getEnchantLevel() * 2);
        }
        return 1;
    }
}

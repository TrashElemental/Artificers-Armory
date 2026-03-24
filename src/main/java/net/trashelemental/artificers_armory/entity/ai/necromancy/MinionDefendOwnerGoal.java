package net.trashelemental.artificers_armory.entity.ai.necromancy;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Goal to let necromancy minions proactively defend their owner, with some randomization so not all minions will
 * go after the same target if there are multiple.
 */
public class MinionDefendOwnerGoal extends MinionTargetGoal {

    public MinionDefendOwnerGoal(Mob mob) {
        super(mob);
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = getOwner();

        if (mob.getRandom().nextFloat() > 0.25f) return false;
        if (owner == null) return false;

        LivingEntity currentTarget = mob.getTarget();
        if (currentTarget != null && currentTarget.isAlive()) return false;

        List<Mob> candidates = mob.level().getEntitiesOfClass(
                Mob.class, owner.getBoundingBox().inflate(12.0D),
                e -> e.isAlive() && e.getTarget() instanceof Mob target && isDefendableAlly(target, owner)
        );

        if (candidates.isEmpty()) return false;

        int minAttackers = Integer.MAX_VALUE;
        List<Mob> bestTargets = new ArrayList<>();

        for (Mob candidate : candidates) {
            int attackers = countAlliesTargeting(candidate, owner);

            if (attackers < minAttackers) {
                minAttackers = attackers;
                bestTargets.clear();
                bestTargets.add(candidate);
            } else if (attackers == minAttackers) {
                bestTargets.add(candidate);
            }
        }

        if (bestTargets.isEmpty()) return false;

        Collections.shuffle(bestTargets, new Random(mob.getUUID().hashCode() ^ mob.tickCount));
        mob.setTarget(bestTargets.get(0));
        return true;
    }

    @Override
    public void stop() {
        mob.setTarget(null);
        super.stop();
    }

    private boolean isAlly(Entity e, LivingEntity owner) {
        return e instanceof OwnableEntity ownable
                && ownable.getOwner() == owner;
    }

    private boolean isDefendableAlly(Entity ally, LivingEntity owner) {
        if (ally == owner) return true;

        if (ally instanceof OwnableEntity ownable) {
            return ownable.getOwner() == owner;
        }

        return false;
    }

    private int countAlliesTargeting(Mob target, LivingEntity owner) {
        return mob.level().getEntitiesOfClass(Mob.class, mob.getBoundingBox().inflate(12.0D),
                e -> isAlly(e, owner) && e.getTarget() == target).size();
    }
}
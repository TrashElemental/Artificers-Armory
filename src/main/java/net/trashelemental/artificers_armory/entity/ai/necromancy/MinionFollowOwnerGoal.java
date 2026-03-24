package net.trashelemental.artificers_armory.entity.ai.necromancy;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class MinionFollowOwnerGoal extends Goal {
    private final Mob mob;
    private final double speed;
    private final float stopDistance;
    private final float startDistance;

    public MinionFollowOwnerGoal(Mob mob, double speed, float startDistance, float stopDistance) {
        this.mob = mob;
        this.speed = speed;
        this.startDistance = startDistance;
        this.stopDistance = stopDistance;
    }

    @Override
    public boolean canUse() {
        if (!(mob instanceof OwnableEntity ownable)) return false;

        LivingEntity owner = ownable.getOwner();
        if (owner == null) return false;

        if (mob.getTarget() != null) return false;

        return mob.distanceTo(owner) > startDistance;
    }

    @Override
    public boolean canContinueToUse() {
        if (!(mob instanceof OwnableEntity ownable)) return false;

        LivingEntity owner = ownable.getOwner();
        if (owner == null) return false;

        return mob.distanceTo(owner) > stopDistance;
    }

    private int recalculationCooldown = 0;

    @Override
    public void tick() {
        if (recalculationCooldown-- > 0) return;
        recalculationCooldown = 20 + mob.getRandom().nextInt(20);

        LivingEntity owner = ((OwnableEntity) mob).getOwner();
        if (owner == null) return;

        double radius = stopDistance - 2.0;
        double angle = mob.getRandom().nextDouble() * Math.PI * 2;

        double x = owner.getX() + Math.cos(angle) * radius;
        double z = owner.getZ() + Math.sin(angle) * radius;

        mob.getNavigation().moveTo(x, owner.getY(), z, speed);
    }
}

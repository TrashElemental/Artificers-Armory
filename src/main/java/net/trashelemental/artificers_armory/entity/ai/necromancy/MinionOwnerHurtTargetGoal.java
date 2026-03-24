package net.trashelemental.artificers_armory.entity.ai.necromancy;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;

public class MinionOwnerHurtTargetGoal extends MinionTargetGoal {

    public MinionOwnerHurtTargetGoal(Mob mob) {
        super(mob);
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = getOwner();
        if (owner == null) return false;

        LivingEntity target = owner.getLastHurtMob();
        if (target == null || !target.isAlive()) return false;
        if (target instanceof OwnableEntity ownable && ownable.getOwner() == owner) return false;

        mob.setTarget(target);
        return true;
    }
}

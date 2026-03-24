package net.trashelemental.artificers_armory.entity.ai.necromancy;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;

public abstract class MinionTargetGoal extends TargetGoal {
    protected final Mob mob;

    protected MinionTargetGoal(Mob mob) {
        super(mob, false);
        this.mob = mob;
    }

    protected LivingEntity getOwner() {
        return mob instanceof OwnableEntity ownable ? ownable.getOwner() : null;
    }
}

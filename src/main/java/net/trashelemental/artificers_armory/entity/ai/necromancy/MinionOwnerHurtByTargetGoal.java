package net.trashelemental.artificers_armory.entity.ai.necromancy;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public class MinionOwnerHurtByTargetGoal extends MinionTargetGoal {

    public MinionOwnerHurtByTargetGoal(Mob mob) {
        super(mob);
    }

    @Override
    public boolean canUse() {
        LivingEntity owner = getOwner();
        if (owner == null) return false;

        LivingEntity attacker = owner.getLastHurtByMob();
        if (attacker == null || !attacker.isAlive()) return false;

        mob.setTarget(attacker);
        return true;
    }
}

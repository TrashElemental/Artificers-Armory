package net.trashelemental.artificers_armory.entity.ai.familiar.passive_behaviors;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.npc.Villager;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarTask;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;

import java.util.List;

public class WaveAtAllyTask implements FamiliarTask {

    private Mob target;

    @Override
    public boolean canRun(FamiliarEntity familiar) {
        target = null;
        LivingEntity owner = familiar.getOwner();
        if (owner == null) return false;

        List<Mob> validTargets = familiar.getAwareness().nearbyMobs.stream()
                .filter(mob -> isValidTarget(familiar, mob))
                .toList();

        if (validTargets.isEmpty()) return false;

        target = validTargets.get(familiar.getRandom().nextInt(validTargets.size()));
        return true;
    }

    private boolean isValidTarget(FamiliarEntity familiar, Mob mob) {

        LivingEntity owner = familiar.getOwner();
        if (owner == null) return false;
        if (mob == owner) return true;
        if (mob instanceof TamableAnimal tamable) {
            if (tamable.isTame() && owner.getUUID().equals(tamable.getOwnerUUID())) {
                return true;
            }
        }
        if (owner.isAlliedTo(mob)) {
            return true;
        }

        return mob instanceof IronGolem || mob instanceof Villager || mob instanceof SnowGolem || mob instanceof Allay;
    }

    @Override
    public int getWeight(FamiliarEntity familiar) {
        return 15;
    }

    @Override
    public void start(FamiliarEntity familiar) {
        if (target != null && target.isAlive()) {
            familiar.getLookControl().setLookAt(target);
        }
        familiar.freezeMovement();
        familiar.triggerAnim("wave", "wave");
    }

    @Override
    public void tick(FamiliarEntity familiar) {
        if (target != null && target.isAlive()) {
            familiar.getLookControl().setLookAt(target);
        }
        familiar.freezeMovement();
    }

    @Override
    public int getDuration(FamiliarEntity familiar) {
        return 20;
    }

    @Override
    public void stop(FamiliarEntity familiar) {

    }
}

package net.trashelemental.artificers_armory.entity.ai.familiar.passive_behaviors;

import net.minecraft.world.entity.Mob;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarAI;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarTask;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;

import java.util.List;

public class LookAtMobTask implements FamiliarTask {

    private Mob target;

    @Override
    public boolean canRun(FamiliarEntity familiar) {
        target = null;
        List<Mob> mobs = familiar.getAwareness().nearbyMobs;
        if (mobs.isEmpty()) return false;
        target = mobs.get(familiar.getRandom().nextInt(mobs.size()));
        return true;
    }

    @Override
    public int getWeight(FamiliarEntity familiar) {
        return 6;
    }

    @Override
    public void start(FamiliarEntity familiar) {
        if (target != null && target.isAlive()) {
            familiar.getLookControl().setLookAt(target);
        }
        familiar.freezeMovement();
        FamiliarAI.doLookAnimation(familiar);
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
        return 80 + familiar.getRandom().nextInt(40);
    }

    @Override
    public void stop(FamiliarEntity familiar) {

    }
}
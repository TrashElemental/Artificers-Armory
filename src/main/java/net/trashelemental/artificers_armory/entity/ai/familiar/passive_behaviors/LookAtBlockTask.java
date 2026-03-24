package net.trashelemental.artificers_armory.entity.ai.familiar.passive_behaviors;

import net.minecraft.core.BlockPos;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarTask;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarAI;
import net.trashelemental.artificers_armory.util.ModTags;

import java.util.List;

public class LookAtBlockTask implements FamiliarTask {

    private BlockPos target;

    @Override
    public boolean canRun(FamiliarEntity familiar) {
        target = null;
        List<BlockPos> blocks = familiar.getAwareness().interestingBlocks;
        List<BlockPos> valid = blocks.stream().filter(pos -> familiar.level().getBlockState(pos).is(ModTags.Blocks.FAMILIAR_INTERESTING)).toList();
        if (valid.isEmpty()) return false;
        target = valid.get(familiar.getRandom().nextInt(valid.size()));
        return true;
    }

    @Override
    public int getWeight(FamiliarEntity familiar) {
        return 6;
    }

    @Override
    public void start(FamiliarEntity familiar) {
        if (target != null) {
            familiar.getLookControl().setLookAt(target.getCenter());
        }
        familiar.freezeMovement();
        FamiliarAI.doLookAnimation(familiar);
    }

    @Override
    public void tick(FamiliarEntity familiar) {
        if (target == null) return;
        familiar.getLookControl().setLookAt(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5);
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

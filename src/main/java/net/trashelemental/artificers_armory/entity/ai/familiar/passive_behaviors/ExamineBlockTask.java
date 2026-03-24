package net.trashelemental.artificers_armory.entity.ai.familiar.passive_behaviors;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarAI;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarTask;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import net.trashelemental.artificers_armory.util.ModTags;

import java.util.List;

public class ExamineBlockTask implements FamiliarTask {

    private BlockPos target;
    private Vec3 hoverPos;
    private double orbitAngle = 0;
    private boolean startedExamining = false;
    private int tickCounter = 0;

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
        return 10;
    }

    @Override
    public void start(FamiliarEntity familiar) {
        if (target == null) return;
        tickCounter = 0;
        Vec3 mobPos = target.getCenter();
        hoverPos = mobPos.add((familiar.getRandom().nextDouble() - 0.5) * 2, 1.2, (familiar.getRandom().nextDouble() - 0.5) * 2);
        orbitAngle = familiar.getRandom().nextDouble() * 2 * Math.PI;
        startedExamining = false;
        familiar.setDeltaMovement(Vec3.ZERO);
        familiar.getNavigation().moveTo(hoverPos.x, hoverPos.y, hoverPos.z, 1.0);
    }

    @Override
    public void tick(FamiliarEntity familiar) {

        if (target == null) return;
        familiar.getLookControl().setLookAt(target.getCenter());
        double distToHover = familiar.position().distanceTo(hoverPos);

        if (!startedExamining) {
            if (distToHover > 0.3) {
                familiar.getNavigation().moveTo(hoverPos.x, hoverPos.y, hoverPos.z, 1.0);
                return;
            }
            startedExamining = true;
            familiar.getNavigation().stop();
            familiar.setDeltaMovement(Vec3.ZERO);
            FamiliarAI.doLookAnimation(familiar);
        }

        orbitAngle += 0.05;
        double orbitRadius = 1.0;
        double xOffset = Math.cos(orbitAngle) * orbitRadius;
        double zOffset = Math.sin(orbitAngle) * orbitRadius;
        double bobAmplitude = 0.2;
        double bobSpeed = 0.15;
        double yOffset = Math.sin(familiar.tickCount * bobSpeed) * bobAmplitude + 1.2;
        Vec3 targetPos = target.getCenter().add(xOffset, yOffset, zOffset);
        double dist = familiar.position().distanceTo(targetPos);

        if (dist > 0.1) {
            familiar.getNavigation().moveTo(targetPos.x, targetPos.y, targetPos.z, 1.0);
        }

        if (startedExamining) {
            tickCounter++;
            if (tickCounter >= 60) {
                familiar.getCurrentTask().stop(familiar);
            }
        }
    }

    @Override
    public int getDuration(FamiliarEntity familiar) {
        return 300;
    }

    @Override
    public void stop(FamiliarEntity familiar) {

    }
}

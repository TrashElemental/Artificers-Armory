package net.trashelemental.artificers_armory.entity.ai.familiar.passive_abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarTask;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.particle.ModParticles;

import java.util.ArrayList;
import java.util.List;

public class GrowCropsTask implements FamiliarTask {

    private BlockPos target;

    @Override
    public boolean canRun(FamiliarEntity familiar) {
        target = null;
        if (familiar.getLevel() < 2) return false;
        List<BlockPos> blocks = familiar.getAwareness().interestingBlocks;
        List<BlockPos> valid = new ArrayList<>();
        for (BlockPos pos : blocks) {
            if (isGrowable(familiar, pos)) {
                valid.add(pos);
            }
        }
        if (valid.isEmpty()) return false;
        target = valid.get(familiar.getRandom().nextInt(valid.size()));
        return true;
    }

    @Override
    public int getWeight(FamiliarEntity familiar) {
        return 3;
    }

    @Override
    public void start(FamiliarEntity familiar) {
        if (target == null) return;

        familiar.getLookControl().setLookAt(target.getCenter());
        familiar.triggerAnim("behavior", "minorSupport");
        familiar.level().playSound(null, familiar.blockPosition(), SoundEvents.ALLAY_ITEM_GIVEN, SoundSource.NEUTRAL, 0.3f, 1f);

        ParticleMethods.ParticleTrailBlockToEntity(familiar.level(), ParticleTypes.HAPPY_VILLAGER, target, familiar, 8);
        ParticleMethods.ParticlesAroundServerSide(familiar.level(), ParticleTypes.HAPPY_VILLAGER,
                target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, 10, 1.2);

        int radius = getRadius(familiar.getLevel());
        for (BlockPos pos : BlockPos.betweenClosed(target.offset(-radius, 0, -radius), target.offset(radius, 0, radius))) {
            growBlock(familiar, pos);
        }
    }

    @Override
    public void tick(FamiliarEntity familiar) {
        if (target == null) return;
        familiar.getLookControl().setLookAt(target.getCenter());
    }

    @Override
    public int getDuration(FamiliarEntity familiar) {
        return 40;
    }

    private boolean isGrowable(FamiliarEntity familiar, BlockPos pos) {
        BlockState state = familiar.level().getBlockState(pos);
        int level = familiar.getLevel();

        if (state.getBlock() instanceof CropBlock crop) {
            return !crop.isMaxAge(state);
        }
        if (state.getBlock() instanceof SweetBerryBushBlock) {
            return state.getValue(SweetBerryBushBlock.AGE) < 3;
        }
        if (state.getBlock() instanceof CaveVines) {
            return !state.getValue(CaveVines.BERRIES);
        }

        if (level >= 6 && state.getBlock() instanceof NetherWartBlock) {
            return state.getValue(NetherWartBlock.AGE) < 3;
        }

        if (level >= 6) {
            return state.is(Blocks.SMALL_AMETHYST_BUD) || state.is(Blocks.MEDIUM_AMETHYST_BUD) || state.is(Blocks.LARGE_AMETHYST_BUD);
        }

        return false;
    }

    private int getRadius(int level) {
        if (level >= 5) return 2;
        if (level >= 3) return 1;
        return 0;
    }

    private int getGrowth(int level) {
        return Math.min(3, Math.max(1, level-2));
    }

    private void growBlock(FamiliarEntity familiar, BlockPos pos) {
        if (!isGrowable(familiar, pos)) return;
        Level level = familiar.level();
        BlockState state = level.getBlockState(pos);
        int tier = familiar.getLevel();

        // Crops
        if (state.getBlock() instanceof CropBlock crop) {
            if (!crop.isMaxAge(state)) {
                int newAge = Math.min(crop.getMaxAge(), crop.getAge(state) + getGrowth(tier));
                level.setBlock(pos, crop.getStateForAge(newAge), 2);
            }
        }

        // Berries
        else if (state.getBlock() instanceof SweetBerryBushBlock) {
            int age = state.getValue(SweetBerryBushBlock.AGE);
            if (age < 3) {
                int newAge = Math.min(3, age + getGrowth(tier));
                level.setBlock(pos, state.setValue(SweetBerryBushBlock.AGE, newAge), 2);
            }
        }

        // Glow berries
        else if (state.getBlock() instanceof CaveVines) {
            if (!state.getValue(CaveVines.BERRIES)) {
                level.setBlock(pos, state.setValue(CaveVines.BERRIES, true), 2);
            }
        }

        // Nether wart
        else if (tier >= 6 && state.getBlock() instanceof NetherWartBlock) {
            int age = state.getValue(NetherWartBlock.AGE);
            if (age < 3) {
                int newAge = Math.min(3, age + getGrowth(tier));
                level.setBlock(pos, state.setValue(NetherWartBlock.AGE, newAge), 2);
            }
        }

        // Amethyst
        else if (tier >= 6) {
            if (state.is(Blocks.SMALL_AMETHYST_BUD)) {
                level.setBlock(pos, Blocks.MEDIUM_AMETHYST_BUD
                        .defaultBlockState()
                        .setValue(AmethystClusterBlock.FACING,
                                state.getValue(AmethystClusterBlock.FACING)), 3);
            }
            else if (state.is(Blocks.MEDIUM_AMETHYST_BUD)) {
                level.setBlock(pos, Blocks.LARGE_AMETHYST_BUD
                        .defaultBlockState()
                        .setValue(AmethystClusterBlock.FACING,
                                state.getValue(AmethystClusterBlock.FACING)), 3);
            }
            else if (state.is(Blocks.LARGE_AMETHYST_BUD)) {
                level.setBlock(pos, Blocks.AMETHYST_CLUSTER
                        .defaultBlockState()
                        .setValue(AmethystClusterBlock.FACING,
                                state.getValue(AmethystClusterBlock.FACING)), 3);
            }
        }
    }

    @Override
    public void stop(FamiliarEntity familiar) {

    }
}

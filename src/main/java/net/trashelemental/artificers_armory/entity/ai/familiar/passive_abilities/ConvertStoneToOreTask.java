package net.trashelemental.artificers_armory.entity.ai.familiar.passive_abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarTask;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.particle.ModParticles;

import java.util.ArrayList;
import java.util.List;

public class ConvertStoneToOreTask implements FamiliarTask {

    private BlockPos target;
    private int tickCounter = 0;

    @Override
    public boolean canRun(FamiliarEntity familiar) {
        target = null;
        if (familiar.getLevel() < 7) return false;
        if (familiar.getRandom().nextBoolean()) return false;
        List<BlockPos> blocks = familiar.getAwareness().interestingBlocks;
        List<BlockPos> valid = new ArrayList<>();
        for (BlockPos pos : blocks) {
            if (canBeConverted(familiar, pos)) {
                valid.add(pos);
            }
        }
        if (valid.isEmpty()) return false;
        target = valid.get(familiar.getRandom().nextInt(valid.size()));
        return true;
    }

    @Override
    public int getWeight(FamiliarEntity familiar) {
        return 1;
    }

    @Override
    public void start(FamiliarEntity familiar) {
        tickCounter = 0;
        if (target == null) return;
        familiar.freezeMovement();
        familiar.getLookControl().setLookAt(target.getCenter());
        familiar.triggerAnim("behavior", "support");
        familiar.level().playSound(null, familiar.blockPosition(), SoundEvents.ALLAY_ITEM_GIVEN, SoundSource.NEUTRAL, 1f, 1f);
    }

    @Override
    public void tick(FamiliarEntity familiar) {
        tickCounter++;
        if (target == null) return;
        familiar.freezeMovement();
        familiar.getLookControl().setLookAt(target.getCenter());
        if (tickCounter == 10) {
            convertBlock(familiar, target);
            ParticleMethods.ParticleTrailBlockToEntity(familiar.level(), ModParticles.FAMILIAR_ATTENTION.get(), target, familiar, 8);
            ParticleMethods.ParticlesAroundServerSide(familiar.level(), ModParticles.FAMILIAR_ATTENTION.get(),
                    target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, 10, 1.2);
        }
    }

    @Override
    public int getDuration(FamiliarEntity familiar) {
        return 20;
    }

    private boolean canBeConverted(FamiliarEntity familiar, BlockPos pos) {
        BlockState state = familiar.level().getBlockState(pos);
        return state.is(Blocks.STONE) || state.is(Blocks.DEEPSLATE);
    }

    private void convertBlock(FamiliarEntity familiar, BlockPos pos) {
        Level level = familiar.level();
        BlockState state = level.getBlockState(pos);

        Block newBlock = null;

        if (state.is(Blocks.STONE)) {
            newBlock = STONE_ORES[familiar.getRandom().nextInt(STONE_ORES.length)];
        }
        else if (state.is(Blocks.DEEPSLATE)) {
            newBlock = DEEPSLATE_ORES[familiar.getRandom().nextInt(DEEPSLATE_ORES.length)];
        }

        if (newBlock != null) {
            level.levelEvent(2001, pos, Block.getId(state));
            level.setBlock(pos, newBlock.defaultBlockState(), 3);
            level.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS);
        }
    }

    @Override
    public void stop(FamiliarEntity familiar) {

    }

    private static final Block[] STONE_ORES = {
            Blocks.COAL_ORE,
            Blocks.IRON_ORE,
            Blocks.COPPER_ORE,
            Blocks.GOLD_ORE,
            Blocks.REDSTONE_ORE,
            Blocks.LAPIS_ORE,
            Blocks.DIAMOND_ORE,
            Blocks.EMERALD_ORE
    };

    private static final Block[] DEEPSLATE_ORES = {
            Blocks.DEEPSLATE_COAL_ORE,
            Blocks.DEEPSLATE_IRON_ORE,
            Blocks.DEEPSLATE_COPPER_ORE,
            Blocks.DEEPSLATE_GOLD_ORE,
            Blocks.DEEPSLATE_REDSTONE_ORE,
            Blocks.DEEPSLATE_LAPIS_ORE,
            Blocks.DEEPSLATE_DIAMOND_ORE,
            Blocks.DEEPSLATE_EMERALD_ORE
    };
}

package net.trashelemental.artificers_armory.entity.ai.familiar.passive_abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarRole;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarTask;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.particle.ModParticles;

import java.util.ArrayList;
import java.util.List;

public class SpotLootTask implements FamiliarTask {
    private BlockPos target;

    @Override
    public boolean canRun(FamiliarEntity familiar) {
        target = null;
        if (familiar.getLevel() < 5) return false;
        if (!(familiar.getOwner() instanceof Player player)) return false;
        if (familiar.getRole() == FamiliarRole.BRUISER) return false;
        List<BlockPos> blocks = familiar.getAwareness().interestingBlocks;
        List<BlockPos> visible = new ArrayList<>();
        List<BlockPos> hidden = new ArrayList<>();

        for (BlockPos pos : blocks) {
            BlockState state = familiar.level().getBlockState(pos);
            BlockEntity be = familiar.level().getBlockEntity(pos);
            boolean isOre = state.is(Tags.Blocks.ORES);
            boolean isLootChest = be instanceof ChestBlockEntity && ChestBlockEntity.getOpenCount(familiar.level(), pos) == 0;

            if (!isOre && !isLootChest) continue;
            boolean visibleBlock = familiar.level().clip(new ClipContext(player.getEyePosition(), Vec3.atCenterOf(pos),
                            ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player)).getType() == HitResult.Type.MISS;

            if (visibleBlock) visible.add(pos);
            else hidden.add(pos);
        }

        List<BlockPos> pool = !hidden.isEmpty() ? hidden : visible;
        if (pool.isEmpty()) return false;
        target = pool.get(familiar.getRandom().nextInt(pool.size()));
        return true;
    }

    @Override
    public void start(FamiliarEntity familiar) {
        if (target == null) return;
        if (!(familiar.getOwner() instanceof Player player)) return;

        BlockState state = familiar.level().getBlockState(target);
        ParticleMethods.ParticleTrailBlockToEntity(familiar.level(), ModParticles.FAMILIAR_ATTENTION.get(), target, familiar, 8);
        ParticleMethods.ParticlesAroundServerSide(familiar.level(), ModParticles.FAMILIAR_ATTENTION.get(),
                target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, 10, 1.2);
        familiar.level().playSound(null, familiar.blockPosition(), SoundEvents.ALLAY_AMBIENT_WITH_ITEM, SoundSource.NEUTRAL, 0.5f, 1.2f);
        if (familiar.getLevel() >= 6 && state.is(Blocks.CHEST)) {
            int amplifier = familiar.getLevel() - 6;
            player.addEffect(new MobEffectInstance(MobEffects.LUCK, 200, amplifier, false, false));
        }

        familiar.getLookControl().setLookAt(target.getCenter());
        familiar.triggerAnim("behavior", "minorSupport");
    }

    @Override
    public int getDuration(FamiliarEntity familiar) { return 40; }

    @Override
    public void tick(FamiliarEntity familiar) {
        if (target == null) return;
        familiar.getLookControl().setLookAt(target.getCenter());
    }

    @Override
    public int getWeight(FamiliarEntity familiar) {
        if (familiar.getRole() == FamiliarRole.PRANKSTER) return 5;
        return 2;
    }

    @Override
    public void stop(FamiliarEntity familiar) {

    }
}

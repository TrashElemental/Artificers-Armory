package net.trashelemental.artificers_armory.util.event;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.trashelemental.artificers_armory.Config;
import net.trashelemental.artificers_armory.item.custom.FirebrandItem;

@Mod.EventBusSubscriber
public class FirebrandLightEvents {

    private static final String FIREBRAND_LIGHT_POS = "FirebrandLightPos";

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;
        Level level = player.level();
        if (level.isClientSide) return;
        if (!Config.FIREBRANDS_SHED_LIGHT.get()) return;

        boolean holdingFirebrand = player.getMainHandItem().getItem() instanceof FirebrandItem ||
                        player.getOffhandItem().getItem() instanceof FirebrandItem;

        CompoundTag data = player.getPersistentData();
        BlockPos currentPos = player.blockPosition();

        if (!holdingFirebrand) {
            removeLight(level, data);
            return;
        }

        BlockPos lastPos = data.contains(FIREBRAND_LIGHT_POS) ? BlockPos.of(data.getLong(FIREBRAND_LIGHT_POS)) : null;

        if (!currentPos.equals(lastPos)) {
            removeLight(level, data);

            BlockPos lightPos = placeLight(level, player);
            if (lightPos != null) {
                data.putLong(FIREBRAND_LIGHT_POS, lightPos.asLong());
            }
        }
    }

    public static BlockPos placeLight(Level level, Player player) {
        BlockPos lightPos = findValidLightPos(level, player);
        if (lightPos != null) {
            level.setBlock(lightPos, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, 15), Block.UPDATE_CLIENTS);
        }
        return lightPos;
    }

    public static void removeLight(Level level, CompoundTag data) {
        if (!data.contains(FIREBRAND_LIGHT_POS)) return;

        BlockPos pos = BlockPos.of(data.getLong(FIREBRAND_LIGHT_POS));
        if (level.getBlockState(pos).is(Blocks.LIGHT)) {
            level.removeBlock(pos, false);
        }
        data.remove(FIREBRAND_LIGHT_POS);
    }

    public static BlockPos findValidLightPos(Level level, Entity entity) {
        BlockPos base = entity.blockPosition();

        BlockPos[] candidates = new BlockPos[] {
                base, base.above(), base.below(), base.north(), base.south(),
                base.east(), base.west(), base.above().north(),
                base.above().south(), base.above().east(), base.above().west()
        };

        for (BlockPos pos : candidates) {
            BlockState state = level.getBlockState(pos);

            if (state.isAir()) {
                return pos;
            }
        }

        return null;
    }
}

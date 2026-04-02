package net.trashelemental.artificers_armory.entity.ai.familiar;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod;
import net.trashelemental.artificers_armory.entity.ai.familiar.triggered_abilities.FetchItemTask;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import net.trashelemental.artificers_armory.util.ModTags;

import java.util.ArrayList;
import java.util.List;

public class FamiliarAI {

    public static void doSpawnAnimation(FamiliarEntity familiar) {
        if (familiar.level().isClientSide) return;
        if (familiar.isSpawning()) return;

        familiar.setSpawning(true);
        familiar.setSpawnTimer(10);

        familiar.setNoAi(true);
        familiar.setInvulnerable(true);
        familiar.setDeltaMovement(Vec3.ZERO);

        int choice = familiar.getRandom().nextInt(3);

        switch (choice) {
            case 0 -> familiar.triggerAnim("popIn", "POP_IN");
            case 1 -> familiar.triggerAnim("teleportIn", "TELEPORT_IN");
            case 2 -> familiar.triggerAnim("riseIn", "RISE_IN");
        }
    }

    public static void despawnFromLifespan(FamiliarEntity familiar) {
        if (familiar.level().isClientSide) return;
        if (familiar.isDespawning()) return;

        familiar.setNoAi(true);
        familiar.setInvulnerable(true);
        familiar.setDeltaMovement(Vec3.ZERO);
        if (familiar.getOwner() instanceof Player owner) {
            familiar.lookAt(owner, 360F, 360F);
        }

        familiar.setDespawning(true);
        familiar.setDespawnTimer(30);
        int choice = familiar.getRandom().nextInt(3);

        switch (choice) {
            case 0 -> familiar.triggerAnim("popOut", "POP_OUT");
            case 1 -> familiar.triggerAnim("teleportOut", "TELEPORT_OUT");
            case 2 -> familiar.triggerAnim("riseOut", "RISE_OUT");
        }

        if (familiar.getOwner() instanceof Player player) {
            player.getPersistentData().remove("artificers_armory_familiar");
        }
    }

    public static void doIdleAnimation(FamiliarEntity familiar) {
        List<String> animations = new ArrayList<>();

        animations.add("look");
        animations.add("think");
        animations.add("spin");
        animations.add("curious");
        animations.add("idea_fail");

        if (familiar.getRole() == FamiliarRole.BRUISER) {
            animations.add("shadow_box");
            animations.add("shadow_box");
            animations.add("shadow_box");
        }

        if (familiar.getRole() == FamiliarRole.PRANKSTER) {
            animations.add("scheme");
            animations.add("scheme");
            animations.add("scheme");
        }

        String chosen = animations.get(familiar.getRandom().nextInt(animations.size()));
        familiar.triggerAnim("idle", chosen);
    }

    public static void doLookAnimation(FamiliarEntity familiar) {
        int choice = familiar.getRandom().nextInt(3);

        switch (choice) {
            case 0 -> familiar.triggerAnim("idle", "inspect");
            case 1 -> familiar.triggerAnim("idle", "think");
            case 2 -> familiar.triggerAnim("idle", "curious");
        }
    }

    private static int randomBetween(FamiliarEntity familiar, int min, int max) {
        return familiar.getRandom().nextInt(max - min + 1) + min;
    }

    public static void doIdle(FamiliarEntity familiar) {
        RandomSource random = familiar.getRandom();
        evaluateSurroundings(familiar);

        float abilityChance = 0.4f;
        if (familiar.getRole() == FamiliarRole.HEALER || familiar.getRole() == FamiliarRole.PRANKSTER) abilityChance = 0.6f;

        if (random.nextFloat() < abilityChance) {
            if (FamiliarEventHandlers.tryPassiveAbility(familiar)) {
                familiar.setIdleCooldown(randomBetween(familiar, 200, 300)); // 10–15 sec
                return;
            }
        }

        if (random.nextFloat() < 0.5f) {
            if (FamiliarEventHandlers.tryPassiveBehavior(familiar)) {
                familiar.setIdleCooldown(randomBetween(familiar, 160, 240)); // 8–12 sec
                return;
            }
        }

        doIdleAnimation(familiar);
        familiar.setIdleCooldown(randomBetween(familiar, 160, 240));
    }

    public static void doCombatAbility(FamiliarEntity familiar) {
        RandomSource random = familiar.getRandom();
        evaluateSurroundings(familiar);

        float abilityChance = 0.5f;
        if (familiar.getRole() != FamiliarRole.NONE) abilityChance = 0.8f;

        if (random.nextFloat() < abilityChance) {
            if (FamiliarEventHandlers.tryCombatAbility(familiar)) {
                if (familiar.getRole() == FamiliarRole.HEALER || familiar.getRole() == FamiliarRole.PRANKSTER || familiar.getRole() == FamiliarRole.BRUISER) {
                    familiar.setCombatCooldown(randomBetween(familiar, 80, 120)); // 4-6 sec
                } else familiar.setCombatCooldown(randomBetween(familiar, 100, 140)); // 5-7 sec
            }
        } else {
            familiar.setCombatCooldown(randomBetween(familiar, 80, 120)); // 4-6 sec
         }
    }

    public static void evaluateSurroundings(FamiliarEntity familiar) {
        FamiliarAwareness awareness = new FamiliarAwareness();
        Level level = familiar.level();
        AABB scanBox = familiar.getBoundingBox().inflate(16);
        awareness.nearbyMobs = level.getEntitiesOfClass(Mob.class, scanBox);
        awareness.nearbyItems = level.getEntitiesOfClass(ItemEntity.class, scanBox);
        awareness.nearbyPlayers = level.getEntitiesOfClass(Player.class, scanBox);
        BlockPos center = familiar.blockPosition();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int x = -5; x <= 5; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -5; z <= 5; z++) {
                    pos.set(center.getX() + x, center.getY() + y, center.getZ() + z);
                    BlockState state = level.getBlockState(pos);

                    if (isFamiliarInteresting(state)) {
                        awareness.interestingBlocks.add(pos.immutable());
                    }
                }
            }
        }

        familiar.setAwareness(awareness);
    }

    public static boolean isFamiliarInteresting(BlockState state) {
        Block block = state.getBlock();

        return state.is(ModTags.Blocks.FAMILIAR_INTERESTING)
                || state.is(Blocks.STONE)
                || state.is(Blocks.DEEPSLATE)
                || state.is(Blocks.SMALL_AMETHYST_BUD)
                || state.is(Blocks.MEDIUM_AMETHYST_BUD)
                || state.is(Blocks.LARGE_AMETHYST_BUD)
                || block instanceof CropBlock cropBlock && !(cropBlock.isMaxAge(state))
                || block instanceof SweetBerryBushBlock
                || block instanceof CaveVines
                || block instanceof NetherWartBlock;
    }

    public static void forceFetchItem(FamiliarEntity familiar, ItemEntity item) {
        familiar.cancelCurrentTask();
        familiar.startTask(new FetchItemTask(item), 300);
    }

}

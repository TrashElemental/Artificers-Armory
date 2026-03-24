package net.trashelemental.artificers_armory.entity.ai.familiar;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.trashelemental.artificers_armory.Config;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class CollectItemsGoal extends Goal {
    private final FamiliarEntity familiar;
    private ItemEntity targetItem;
    private BlockPos targetContainerPos;

    private int ticksTryingToReachItem = 0;
    private static final int MAX_TICKS_TO_REACH = 100;
    private int containerSearchCooldown = 0;

    public CollectItemsGoal(FamiliarEntity familiar) {
        this.familiar = familiar;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!familiar.canCollect() || familiar.isIgnoringItems()) return false;
        if (familiar.getItemPickupCooldown() > 0) return false;
        if (!Config.FAMILIAR_COLLECT_ITEMS.get()) return false;

        if (familiar.hasCarriedItem()) {
            return true;
        }

        targetItem = findNearbyItem();
        return targetItem != null;
    }

    @Override
    public boolean canContinueToUse() {
        return (targetItem != null && !targetItem.isRemoved()) || familiar.hasCarriedItem();
    }

    @Override
    public void tick() {
        if (familiar.isIgnoringItems()) return;

        if (targetItem != null && !targetItem.isRemoved()) {
            Vec3 itemPos = targetItem.position().add(0, 0.1, 0);
            familiar.getNavigation().moveTo(itemPos.x, itemPos.y, itemPos.z, 1.0);

            ticksTryingToReachItem++;

            double distanceSq = familiar.distanceToSqr(targetItem);
            if (distanceSq < 3) {
                familiar.setCarriedItem(targetItem.getItem().copy());
                familiar.playSound(SoundEvents.ITEM_PICKUP);
                targetItem.discard();
                targetItem = null;
                ticksTryingToReachItem = 0;
            } else if (ticksTryingToReachItem > MAX_TICKS_TO_REACH) {
                targetItem = findNearbyItem();
                ticksTryingToReachItem = 0;
            }
            return;
        }

        if (familiar.isDepositingItems() && familiar.hasCarriedItem()) {

            if (containerSearchCooldown-- <= 0 || targetContainerPos == null) {
                targetContainerPos = findNearbyContainer();
                containerSearchCooldown = 40;
            }

            if (targetContainerPos == null) {
                return;
            }

            BlockEntity be = familiar.level().getBlockEntity(targetContainerPos);
            if (!(be instanceof Container)) {
                targetContainerPos = null;
                return;
            }

            Vec3 targetVec = Vec3.atCenterOf(targetContainerPos);
            familiar.getNavigation().moveTo(targetVec.x, targetVec.y, targetVec.z, 1.0);

            if (familiar.blockPosition().closerThan(targetContainerPos, 1.5)) {
                BlockEntity blockEntity = familiar.level().getBlockEntity(targetContainerPos);
                if (blockEntity instanceof Container container) {
                    if (addItemToContainer(container, familiar.getCarriedItem())) {
                        familiar.setCarriedItem(ItemStack.EMPTY);
                        targetContainerPos = null;
                        familiar.playSound(SoundEvents.CHEST_OPEN);
                    }
                }
            }
        }
    }

    @Override
    public void stop() {
        familiar.setItemPickupCooldown(100);
        super.stop();
    }

    private ItemEntity findNearbyItem() {
        List<ItemEntity> items = familiar.level().getEntitiesOfClass(ItemEntity.class,
                familiar.getBoundingBox().inflate(8), item -> !item.getItem().isEmpty());

        if (items.isEmpty()) return null;
        items.sort(Comparator.comparingDouble(familiar::distanceToSqr));
        return items.get(0);
    }

    private BlockPos findNearbyContainer() {
        for (BlockPos pos : BlockPos.betweenClosed(
                familiar.blockPosition().offset(-5, -1, -5),
                familiar.blockPosition().offset(5, 1, 5))) {

            BlockEntity be = familiar.level().getBlockEntity(pos);
            if (be instanceof Container) {
                return pos;
            }
        }
        return null;
    }

    public static boolean addItemToContainer(Container container, ItemStack stack) {
        if (stack.isEmpty()) return false;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack slotStack = container.getItem(i);
            if (!slotStack.isEmpty() && ItemStack.isSameItem(slotStack, stack)
                    && ItemStack.isSameItemSameTags(slotStack, stack)) {

                int space = slotStack.getMaxStackSize() - slotStack.getCount();
                if (space > 0) {
                    int toAdd = Math.min(space, stack.getCount());
                    slotStack.grow(toAdd);
                    stack.shrink(toAdd);
                    container.setChanged();
                    if (stack.isEmpty()) return true;
                }
            }
        }

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack slotStack = container.getItem(i);
            if (slotStack.isEmpty()) {
                container.setItem(i, stack.copy());
                stack.setCount(0);
                container.setChanged();
                return true;
            }
        }

        return stack.isEmpty();
    }
}
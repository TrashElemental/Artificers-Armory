package net.trashelemental.artificers_armory.entity.ai.familiar.triggered_abilities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarTask;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;

public class FetchItemTask implements FamiliarTask {

    private ItemEntity targetItem;
    private boolean gotItem;
    private boolean finished;

    public FetchItemTask(ItemEntity item) {
        this.targetItem = item;
    }

    @Override
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public boolean canRun(FamiliarEntity familiar) {
        if (finished) return false;
        return (targetItem != null && targetItem.isAlive()) || familiar.hasCarriedItem();
    }

    @Override
    public int getWeight(FamiliarEntity familiar) {
        return 100;
    }

    @Override
    public void start(FamiliarEntity familiar) {
        gotItem = false;
        finished = false;
        familiar.freezeMovement();
        familiar.noPhysics = true;
    }

    @Override
    public void tick(FamiliarEntity familiar) {

        if (!familiar.hasCarriedItem() && (targetItem == null || !targetItem.isAlive())) {
            finished = true;
            return;
        }

        if (!familiar.hasCarriedItem()) {
            familiar.getNavigation().moveTo(targetItem, 1.2);
            if (familiar.distanceToSqr(targetItem) < 2) {
                familiar.setCarriedItem(targetItem.getItem().copy());
                targetItem.discard();
                familiar.getNavigation().stop();
                gotItem = true;
                familiar.level().playSound(null, familiar.blockPosition(),
                        SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL, 0.6f, 1.2f);
            }
        }

        if (familiar.hasCarriedItem()) {
            Player owner = (Player) familiar.getOwner();
            if (owner == null) return;
            familiar.getNavigation().moveTo(owner, 1.4);
            if (familiar.distanceToSqr(owner) < 3) {
                owner.getInventory().placeItemBackInInventory(familiar.getCarriedItem());
                familiar.setCarriedItem(ItemStack.EMPTY);
                familiar.level().playSound(null, familiar.blockPosition(),
                        SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL, 0.6f, 1.2f);
                finished = true;
            }
        }
    }

    @Override
    public int getDuration(FamiliarEntity familiar) {
        return 200;
    }

    @Override
    public void stop(FamiliarEntity familiar) {
        familiar.noPhysics = false;
        targetItem = null;

        if (!gotItem && !finished) {
            ParticleMethods.ParticlesAroundServerSide(familiar.level(), ParticleTypes.SMOKE,
                    familiar.getX(), familiar.getEyeY(), familiar.getZ(), 4, 1.2);
            finished = true;
        }
    }
}

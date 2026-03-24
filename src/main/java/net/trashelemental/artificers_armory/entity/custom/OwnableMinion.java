package net.trashelemental.artificers_armory.entity.custom;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.trashelemental.artificers_armory.item.custom.SpiritCandleItem;

import javax.annotation.Nullable;

public interface OwnableMinion extends OwnableEntity {
    void setOwner(@Nullable LivingEntity owner);

    default boolean checkOwnerHoldingCandle() {
        LivingEntity owner = getOwner();
        if (!(owner instanceof Player player)) return false;

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        boolean holdingCandle = mainHand.getItem() instanceof SpiritCandleItem || offHand.getItem() instanceof SpiritCandleItem;

        return holdingCandle;
    }

    boolean isOwnedBy(LivingEntity entity);
}

package net.trashelemental.artificers_armory.magic.enchantments.custom.firebrand;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.trashelemental.artificers_armory.item.custom.FirebrandItem;

public class SoulBlazeEnchantment extends Enchantment {
    public SoulBlazeEnchantment(EquipmentSlot... slots) {
        super(Rarity.COMMON, EnchantmentCategory.BREAKABLE, slots);
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean isTradeable() {
        return false;
    }

    @Override
    public boolean canEnchant(ItemStack item) {
        return item.getItem() instanceof FirebrandItem;
    }

}

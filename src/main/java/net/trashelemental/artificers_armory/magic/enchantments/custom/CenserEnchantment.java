package net.trashelemental.artificers_armory.magic.enchantments.custom;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.trashelemental.artificers_armory.Config;
import net.trashelemental.artificers_armory.item.custom.BlightItem;
import net.trashelemental.artificers_armory.item.custom.CenserItem;

public class CenserEnchantment extends Enchantment {
    public CenserEnchantment(EquipmentSlot... slots) {
        super(Rarity.COMMON, EnchantmentCategory.BREAKABLE, slots);
    }

    @Override
    public boolean isTradeable() {
        return Config.ENCHANTMENTS_TRADABLE.get();
    }

    @Override
    public boolean isAllowedOnBooks() {
        return Config.ENCHANTMENTS_ON_BOOKS.get();
    }


    @Override
    public boolean canEnchant(ItemStack item) {
        return item.getItem() instanceof CenserItem;
    }
}

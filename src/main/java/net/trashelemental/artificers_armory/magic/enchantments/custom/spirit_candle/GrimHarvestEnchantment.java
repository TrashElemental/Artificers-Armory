package net.trashelemental.artificers_armory.magic.enchantments.custom.spirit_candle;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.trashelemental.artificers_armory.Config;
import net.trashelemental.artificers_armory.item.custom.SpiritCandleItem;

public class GrimHarvestEnchantment extends Enchantment {
    public GrimHarvestEnchantment(EquipmentSlot... slots) {
        super(Rarity.COMMON, EnchantmentCategory.BREAKABLE, slots);
    }

    @Override
    public int getMaxLevel() {
        return 3;
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
        return item.getItem() instanceof SpiritCandleItem;
    }

}

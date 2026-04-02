package net.trashelemental.artificers_armory.magic.enchantments.custom.firebrand;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.trashelemental.artificers_armory.Config;
import net.trashelemental.artificers_armory.item.custom.FirebrandItem;
import net.trashelemental.artificers_armory.magic.enchantments.ModEnchantments;

public class WarmingLightEnchantment extends Enchantment {
    public WarmingLightEnchantment(EquipmentSlot... slots) {
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
        return item.getItem() instanceof FirebrandItem;
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        if (other == ModEnchantments.CHARGE_BLAST.get() ||
                other == ModEnchantments.FLAMETHROWER.get() ||
                other == ModEnchantments.WARMING_LIGHT.get()) {
            return false;
        }
        return super.checkCompatibility(other);
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}

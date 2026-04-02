package net.trashelemental.artificers_armory.magic.enchantments.custom.chime;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.trashelemental.artificers_armory.Config;
import net.trashelemental.artificers_armory.item.custom.ChimeItem;
import net.trashelemental.artificers_armory.magic.enchantments.ModEnchantments;

public class FamiliarRoleEnchantment extends Enchantment {
    public FamiliarRoleEnchantment(EquipmentSlot... slots) {
        super(Rarity.COMMON, EnchantmentCategory.BREAKABLE, slots);
    }

    @Override
    public int getMaxLevel() {
        return 2;
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
        return item.getItem() instanceof ChimeItem;
    }

    @Override
    protected boolean checkCompatibility(Enchantment other) {
        if (other instanceof FamiliarRoleEnchantment) {
            return false;
        }
        return super.checkCompatibility(other);
    }

}

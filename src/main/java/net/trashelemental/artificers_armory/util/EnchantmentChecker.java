package net.trashelemental.artificers_armory.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.Map;

public class EnchantmentChecker {

    public int checkEnchantmentLevel(ItemStack stack, TagKey<Enchantment> tag) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);

        if (enchantments.isEmpty()) {
            return 0;
        }

        int totalLevel = 0;

        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int level = entry.getValue();

            if (BuiltInRegistries.ENCHANTMENT.wrapAsHolder(enchantment).is(tag)) {
                totalLevel += level;
            }
        }

        return totalLevel;
    }
}


package net.trashelemental.artificers_armory.util.spirit_candle;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record EquipmentRules(
        float equipChance,
        float enchantChance,
        int minEnchantLevel,
        int maxEnchantLevel,
        List<EquipmentEntry> equipmentPool
) {}


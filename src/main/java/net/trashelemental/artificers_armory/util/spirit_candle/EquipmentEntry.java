package net.trashelemental.artificers_armory.util.spirit_candle;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public record EquipmentEntry(
        ItemStack stack,
        EquipmentSlot slot,
        Set<ResourceLocation> allowedEntities
) {
    public boolean canEquip(Mob mob) {
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
        return allowedEntities == null || allowedEntities.isEmpty() || allowedEntities.contains(id);
    }
}

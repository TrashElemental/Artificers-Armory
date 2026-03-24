package net.trashelemental.artificers_armory.util.spirit_candle;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

public record SummonEntry(
        ResourceLocation entityId,
        float weight
) {}

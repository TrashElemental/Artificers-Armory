package net.trashelemental.artificers_armory.util.spirit_candle;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class SpiritCandleTier {

    private final ResourceLocation id;
    private final List<SummonEntry> summonTable;
    private final EquipmentRules equipmentRule;

    public SpiritCandleTier(
            ResourceLocation id,
            List<SummonEntry> summonTable,
            EquipmentRules equipmentRule
    ) {
        this.id = id;
        this.summonTable = summonTable;
        this.equipmentRule = equipmentRule;
    }

    public EquipmentRules getEquipmentRule() {
        return equipmentRule;
    }

    public EntityType<? extends Mob> getRandomSummon(RandomSource random) {
        float total = 0f;
        for (SummonEntry entry : summonTable) {
            total += entry.weight();
        }

        float roll = random.nextFloat() * total;
        for (SummonEntry entry : summonTable) {
            roll -= entry.weight();
            if (roll <= 0f) {
                return resolve(entry);
            }
        }

        return resolve(summonTable.get(0));
    }

    @SuppressWarnings("unchecked")
    private EntityType<? extends Mob> resolve(SummonEntry entry) {
        EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(entry.entityId());
        if (type == null) {
            throw new IllegalStateException("Unknown entity for Spirit Candle summon: " + entry.entityId());
        }
        return (EntityType<? extends Mob>) type;
    }
}

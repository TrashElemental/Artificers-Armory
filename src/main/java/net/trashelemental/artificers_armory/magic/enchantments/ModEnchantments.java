package net.trashelemental.artificers_armory.magic.enchantments;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.magic.enchantments.custom.chime.FamiliarRoleEnchantment;
import net.trashelemental.artificers_armory.magic.enchantments.custom.firebrand.ChargeBlastEnchantment;
import net.trashelemental.artificers_armory.magic.enchantments.custom.firebrand.FlamethrowerEnchantment;
import net.trashelemental.artificers_armory.magic.enchantments.custom.firebrand.SoulBlazeEnchantment;
import net.trashelemental.artificers_armory.magic.enchantments.custom.firebrand.WarmingLightEnchantment;
import net.trashelemental.artificers_armory.magic.enchantments.custom.spirit_candle.*;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> REGISTRY = DeferredRegister.create(Registries.ENCHANTMENT, ArtificersArmory.MOD_ID);

    // Firebrand
    public static final RegistryObject<Enchantment> CHARGE_BLAST = REGISTRY.register("charge_blast", ChargeBlastEnchantment::new);
    public static final RegistryObject<Enchantment> FLAMETHROWER = REGISTRY.register("flamethrower", FlamethrowerEnchantment::new);
    public static final RegistryObject<Enchantment> WARMING_LIGHT = REGISTRY.register("warming_light", WarmingLightEnchantment::new);
    public static final RegistryObject<Enchantment> SOUL_BLAZE = REGISTRY.register("soul_blaze", SoulBlazeEnchantment::new);

    // Spirit Candle
    public static final RegistryObject<Enchantment> GRIM_HARVEST = REGISTRY.register("grim_harvest", GrimHarvestEnchantment::new);
    public static final RegistryObject<Enchantment> HEX = REGISTRY.register("hex", HexEnchantment::new);
    public static final RegistryObject<Enchantment> FOCUS = REGISTRY.register("focus", FocusEnchantment::new);
    public static final RegistryObject<Enchantment> LIFEDRAIN = REGISTRY.register("lifedrain", LifedrainEnchantment::new);
    public static final RegistryObject<Enchantment> PHYLACTERY = REGISTRY.register("phylactery", PhylacteryEnchantment::new);

    //Chime
    public static final RegistryObject<Enchantment> PROTECTOR = REGISTRY.register("protector", FamiliarRoleEnchantment::new);
    public static final RegistryObject<Enchantment> HEALER = REGISTRY.register("healer", FamiliarRoleEnchantment::new);
    public static final RegistryObject<Enchantment> PRANKSTER = REGISTRY.register("prankster", FamiliarRoleEnchantment::new);
    public static final RegistryObject<Enchantment> BRUISER = REGISTRY.register("bruiser", FamiliarRoleEnchantment::new);


    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}

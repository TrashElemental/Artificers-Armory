package net.trashelemental.artificers_armory.magic.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.magic.effects.custom.EmpoweredEffect;
import net.trashelemental.artificers_armory.magic.effects.custom.GrimHarvestEffect;
import net.trashelemental.artificers_armory.magic.effects.custom.SoulBurnEffect;
import net.trashelemental.artificers_armory.magic.effects.custom.TeamworkEffect;

public class ModMobEffects {

    public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ArtificersArmory.MOD_ID);


    public static final RegistryObject<MobEffect> SOUL_BURN = REGISTRY.register("soul_burn", SoulBurnEffect::new);
    public static final RegistryObject<MobEffect> EMPOWERED = REGISTRY.register("empowered", EmpoweredEffect::new);
    public static final RegistryObject<MobEffect> GRIM_HARVEST = REGISTRY.register("grim_harvest", GrimHarvestEffect::new);

    public static final RegistryObject<MobEffect> TEAMWORK = REGISTRY.register("teamwork", TeamworkEffect::new);


    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}

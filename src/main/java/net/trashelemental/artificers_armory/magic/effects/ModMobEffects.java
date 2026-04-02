package net.trashelemental.artificers_armory.magic.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.magic.effects.custom.*;

public class ModMobEffects {

    public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ArtificersArmory.MOD_ID);


    public static final RegistryObject<MobEffect> SOUL_BURN = REGISTRY.register("soul_burn", SoulBurnEffect::new);
    public static final RegistryObject<MobEffect> EMPOWERED = REGISTRY.register("empowered", EmpoweredEffect::new);
    public static final RegistryObject<MobEffect> GRIM_HARVEST = REGISTRY.register("grim_harvest", GrimHarvestEffect::new);

    public static final RegistryObject<MobEffect> BLESSING = REGISTRY.register("blessing", BlessingEffect::new);

    public static final RegistryObject<MobEffect> PLAGUE = REGISTRY.register("plague", PlagueEffect::new);
    public static final RegistryObject<MobEffect> PESTILENCE = REGISTRY.register("pestilence", PestilenceEffect::new);
    public static final RegistryObject<MobEffect> BLACK_DEATH = REGISTRY.register("black_death", BlackDeathEffect::new);
    public static final RegistryObject<MobEffect> DELIRIUM = REGISTRY.register("delirium", DeliriumEffect::new);


    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}

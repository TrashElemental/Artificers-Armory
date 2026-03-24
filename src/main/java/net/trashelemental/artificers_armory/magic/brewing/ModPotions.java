package net.trashelemental.artificers_armory.magic.brewing;

import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.trashelemental.artificers_armory.ArtificersArmory;

public class ModPotions {
    public static final DeferredRegister<Potion> REGISTRY = DeferredRegister.create(ForgeRegistries.POTIONS, ArtificersArmory.MOD_ID);





    public static void register(IEventBus eventBus) {
        REGISTRY.register(eventBus);
    }
}

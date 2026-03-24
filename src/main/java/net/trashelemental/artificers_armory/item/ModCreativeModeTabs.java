package net.trashelemental.artificers_armory.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.trashelemental.artificers_armory.ArtificersArmory;

public class ModCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ArtificersArmory.MOD_ID);

    public static final RegistryObject<CreativeModeTab> ARTIFICERS_ARMORY_TAB = CREATIVE_MODE_TABS.register("artificers_armory_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.STONE_FIREBRAND.get()))
                    .title(Component.translatable("creativetab.artificers_armory"))
                    .displayItems((itemDisplayParameters, output) -> {

                        output.accept(ModItems.WOOD_FIREBRAND.get());
                        output.accept(ModItems.STONE_FIREBRAND.get());
                        output.accept(ModItems.COPPER_FIREBRAND.get());
                        output.accept(ModItems.IRON_FIREBRAND.get());
                        output.accept(ModItems.GOLD_FIREBRAND.get());
                        output.accept(ModItems.DIAMOND_FIREBRAND.get());
                        output.accept(ModItems.NETHERITE_FIREBRAND.get());

                        output.accept(ModItems.WOOD_SPIRIT_CANDLE.get());
                        output.accept(ModItems.STONE_SPIRIT_CANDLE.get());
                        output.accept(ModItems.COPPER_SPIRIT_CANDLE.get());
                        output.accept(ModItems.IRON_SPIRIT_CANDLE.get());
                        output.accept(ModItems.GOLD_SPIRIT_CANDLE.get());
                        output.accept(ModItems.DIAMOND_SPIRIT_CANDLE.get());
                        output.accept(ModItems.NETHERITE_SPIRIT_CANDLE.get());

                        output.accept(ModItems.WOOD_CHIME.get());
                        output.accept(ModItems.STONE_CHIME.get());
                        output.accept(ModItems.COPPER_CHIME.get());
                        output.accept(ModItems.IRON_CHIME.get());
                        output.accept(ModItems.GOLD_CHIME.get());
                        output.accept(ModItems.DIAMOND_CHIME.get());
                        output.accept(ModItems.NETHERITE_CHIME.get());

                    })
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}

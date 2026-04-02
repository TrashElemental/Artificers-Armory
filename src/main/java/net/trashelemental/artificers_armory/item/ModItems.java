package net.trashelemental.artificers_armory.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.item.custom.*;
import net.trashelemental.artificers_armory.util.spirit_candle.SpiritCandleTiers;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ArtificersArmory.MOD_ID);

    public static final RegistryObject<Item> WOOD_FIREBRAND = ITEMS.register("firebrand_wood",
            () -> new FirebrandItem(new Item.Properties().durability(50),
                    0, 1, 20, 3, -2.8F, false, 10));
    public static final RegistryObject<Item> STONE_FIREBRAND = ITEMS.register("firebrand_stone",
            () -> new FirebrandItem(new Item.Properties().durability(100),
                    0, 2, 20, 3, -2.8F, false, 10));
    public static final RegistryObject<Item> COPPER_FIREBRAND = ITEMS.register("firebrand_copper",
            () -> new FirebrandItem(new Item.Properties().durability(130),
                    1, 3, 20, 5, -2.8F, true, 15));
    public static final RegistryObject<Item> IRON_FIREBRAND = ITEMS.register("firebrand_iron",
            () -> new FirebrandItem(new Item.Properties().durability(190),
                    2, 4, 20, 5, -2.8F, false, 15));
    public static final RegistryObject<Item> GOLD_FIREBRAND = ITEMS.register("firebrand_gold",
            () -> new FirebrandItem(new Item.Properties().durability(40),
                    2, 4, 10, 5, -2F, true, 30));
    public static final RegistryObject<Item> DIAMOND_FIREBRAND = ITEMS.register("firebrand_diamond",
            () -> new FirebrandItem(new Item.Properties().durability(1200),
                    3, 5, 20, 7, -2.8F, false, 20));
    public static final RegistryObject<Item> NETHERITE_FIREBRAND = ITEMS.register("firebrand_netherite",
            () -> new FirebrandItem(new Item.Properties().durability(1560),
                    4, 6, 20, 9, -2.8F, true, 25));


    public static final RegistryObject<Item> WOOD_CENSER = ITEMS.register("censer_wood",
            () -> new CenserItem(new Item.Properties().durability(60),
                    4, -2.8F, 5, 0, 3, 100, 10));
    public static final RegistryObject<Item> STONE_CENSER = ITEMS.register("censer_stone",
            () -> new CenserItem(new Item.Properties().durability(120),
                    5, -2.8F, 6, 0, 4, 100, 10));
    public static final RegistryObject<Item> COPPER_CENSER = ITEMS.register("censer_copper",
            () -> new CenserItem(new Item.Properties().durability(150),
                    5, -2.8F, 6, 0, 5, 100, 15));
    public static final RegistryObject<Item> IRON_CENSER = ITEMS.register("censer_iron",
            () -> new CenserItem(new Item.Properties().durability(210),
                    6, -2.8F, 7, 1, 5, 100, 15));
    public static final RegistryObject<Item> GOLD_CENSER = ITEMS.register("censer_gold",
            () -> new CenserItem(new Item.Properties().durability(50),
                    5, -2.6F, 8, 1, 7, 50, 30));
    public static final RegistryObject<Item> DIAMOND_CENSER = ITEMS.register("censer_diamond",
            () -> new CenserItem(new Item.Properties().durability(1200),
                    7, -2.8F, 9, 2, 8, 100, 20));
    public static final RegistryObject<Item> NETHERITE_CENSER = ITEMS.register("censer_netherite",
            () -> new CenserItem(new Item.Properties().durability(1560),
                    8, -2.8F, 10, 3, 10, 100, 25));


    public static final RegistryObject<Item> WOOD_BLIGHT = ITEMS.register("blight_wood",
            () -> new BlightItem(new Item.Properties().durability(60),
                    2, -2.2F, 1, 5, 80, 10, 0.2f));
    public static final RegistryObject<Item> STONE_BLIGHT = ITEMS.register("blight_stone",
            () -> new BlightItem(new Item.Properties().durability(120),
                    3, -2.2F,  1, 7, 80, 10, 0.3f));
    public static final RegistryObject<Item> COPPER_BLIGHT = ITEMS.register("blight_copper",
            () -> new BlightItem(new Item.Properties().durability(150),
                    3, -2.2F, 2, 7, 80, 15, 0.4f));
    public static final RegistryObject<Item> IRON_BLIGHT = ITEMS.register("blight_iron",
            () -> new BlightItem(new Item.Properties().durability(210),
                    4, -2.2F,  2, 10, 80, 15, 0.5f));
    public static final RegistryObject<Item> GOLD_BLIGHT = ITEMS.register("blight_gold",
            () -> new BlightItem(new Item.Properties().durability(50),
                    3, -2F, 3, 11, 40, 30, 0.6f));
    public static final RegistryObject<Item> DIAMOND_BLIGHT = ITEMS.register("blight_diamond",
            () -> new BlightItem(new Item.Properties().durability(1200),
                    5, -2.2F, 3, 12, 80, 20, 0.7f));
    public static final RegistryObject<Item> NETHERITE_BLIGHT = ITEMS.register("blight_netherite",
            () -> new BlightItem(new Item.Properties().durability(1560),
                    6, -2.2F,  4, 13, 80, 25, 0.8f));


    public static final RegistryObject<Item> WOOD_SPIRIT_CANDLE = ITEMS.register("spirit_candle_wood",
            () -> new SpiritCandleItem(new Item.Properties().durability(50),
                    2, SpiritCandleTiers.WOOD, 60, 6, 1, 1, 10));
    public static final RegistryObject<Item> STONE_SPIRIT_CANDLE = ITEMS.register("spirit_candle_stone",
            () -> new SpiritCandleItem(new Item.Properties().durability(100),
                    3, SpiritCandleTiers.STONE, 60, 8, 1, 2, 10));
    public static final RegistryObject<Item> COPPER_SPIRIT_CANDLE = ITEMS.register("spirit_candle_copper",
            () -> new SpiritCandleItem(new Item.Properties().durability(130),
                    3, SpiritCandleTiers.COPPER, 60, 8, 2, 3, 15));
    public static final RegistryObject<Item> IRON_SPIRIT_CANDLE = ITEMS.register("spirit_candle_iron",
            () -> new SpiritCandleItem(new Item.Properties().durability(190),
                    4, SpiritCandleTiers.IRON, 60, 10, 2, 4, 15));
    public static final RegistryObject<Item> GOLD_SPIRIT_CANDLE = ITEMS.register("spirit_candle_gold",
            () -> new SpiritCandleItem(new Item.Properties().durability(40),
                    4, SpiritCandleTiers.GOLD, 30, 10, 3, 6, 30));
    public static final RegistryObject<Item> DIAMOND_SPIRIT_CANDLE = ITEMS.register("spirit_candle_diamond",
            () -> new SpiritCandleItem(new Item.Properties().durability(1200),
                    5, SpiritCandleTiers.DIAMOND, 60, 14, 3, 6,20));
    public static final RegistryObject<Item> NETHERITE_SPIRIT_CANDLE = ITEMS.register("spirit_candle_netherite",
            () -> new SpiritCandleItem(new Item.Properties().durability(1560),
                    6, SpiritCandleTiers.NETHERITE, 60, 16, 4, 7,25));


    public static final RegistryObject<Item> WOOD_CHIME = ITEMS.register("chime_wood",
            () -> new ChimeItem(new Item.Properties().durability(50),
                    120, 1, 20, 2, 1, 20, 10));
    public static final RegistryObject<Item> STONE_CHIME = ITEMS.register("chime_stone",
            () -> new ChimeItem(new Item.Properties().durability(100),
                    180, 2, 25, 2, 1, 20, 10));
    public static final RegistryObject<Item> COPPER_CHIME = ITEMS.register("chime_copper",
            () -> new ChimeItem(new Item.Properties().durability(130),
                    240, 3, 25, 3, 2, 20, 15));
    public static final RegistryObject<Item> IRON_CHIME = ITEMS.register("chime_iron",
            () -> new ChimeItem(new Item.Properties().durability(190),
                    300, 4, 30, 3, 2, 20, 15));
    public static final RegistryObject<Item> GOLD_CHIME = ITEMS.register("chime_gold",
            () -> new ChimeItem(new Item.Properties().durability(40),
                    360, 5, 30, 4, 3, 10, 30));
    public static final RegistryObject<Item> DIAMOND_CHIME = ITEMS.register("chime_diamond",
            () -> new ChimeItem(new Item.Properties().durability(1200),
                    600, 6, 40, 4, 4, 20, 20));
    public static final RegistryObject<Item> NETHERITE_CHIME = ITEMS.register("chime_netherite",
            () -> new ChimeItem(new Item.Properties().durability(1560),
                    1200, 7, 50, 5, 5, 20, 25));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}

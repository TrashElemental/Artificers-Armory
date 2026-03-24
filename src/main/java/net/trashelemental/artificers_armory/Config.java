package net.trashelemental.artificers_armory;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = ArtificersArmory.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    //Demo Related



    //Non-Demo
    public static final ForgeConfigSpec.BooleanValue FIREBRANDS_SHED_LIGHT = BUILDER
            .comment("Should firebrands and their projectiles emit light when held or fired?")
            .define("Firebrands emit light:", true);
    public static final ForgeConfigSpec.BooleanValue ENCHANT_TOOLTIPS = BUILDER
            .comment("Should items have additional tooltips when enchanted with certain enchantments?")
            .define("Additional enchantment tooltips:", true);
    public static final ForgeConfigSpec.BooleanValue FAMILIAR_COLLECT_ITEMS = BUILDER
            .comment("Should the familiar summoned by Chimes be able to interact with dropped items? This can also be toggled by interacting with the familiar using a Chime.")
            .define("Familiar interacts with items:", true);
    public static final ForgeConfigSpec.BooleanValue SUPPORT_OTHER_PLAYERS = BUILDER
            .comment("Should certain effects from this mod that are meant to support allies affect other players? By default, these effects will try not to support players that you are actively fighting.")
            .define("Support effects apply to non-hostile players:", true);


    public static final ForgeConfigSpec.BooleanValue ISS_COMPAT = BUILDER
            .comment("Should certain items should boost certain attributes from Iron's Spells And Spellbooks when held in either hand?")
            .define("Boost IS&S Attributes:", true);
    public static final ForgeConfigSpec.DoubleValue ISS_BOOST_AMOUNT = BUILDER
            .comment("How much of a boost should items from this mod provide to specific spell schools from Iron's Spells And Spellbooks if the above config is enabled?")
            .defineInRange("Percentage Boost:", 0.2, 0.01, 1);
//    public static final ForgeConfigSpec.BooleanValue TOOLS_BOOST_AN_ATTRIBUTES = BUILDER
//            .comment("Controls whether certain items should boost certain attributes from Ars Nouveau when held in either hand.")
//            .define("Boost Ars Nouveau Attributes:", true);




    static final ForgeConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        if (event.getConfig().getSpec() == Config.SPEC) {
        }

    }
}

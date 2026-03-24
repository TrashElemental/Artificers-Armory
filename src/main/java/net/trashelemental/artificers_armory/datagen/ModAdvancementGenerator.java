package net.trashelemental.artificers_armory.datagen;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.item.ModItems;

import java.util.function.Consumer;

public class ModAdvancementGenerator implements ForgeAdvancementProvider.AdvancementGenerator {

    @Override
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> writer, ExistingFileHelper existingFileHelper) {

        String namespace = ArtificersArmory.MOD_ID;

        Advancement ParentAdvancement = Advancement.Builder.advancement()
                .display(
                        ModItems.STONE_FIREBRAND.get(),
                        Component.translatable("advancements.artificers_armory.parent.title"),
                        Component.translatable("advancements.artificers_armory.parent.description"),
                        new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"),
                        FrameType.TASK,
                        true,  // show toast
                        false,  // announce to chat
                        false  // hidden
                )
                .addCriterion("impossible", new ImpossibleTrigger.TriggerInstance())
                .save(writer, new ResourceLocation(namespace,"parent_advancement"), existingFileHelper);

        Advancement FirebrandShoot = Advancement.Builder.advancement()
                .display(
                        ModItems.WOOD_FIREBRAND.get(), // Icon
                        Component.translatable("advancements.artificers_armory.firebrand_shoot.title"), // Title
                        Component.translatable("advancements.artificers_armory.firebrand_shoot.description"), // Description
                        new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"), // Background
                        FrameType.GOAL, // Frame Type
                        true, // Show Toast
                        true, // Announce to Chat
                        false // Hidden
                )
                .parent(ParentAdvancement)
                .addCriterion("impossible", new ImpossibleTrigger.TriggerInstance())
                .save(writer, new ResourceLocation(namespace, "firebrand_shoot"), existingFileHelper);

        Advancement NetheriteFirebrandGet = Advancement.Builder.advancement()
                .display(
                        ModItems.NETHERITE_FIREBRAND.get(), // Icon
                        Component.translatable("advancements.artificers_armory.netherite_firebrand_get.title"), // Title
                        Component.translatable("advancements.artificers_armory.netherite_firebrand_get.description"), // Description
                        new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"), // Background
                        FrameType.GOAL, // Frame Type
                        true, // Show Toast
                        true, // Announce to Chat
                        false // Hidden
                )
                .parent(FirebrandShoot)
                .addCriterion("has_netherite_firebrand", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.NETHERITE_FIREBRAND.get()))
                .save(writer, new ResourceLocation(namespace, "netherite_firebrand_get"), existingFileHelper);

        Advancement SpiritCandleSummon = Advancement.Builder.advancement()
                .display(
                        ModItems.WOOD_SPIRIT_CANDLE.get(), // Icon
                        Component.translatable("advancements.artificers_armory.spirit_candle_raise_minion.title"), // Title
                        Component.translatable("advancements.artificers_armory.spirit_candle_raise_minion.description"), // Description
                        new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"), // Background
                        FrameType.GOAL, // Frame Type
                        true, // Show Toast
                        true, // Announce to Chat
                        false // Hidden
                )
                .parent(ParentAdvancement)
                .addCriterion("impossible", new ImpossibleTrigger.TriggerInstance())
                .save(writer, new ResourceLocation(namespace, "spirit_candle_raise_minion"), existingFileHelper);

//        Advancement NervousMinions = Advancement.Builder.advancement()
//                .display(
//                        Items.SKELETON_SKULL, // Icon
//                        Component.translatable("advancements.artificers_armory.nervous_minion.title"), // Title
//                        Component.translatable("advancements.artificers_armory.nervous_minion.description"), // Description
//                        new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"), // Background
//                        FrameType.GOAL, // Frame Type
//                        true, // Show Toast
//                        true, // Announce to Chat
//                        false // Hidden
//                )
//                .parent(SpiritCandleSummon)
//                .addCriterion("impossible", new ImpossibleTrigger.TriggerInstance())
//                .save(writer, new ResourceLocation(namespace, "nervous_minion"), existingFileHelper);

        Advancement NetheriteSpiritCandleGet = Advancement.Builder.advancement()
                .display(
                        ModItems.NETHERITE_SPIRIT_CANDLE.get(), // Icon
                        Component.translatable("advancements.artificers_armory.netherite_spirit_candle_get.title"), // Title
                        Component.translatable("advancements.artificers_armory.netherite_spirit_candle_get.description"), // Description
                        new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"), // Background
                        FrameType.GOAL, // Frame Type
                        true, // Show Toast
                        true, // Announce to Chat
                        false // Hidden
                )
                .parent(SpiritCandleSummon)
                .addCriterion("has_netherite_spirit_candle", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.NETHERITE_SPIRIT_CANDLE.get()))
                .save(writer, new ResourceLocation(namespace, "netherite_spirit_candle_get"), existingFileHelper);


        Advancement ChimeSummon = Advancement.Builder.advancement()
                .display(
                        ModItems.WOOD_CHIME.get(), // Icon
                        Component.translatable("advancements.artificers_armory.chime_summon.title"), // Title
                        Component.translatable("advancements.artificers_armory.chime_summon.description"), // Description
                        new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"), // Background
                        FrameType.GOAL, // Frame Type
                        true, // Show Toast
                        true, // Announce to Chat
                        false // Hidden
                )
                .parent(ParentAdvancement)
                .addCriterion("impossible", new ImpossibleTrigger.TriggerInstance())
                .save(writer, new ResourceLocation(namespace, "chime_summon"), existingFileHelper);

        Advancement NetheriteChimeGet = Advancement.Builder.advancement()
                .display(
                        ModItems.NETHERITE_CHIME.get(), // Icon
                        Component.translatable("advancements.artificers_armory.netherite_chime_get.title"), // Title
                        Component.translatable("advancements.artificers_armory.netherite_chime_get.description"), // Description
                        new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"), // Background
                        FrameType.GOAL, // Frame Type
                        true, // Show Toast
                        true, // Announce to Chat
                        false // Hidden
                )
                .parent(ChimeSummon)
                .addCriterion("has_netherite_chime", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.NETHERITE_CHIME.get()))
                .save(writer, new ResourceLocation(namespace, "netherite_chime_get"), existingFileHelper);

        Advancement ProtectorSummon = Advancement.Builder.advancement()
                .display(
                        ModItems.IRON_CHIME.get(), // Icon
                        Component.translatable("advancements.artificers_armory.protector_summon.title"), // Title
                        Component.translatable("advancements.artificers_armory.protector_summon.description"), // Description
                        new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"), // Background
                        FrameType.GOAL, // Frame Type
                        true, // Show Toast
                        true, // Announce to Chat
                        false // Hidden
                )
                .parent(ChimeSummon)
                .addCriterion("impossible", new ImpossibleTrigger.TriggerInstance())
                .save(writer, new ResourceLocation(namespace, "protector_summon"), existingFileHelper);

        Advancement HealerSummon = Advancement.Builder.advancement()
                .display(
                        ModItems.IRON_CHIME.get(), // Icon
                        Component.translatable("advancements.artificers_armory.healer_summon.title"), // Title
                        Component.translatable("advancements.artificers_armory.healer_summon.description"), // Description
                        new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"), // Background
                        FrameType.GOAL, // Frame Type
                        true, // Show Toast
                        true, // Announce to Chat
                        false // Hidden
                )
                .parent(ChimeSummon)
                .addCriterion("impossible", new ImpossibleTrigger.TriggerInstance())
                .save(writer, new ResourceLocation(namespace, "healer_summon"), existingFileHelper);

        Advancement PranksterSummon = Advancement.Builder.advancement()
                .display(
                        ModItems.IRON_CHIME.get(), // Icon
                        Component.translatable("advancements.artificers_armory.prankster_summon.title"), // Title
                        Component.translatable("advancements.artificers_armory.prankster_summon.description"), // Description
                        new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"), // Background
                        FrameType.GOAL, // Frame Type
                        true, // Show Toast
                        true, // Announce to Chat
                        false // Hidden
                )
                .parent(ChimeSummon)
                .addCriterion("impossible", new ImpossibleTrigger.TriggerInstance())
                .save(writer, new ResourceLocation(namespace, "prankster_summon"), existingFileHelper);

        Advancement BruiserSummon = Advancement.Builder.advancement()
                .display(
                        ModItems.IRON_CHIME.get(), // Icon
                        Component.translatable("advancements.artificers_armory.bruiser_summon.title"), // Title
                        Component.translatable("advancements.artificers_armory.bruiser_summon.description"), // Description
                        new ResourceLocation("textures/gui/advancements/backgrounds/stone.png"), // Background
                        FrameType.GOAL, // Frame Type
                        true, // Show Toast
                        true, // Announce to Chat
                        false // Hidden
                )
                .parent(ChimeSummon)
                .addCriterion("impossible", new ImpossibleTrigger.TriggerInstance())
                .save(writer, new ResourceLocation(namespace, "bruiser_summon"), existingFileHelper);
    }
}

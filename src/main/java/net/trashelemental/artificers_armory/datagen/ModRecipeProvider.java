package net.trashelemental.artificers_armory.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.item.ModItems;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {

        // Firebrand
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.WOOD_FIREBRAND.get())
                .pattern("c  ")
                .pattern("b  ")
                .pattern("a  ")
                .define('a', Items.STICK)
                .define('b', Items.LEATHER)
                .define('c', Items.TORCH)
                .unlockedBy(getHasName(Items.TORCH), has(Items.TORCH))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STONE_FIREBRAND.get())
                .pattern("c  ")
                .pattern("b  ")
                .pattern("a  ")
                .define('a', Items.STICK)
                .define('b', Items.COBBLESTONE)
                .define('c', Items.TORCH)
                .unlockedBy(getHasName(Items.COBBLESTONE), has(Items.COBBLESTONE))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COPPER_FIREBRAND.get())
                .pattern("c  ")
                .pattern("b  ")
                .pattern("a  ")
                .define('a', Items.STICK)
                .define('b', Items.COPPER_INGOT)
                .define('c', Items.TORCH)
                .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.IRON_FIREBRAND.get())
                .pattern("c  ")
                .pattern("b  ")
                .pattern("a  ")
                .define('a', Items.STICK)
                .define('b', Items.IRON_INGOT)
                .define('c', Items.TORCH)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GOLD_FIREBRAND.get())
                .pattern("c  ")
                .pattern("b  ")
                .pattern("a  ")
                .define('a', Items.STICK)
                .define('b', Items.GOLD_INGOT)
                .define('c', Items.TORCH)
                .unlockedBy(getHasName(Items.GOLD_INGOT), has(Items.GOLD_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.DIAMOND_FIREBRAND.get())
                .pattern("c  ")
                .pattern("b  ")
                .pattern("a  ")
                .define('a', Items.STICK)
                .define('b', Items.DIAMOND)
                .define('c', Items.TORCH)
                .unlockedBy(getHasName(Items.DIAMOND), has(Items.DIAMOND))
                .save(pWriter);

        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                        Ingredient.of(ModItems.DIAMOND_FIREBRAND.get()),
                        Ingredient.of(Items.NETHERITE_INGOT),
                        RecipeCategory.MISC,
                        ModItems.NETHERITE_FIREBRAND.get()
                )
                .unlocks("has_diamond_firebrand", has(ModItems.DIAMOND_FIREBRAND.get()))
                .save(pWriter, new ResourceLocation(ArtificersArmory.MOD_ID, "netherite_firebrand"));


        // Spirit Candle
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.WOOD_SPIRIT_CANDLE.get())
                .pattern(" d ")
                .pattern("cbe")
                .pattern(" a ")
                .define('a', Items.STICK)
                .define('b', ItemTags.PLANKS)
                .define('c', Items.ROTTEN_FLESH)
                .define('d', Items.STRING)
                .define('e', Items.BONE)
                .unlockedBy(getHasName(Items.ROTTEN_FLESH), has(Items.ROTTEN_FLESH))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STONE_SPIRIT_CANDLE.get())
                .pattern(" d ")
                .pattern("cbe")
                .pattern(" a ")
                .define('a', Items.STICK)
                .define('b', Items.COBBLESTONE)
                .define('c', Items.ROTTEN_FLESH)
                .define('d', Items.STRING)
                .define('e', Items.BONE)
                .unlockedBy(getHasName(Items.COBBLESTONE), has(Items.COBBLESTONE))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COPPER_SPIRIT_CANDLE.get())
                .pattern(" d ")
                .pattern("cbe")
                .pattern(" a ")
                .define('a', Items.STICK)
                .define('b', Items.COPPER_INGOT)
                .define('c', Items.ROTTEN_FLESH)
                .define('d', Items.STRING)
                .define('e', Items.BONE)
                .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.IRON_SPIRIT_CANDLE.get())
                .pattern(" d ")
                .pattern("cbe")
                .pattern(" a ")
                .define('a', Items.STICK)
                .define('b', Items.IRON_INGOT)
                .define('c', Items.ROTTEN_FLESH)
                .define('d', Items.STRING)
                .define('e', Items.BONE)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GOLD_SPIRIT_CANDLE.get())
                .pattern(" d ")
                .pattern("cbe")
                .pattern(" a ")
                .define('a', Items.STICK)
                .define('b', Items.GOLD_INGOT)
                .define('c', Items.ROTTEN_FLESH)
                .define('d', Items.STRING)
                .define('e', Items.BONE)
                .unlockedBy(getHasName(Items.GOLD_INGOT), has(Items.GOLD_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.DIAMOND_SPIRIT_CANDLE.get())
                .pattern(" d ")
                .pattern("cbe")
                .pattern(" a ")
                .define('a', Items.STICK)
                .define('b', Items.DIAMOND)
                .define('c', Items.ROTTEN_FLESH)
                .define('d', Items.STRING)
                .define('e', Items.BONE)
                .unlockedBy(getHasName(Items.DIAMOND), has(Items.DIAMOND))
                .save(pWriter);

        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                        Ingredient.of(ModItems.DIAMOND_SPIRIT_CANDLE.get()),
                        Ingredient.of(Items.NETHERITE_INGOT),
                        RecipeCategory.MISC,
                        ModItems.NETHERITE_SPIRIT_CANDLE.get()
                )
                .unlocks("has_diamond_spirit_candle", has(ModItems.DIAMOND_SPIRIT_CANDLE.get()))
                .save(pWriter, new ResourceLocation(ArtificersArmory.MOD_ID, "netherite_spirit_candle"));


        // Chime
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.WOOD_CHIME.get())
                .pattern("bcb")
                .pattern(" b ")
                .pattern(" a ")
                .define('a', Items.STICK)
                .define('b', ItemTags.PLANKS)
                .define('c', ItemTags.BUTTONS)
                .unlockedBy(getHasName(Items.OAK_PLANKS), has(Items.OAK_PLANKS))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STONE_CHIME.get())
                .pattern("bcb")
                .pattern(" b ")
                .pattern(" a ")
                .define('a', Items.STICK)
                .define('b', Items.COBBLESTONE)
                .define('c', ItemTags.BUTTONS)
                .unlockedBy(getHasName(Items.COBBLESTONE), has(Items.COBBLESTONE))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COPPER_CHIME.get())
                .pattern("bcb")
                .pattern(" b ")
                .pattern(" a ")
                .define('a', Items.STICK)
                .define('b', Items.COPPER_INGOT)
                .define('c', ItemTags.BUTTONS)
                .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.IRON_CHIME.get())
                .pattern("bcb")
                .pattern(" b ")
                .pattern(" a ")
                .define('a', Items.STICK)
                .define('b', Items.IRON_INGOT)
                .define('c', ItemTags.BUTTONS)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GOLD_CHIME.get())
                .pattern("bcb")
                .pattern(" b ")
                .pattern(" a ")
                .define('a', Items.STICK)
                .define('b', Items.GOLD_INGOT)
                .define('c', ItemTags.BUTTONS)
                .unlockedBy(getHasName(Items.GOLD_INGOT), has(Items.GOLD_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.DIAMOND_CHIME.get())
                .pattern("bcb")
                .pattern(" b ")
                .pattern(" a ")
                .define('a', Items.STICK)
                .define('b', Items.DIAMOND)
                .define('c', ItemTags.BUTTONS)
                .unlockedBy(getHasName(Items.DIAMOND), has(Items.DIAMOND))
                .save(pWriter);

        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                        Ingredient.of(ModItems.DIAMOND_CHIME.get()),
                        Ingredient.of(Items.NETHERITE_INGOT),
                        RecipeCategory.MISC,
                        ModItems.NETHERITE_CHIME.get()
                )
                .unlocks("has_diamond_chime", has(ModItems.DIAMOND_CHIME.get()))
                .save(pWriter, new ResourceLocation(ArtificersArmory.MOD_ID, "netherite_chime"));


        // Blight
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.WOOD_BLIGHT.get())
                .pattern("bbc")
                .pattern(" a ")
                .pattern("a  ")
                .define('a', Items.STICK)
                .define('b', ItemTags.PLANKS)
                .define('c', Items.SPIDER_EYE)
                .unlockedBy(getHasName(Items.OAK_PLANKS), has(Items.OAK_PLANKS))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STONE_BLIGHT.get())
                .pattern("bbc")
                .pattern(" a ")
                .pattern("a  ")
                .define('a', Items.STICK)
                .define('b', Items.COBBLESTONE)
                .define('c', Items.SPIDER_EYE)
                .unlockedBy(getHasName(Items.COBBLESTONE), has(Items.COBBLESTONE))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COPPER_BLIGHT.get())
                .pattern("bbc")
                .pattern(" a ")
                .pattern("a  ")
                .define('a', Items.STICK)
                .define('b', Items.COPPER_INGOT)
                .define('c', Items.SPIDER_EYE)
                .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.IRON_BLIGHT.get())
                .pattern("bbc")
                .pattern(" a ")
                .pattern("a  ")
                .define('a', Items.STICK)
                .define('b', Items.IRON_INGOT)
                .define('c', Items.SPIDER_EYE)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GOLD_BLIGHT.get())
                .pattern("bbc")
                .pattern(" a ")
                .pattern("a  ")
                .define('a', Items.STICK)
                .define('b', Items.GOLD_INGOT)
                .define('c', Items.SPIDER_EYE)
                .unlockedBy(getHasName(Items.GOLD_INGOT), has(Items.GOLD_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.DIAMOND_BLIGHT.get())
                .pattern("bbc")
                .pattern(" a ")
                .pattern("a  ")
                .define('a', Items.STICK)
                .define('b', Items.DIAMOND)
                .define('c', Items.SPIDER_EYE)
                .unlockedBy(getHasName(Items.DIAMOND), has(Items.DIAMOND))
                .save(pWriter);

        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                        Ingredient.of(ModItems.DIAMOND_BLIGHT.get()),
                        Ingredient.of(Items.NETHERITE_INGOT),
                        RecipeCategory.MISC,
                        ModItems.NETHERITE_BLIGHT.get()
                )
                .unlocks("has_diamond_blight", has(ModItems.DIAMOND_BLIGHT.get()))
                .save(pWriter, new ResourceLocation(ArtificersArmory.MOD_ID, "netherite_blight"));


        // Censer
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.WOOD_CENSER.get())
                .pattern(" b ")
                .pattern("c c")
                .pattern("a  ")
                .define('a', Items.STICK)
                .define('b', Items.STRING)
                .define('c', ItemTags.PLANKS)
                .unlockedBy(getHasName(Items.OAK_PLANKS), has(Items.OAK_PLANKS))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STONE_CENSER.get())
                .pattern(" b ")
                .pattern("c c")
                .pattern("a  ")
                .define('a', Items.STICK)
                .define('b', Items.STRING)
                .define('c', Items.COBBLESTONE)
                .unlockedBy(getHasName(Items.COBBLESTONE), has(Items.COBBLESTONE))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COPPER_CENSER.get())
                .pattern(" b ")
                .pattern("c d")
                .pattern("a  ")
                .define('a', Items.STICK)
                .define('b', Items.STRING)
                .define('c', Items.COPPER_INGOT)
                .define('d', Items.COPPER_BLOCK)
                .unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.IRON_CENSER.get())
                .pattern(" b ")
                .pattern("c d")
                .pattern("a  ")
                .define('a', Items.STICK)
                .define('b', Items.CHAIN)
                .define('c', Items.IRON_INGOT)
                .define('d', Items.IRON_BLOCK)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GOLD_CENSER.get())
                .pattern(" b ")
                .pattern("c d")
                .pattern("a  ")
                .define('a', Items.STICK)
                .define('b', Items.CHAIN)
                .define('c', Items.GOLD_INGOT)
                .define('d', Items.GOLD_BLOCK)
                .unlockedBy(getHasName(Items.GOLD_INGOT), has(Items.GOLD_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.DIAMOND_CENSER.get())
                .pattern(" b ")
                .pattern("c d")
                .pattern("a  ")
                .define('a', Items.STICK)
                .define('b', Items.CHAIN)
                .define('c', Items.DIAMOND)
                .define('d', Items.DIAMOND_BLOCK)
                .unlockedBy(getHasName(Items.DIAMOND), has(Items.DIAMOND))
                .save(pWriter);

        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE),
                        Ingredient.of(ModItems.DIAMOND_CENSER.get()),
                        Ingredient.of(Items.NETHERITE_INGOT),
                        RecipeCategory.MISC,
                        ModItems.NETHERITE_CENSER.get()
                )
                .unlocks("has_diamond_censer", has(ModItems.DIAMOND_CENSER.get()))
                .save(pWriter, new ResourceLocation(ArtificersArmory.MOD_ID, "netherite_censer"));


    }
}




package net.trashelemental.artificers_armory.compat.JEI;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.item.ModItems;

import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ArtificersArmory.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {

        registration.addIngredientInfo(List.of(
                        new ItemStack(ModItems.WOOD_FIREBRAND.get()),
                        new ItemStack(ModItems.STONE_FIREBRAND.get()),
                        new ItemStack(ModItems.COPPER_FIREBRAND.get()),
                        new ItemStack(ModItems.IRON_FIREBRAND.get()),
                        new ItemStack(ModItems.GOLD_FIREBRAND.get()),
                        new ItemStack(ModItems.DIAMOND_FIREBRAND.get()),
                        new ItemStack(ModItems.NETHERITE_FIREBRAND.get())),
                VanillaTypes.ITEM_STACK, Component.translatable("jei.artificers_armory.firebrand_info"));

        registration.addIngredientInfo(List.of(
                        new ItemStack(ModItems.WOOD_SPIRIT_CANDLE.get()),
                        new ItemStack(ModItems.STONE_SPIRIT_CANDLE.get()),
                        new ItemStack(ModItems.COPPER_SPIRIT_CANDLE.get()),
                        new ItemStack(ModItems.IRON_SPIRIT_CANDLE.get()),
                        new ItemStack(ModItems.GOLD_SPIRIT_CANDLE.get()),
                        new ItemStack(ModItems.DIAMOND_SPIRIT_CANDLE.get()),
                        new ItemStack(ModItems.NETHERITE_SPIRIT_CANDLE.get())),
                VanillaTypes.ITEM_STACK, Component.translatable("jei.artificers_armory.spirit_candle_info"));

        registration.addIngredientInfo(List.of(
                        new ItemStack(ModItems.WOOD_CHIME.get()),
                        new ItemStack(ModItems.STONE_CHIME.get()),
                        new ItemStack(ModItems.COPPER_CHIME.get()),
                        new ItemStack(ModItems.IRON_CHIME.get()),
                        new ItemStack(ModItems.GOLD_CHIME.get()),
                        new ItemStack(ModItems.DIAMOND_CHIME.get()),
                        new ItemStack(ModItems.NETHERITE_CHIME.get())),
                VanillaTypes.ITEM_STACK, Component.translatable("jei.artificers_armory.chime_info"));

        registration.addIngredientInfo(List.of(
                        new ItemStack(ModItems.WOOD_BLIGHT.get()),
                        new ItemStack(ModItems.STONE_BLIGHT.get()),
                        new ItemStack(ModItems.COPPER_BLIGHT.get()),
                        new ItemStack(ModItems.IRON_BLIGHT.get()),
                        new ItemStack(ModItems.GOLD_BLIGHT.get()),
                        new ItemStack(ModItems.DIAMOND_BLIGHT.get()),
                        new ItemStack(ModItems.NETHERITE_BLIGHT.get())),
                VanillaTypes.ITEM_STACK, Component.translatable("jei.artificers_armory.blight_info"));

        registration.addIngredientInfo(List.of(
                        new ItemStack(ModItems.WOOD_CENSER.get()),
                        new ItemStack(ModItems.STONE_CENSER.get()),
                        new ItemStack(ModItems.COPPER_CENSER.get()),
                        new ItemStack(ModItems.IRON_CENSER.get()),
                        new ItemStack(ModItems.GOLD_CENSER.get()),
                        new ItemStack(ModItems.DIAMOND_CENSER.get()),
                        new ItemStack(ModItems.NETHERITE_CENSER.get())),
                VanillaTypes.ITEM_STACK, Component.translatable("jei.artificers_armory.censer_info"));


    }

}

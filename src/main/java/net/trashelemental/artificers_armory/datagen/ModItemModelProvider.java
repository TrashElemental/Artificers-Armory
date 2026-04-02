package net.trashelemental.artificers_armory.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.item.ModItems;


public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ArtificersArmory.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

        firebrandItem(ModItems.WOOD_FIREBRAND);
        firebrandItem(ModItems.STONE_FIREBRAND);
        firebrandItem(ModItems.COPPER_FIREBRAND);
        firebrandItem(ModItems.IRON_FIREBRAND);
        firebrandItem(ModItems.GOLD_FIREBRAND);
        firebrandItem(ModItems.DIAMOND_FIREBRAND);
        firebrandItem(ModItems.NETHERITE_FIREBRAND);

        spiritCandleItem(ModItems.WOOD_SPIRIT_CANDLE);
        spiritCandleItem(ModItems.STONE_SPIRIT_CANDLE);
        spiritCandleItem(ModItems.COPPER_SPIRIT_CANDLE);
        spiritCandleItem(ModItems.IRON_SPIRIT_CANDLE);
        spiritCandleItem(ModItems.GOLD_SPIRIT_CANDLE);
        spiritCandleItem(ModItems.DIAMOND_SPIRIT_CANDLE);
        spiritCandleItem(ModItems.NETHERITE_SPIRIT_CANDLE);

        handheldItem(ModItems.WOOD_CHIME);
        handheldItem(ModItems.STONE_CHIME);
        handheldItem(ModItems.COPPER_CHIME);
        handheldItem(ModItems.IRON_CHIME);
        handheldItem(ModItems.GOLD_CHIME);
        handheldItem(ModItems.DIAMOND_CHIME);
        handheldItem(ModItems.NETHERITE_CHIME);

        firebrandItem(ModItems.WOOD_CENSER);
        firebrandItem(ModItems.STONE_CENSER);
        firebrandItem(ModItems.COPPER_CENSER);
        firebrandItem(ModItems.IRON_CENSER);
        firebrandItem(ModItems.GOLD_CENSER);
        firebrandItem(ModItems.DIAMOND_CENSER);
        firebrandItem(ModItems.NETHERITE_CENSER);

        blightItem(ModItems.WOOD_BLIGHT);
        blightItem(ModItems.STONE_BLIGHT);
        blightItem(ModItems.COPPER_BLIGHT);
        blightItem(ModItems.IRON_BLIGHT);
        blightItem(ModItems.GOLD_BLIGHT);
        blightItem(ModItems.DIAMOND_BLIGHT);
        blightItem(ModItems.NETHERITE_BLIGHT);

    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0",
                new ResourceLocation(ArtificersArmory.MOD_ID, "item/" + item.getId().getPath()));
    }

    public void evenSimplerBlockItem(RegistryObject<Block> block) {
        this.withExistingParent(ArtificersArmory.MOD_ID + ":" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath(),
                modLoc("block/" + ForgeRegistries.BLOCKS.getKey(block.get()).getPath()));
    }

    public void wallItem(RegistryObject<Block> block, RegistryObject<Block> baseblock) {
        this.withExistingParent(ForgeRegistries.BLOCKS.getKey(block.get()).getPath(), mcLoc("block/wall_inventory"))
                .texture("wall", new ResourceLocation(ArtificersArmory.MOD_ID, "block/" + ForgeRegistries.BLOCKS.getKey(baseblock.get()).getPath()));
    }

    private ItemModelBuilder handheldItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/handheld")).texture("layer0",
                new ResourceLocation(ArtificersArmory.MOD_ID, "item/" + item.getId().getPath()));
    }

    private ItemModelBuilder firebrandItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation(ArtificersArmory.MOD_ID, "item/firebrand")).texture("layer0",
                new ResourceLocation(ArtificersArmory.MOD_ID, "item/" + item.getId().getPath()));
    }

    private ItemModelBuilder spiritCandleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation(ArtificersArmory.MOD_ID, "item/spirit_candle")).texture("layer0",
                new ResourceLocation(ArtificersArmory.MOD_ID, "item/" + item.getId().getPath()));
    }

    private ItemModelBuilder blightItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation(ArtificersArmory.MOD_ID, "item/blight")).texture("layer0",
                new ResourceLocation(ArtificersArmory.MOD_ID, "item/" + item.getId().getPath()));
    }
}

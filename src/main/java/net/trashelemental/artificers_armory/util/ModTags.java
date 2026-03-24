package net.trashelemental.artificers_armory.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.trashelemental.artificers_armory.ArtificersArmory;

public class ModTags {

    public static class Enchantments {

        public static final TagKey<Enchantment> MULTISHOT =
                TagKey.create(Registries.ENCHANTMENT, new ResourceLocation(ArtificersArmory.MOD_ID,"multishot"));
        public static final TagKey<Enchantment> FIREBRAND_ALLOWED =
                TagKey.create(Registries.ENCHANTMENT, new ResourceLocation(ArtificersArmory.MOD_ID,"firebrand_allowed"));
        public static final TagKey<Enchantment> SPIRIT_CANDLE_ALLOWED =
                TagKey.create(Registries.ENCHANTMENT, new ResourceLocation(ArtificersArmory.MOD_ID,"spirit_candle_allowed"));
        public static final TagKey<Enchantment> CHIME_ALLOWED =
                TagKey.create(Registries.ENCHANTMENT, new ResourceLocation(ArtificersArmory.MOD_ID,"chime_allowed"));

    }

    public static class Blocks {

        public static final TagKey<Block> FAMILIAR_INTERESTING =
                TagKey.create(Registries.BLOCK, new ResourceLocation(ArtificersArmory.MOD_ID, "familiar_interesting"));

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(ArtificersArmory.MOD_ID, name));
        }
    }


    public static class Items {


        private static TagKey<Item> tag(String name) {
            return ItemTags.create(new ResourceLocation(ArtificersArmory.MOD_ID, name));
        }
    }

}

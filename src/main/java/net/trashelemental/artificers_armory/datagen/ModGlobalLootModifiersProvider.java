package net.trashelemental.artificers_armory.datagen;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.trashelemental.artificers_armory.ArtificersArmory;

public class ModGlobalLootModifiersProvider extends GlobalLootModifierProvider {
    public ModGlobalLootModifiersProvider(PackOutput output) {
        super(output, ArtificersArmory.MOD_ID);
    }


    @Override
    protected void start() {


    }
}

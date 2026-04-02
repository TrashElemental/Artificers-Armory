package net.trashelemental.artificers_armory.compat.BetterCombat;

import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.item.ModItems;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BetterCombatWeaponPresetProvider implements DataProvider {
    private final PackOutput packOutput;
    private final List<CompletableFuture<?>> futures = new ArrayList<>();
    private CachedOutput cache;
    private Path outputFolder;

    public BetterCombatWeaponPresetProvider(PackOutput packOutput) {
        this.packOutput = packOutput;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        this.cache = cache;
        this.outputFolder = packOutput.getOutputFolder();

        registerWeapon(ModItems.WOOD_FIREBRAND, "wand");
        registerWeapon(ModItems.STONE_FIREBRAND, "wand");
        registerWeapon(ModItems.COPPER_FIREBRAND, "wand");
        registerWeapon(ModItems.IRON_FIREBRAND, "wand");
        registerWeapon(ModItems.GOLD_FIREBRAND, "wand");
        registerWeapon(ModItems.DIAMOND_FIREBRAND, "wand");
        registerWeapon(ModItems.NETHERITE_FIREBRAND, "wand");

        registerWeapon(ModItems.WOOD_BLIGHT, "rapier");
        registerWeapon(ModItems.STONE_BLIGHT, "rapier");
        registerWeapon(ModItems.COPPER_BLIGHT, "rapier");
        registerWeapon(ModItems.IRON_BLIGHT, "rapier");
        registerWeapon(ModItems.GOLD_BLIGHT, "rapier");
        registerWeapon(ModItems.DIAMOND_BLIGHT, "rapier");
        registerWeapon(ModItems.NETHERITE_BLIGHT, "rapier");

        registerWeapon(ModItems.WOOD_CENSER, "mace");
        registerWeapon(ModItems.STONE_CENSER, "mace");
        registerWeapon(ModItems.COPPER_CENSER, "mace");
        registerWeapon(ModItems.IRON_CENSER, "mace");
        registerWeapon(ModItems.GOLD_CENSER, "mace");
        registerWeapon(ModItems.DIAMOND_CENSER, "mace");
        registerWeapon(ModItems.NETHERITE_CENSER, "mace");


        return CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0]));
    }

    private void registerWeapon(RegistryObject<Item> item, String preset) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", "bettercombat:" + preset);

        Path path = outputFolder.resolve("data/" + ArtificersArmory.MOD_ID + "/weapon_attributes/" + item.getId().getPath() + ".json");

        futures.add(DataProvider.saveStable(cache, json, path));
    }

    @Override
    public String getName() {
        return "Better Combat Weapon Attributes";
    }
}

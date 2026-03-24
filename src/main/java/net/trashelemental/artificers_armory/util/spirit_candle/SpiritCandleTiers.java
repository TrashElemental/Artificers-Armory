package net.trashelemental.artificers_armory.util.spirit_candle;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.entity.ModEntities;

import java.util.List;
import java.util.Set;

public class SpiritCandleTiers {

    public static final ResourceLocation zombie = new ResourceLocation(ArtificersArmory.MOD_ID, "zombie_minion");
    public static final ResourceLocation skeleton = new ResourceLocation(ArtificersArmory.MOD_ID, "skeleton_minion");
    public static final ResourceLocation drowned = new ResourceLocation(ArtificersArmory.MOD_ID, "drowned_minion");
    public static final ResourceLocation stray = new ResourceLocation(ArtificersArmory.MOD_ID, "stray_minion");
    public static final ResourceLocation wither_skeleton = new ResourceLocation(ArtificersArmory.MOD_ID, "wither_skeleton_minion");

    // 50% chance for zombie, 50% chance for skeleton
    // 10% chance to have equipment, 0% chance for equipment to be enchanted
    public static final SpiritCandleTier WOOD = new SpiritCandleTier(
            new ResourceLocation(ArtificersArmory.MOD_ID, "wood"),
            List.of(
                    new SummonEntry(zombie, 0.5f),
                    new SummonEntry(skeleton, 0.5f)),
            new EquipmentRules(
                    0.1f, 0.0f, 0, 0,
                    List.of(
                            new EquipmentEntry(new ItemStack(Items.WOODEN_SWORD), EquipmentSlot.MAINHAND,
                                    Set.of(zombie, skeleton)),
                            new EquipmentEntry(new ItemStack(Items.STONE_SHOVEL), EquipmentSlot.MAINHAND,
                                    Set.of(zombie, skeleton))
                    )
            )
    );

    // 50% chance for zombie, 50% chance for skeleton
    // 15% chance to have equipment, 0% chance for equipment to be enchanted
    public static final SpiritCandleTier STONE = new SpiritCandleTier(
            new ResourceLocation(ArtificersArmory.MOD_ID, "stone"),
            List.of(
                    new SummonEntry(zombie, 0.5f),
                    new SummonEntry(skeleton, 0.5f)
            ),
            new EquipmentRules(0.15f, 0.0f, 0, 0,
                    List.of(
                            new EquipmentEntry(new ItemStack(Items.WOODEN_SWORD), EquipmentSlot.MAINHAND,
                                    Set.of(zombie, skeleton)),
                            new EquipmentEntry(new ItemStack(Items.WOODEN_AXE), EquipmentSlot.MAINHAND,
                                    Set.of(zombie)),
                            new EquipmentEntry(new ItemStack(Items.BOW), EquipmentSlot.MAINHAND,
                                    Set.of(skeleton))
                    ))
    );

    // 40% chance for zombie, 40% chance for skeleton, 20% chance for drowned
    // 20% chance to have equipment, 0% chance for equipment to be enchanted
    // Drowned will always spawn with a trident (handled in their class)
    public static final SpiritCandleTier COPPER = new SpiritCandleTier(
            new ResourceLocation(ArtificersArmory.MOD_ID, "copper"),
            List.of(
                    new SummonEntry(zombie, 0.4f),
                    new SummonEntry(skeleton, 0.4f),
                    new SummonEntry(drowned, 0.2f)
            ),
            new EquipmentRules(0.2f, 0.0f, 0, 0,
                    List.of(
                            new EquipmentEntry(new ItemStack(Items.STONE_SWORD), EquipmentSlot.MAINHAND,
                                    Set.of(zombie, skeleton)),
                            new EquipmentEntry(new ItemStack(Items.WOODEN_AXE), EquipmentSlot.MAINHAND,
                                    Set.of(zombie)),
                            new EquipmentEntry(new ItemStack(Items.BOW), EquipmentSlot.MAINHAND,
                                    Set.of(skeleton))
                    ))
    );

    // 50% chance for zombie, 50% chance for skeleton
    // 25% chance to have equipment, 0% chance for equipment to be enchanted
    public static final SpiritCandleTier IRON = new SpiritCandleTier(
            new ResourceLocation(ArtificersArmory.MOD_ID, "iron"),
            List.of(
                    new SummonEntry(zombie, 0.5f),
                    new SummonEntry(skeleton, 0.5f)
            ),
            new EquipmentRules(0.25f, 0.0f, 0, 0,
                    List.of(
                            new EquipmentEntry(new ItemStack(Items.STONE_SWORD), EquipmentSlot.MAINHAND,
                                    Set.of(zombie, skeleton)),
                            new EquipmentEntry(new ItemStack(Items.STONE_AXE), EquipmentSlot.MAINHAND,
                                    Set.of(zombie)),
                            new EquipmentEntry(new ItemStack(Items.BOW), EquipmentSlot.MAINHAND,
                                    Set.of(skeleton)),
                            new EquipmentEntry(new ItemStack(Items.LEATHER_BOOTS), EquipmentSlot.FEET,
                                    Set.of(zombie, skeleton)),
                            new EquipmentEntry(new ItemStack(Items.LEATHER_HELMET), EquipmentSlot.HEAD,
                                    Set.of(zombie, skeleton)),
                            new EquipmentEntry(new ItemStack(Items.LEATHER_CHESTPLATE), EquipmentSlot.CHEST,
                                    Set.of(zombie, skeleton)),
                            new EquipmentEntry(new ItemStack(Items.LEATHER_LEGGINGS), EquipmentSlot.LEGS,
                                    Set.of(zombie, skeleton))
                    ))
    );

    // 50% chance for zombie, 50% chance for skeleton
    // 30% chance to have equipment, 60% chance for equipment to be enchanted
    public static final SpiritCandleTier GOLD = new SpiritCandleTier(
            new ResourceLocation(ArtificersArmory.MOD_ID, "gold"),
            List.of(
                    new SummonEntry(zombie, 0.5f),
                     new SummonEntry(skeleton, 0.5f)
            ),
            new EquipmentRules(0.3f, 0.6f, 1, 2,
                    List.of(
                            new EquipmentEntry(new ItemStack(Items.GOLDEN_SWORD), EquipmentSlot.MAINHAND,
                                    Set.of(zombie)),
                            new EquipmentEntry(new ItemStack(Items.GOLDEN_AXE), EquipmentSlot.MAINHAND,
                                    Set.of(zombie)),
                            new EquipmentEntry(new ItemStack(Items.BOW), EquipmentSlot.MAINHAND,
                                    Set.of(skeleton)),
                            new EquipmentEntry(new ItemStack(Items.LEATHER_BOOTS), EquipmentSlot.FEET,
                                    Set.of(zombie, skeleton)),
                            new EquipmentEntry(new ItemStack(Items.LEATHER_HELMET), EquipmentSlot.HEAD,
                                    Set.of(zombie, skeleton)),
                            new EquipmentEntry(new ItemStack(Items.LEATHER_CHESTPLATE), EquipmentSlot.CHEST,
                                    Set.of(zombie, skeleton)),
                            new EquipmentEntry(new ItemStack(Items.LEATHER_LEGGINGS), EquipmentSlot.LEGS,
                                    Set.of(zombie, skeleton))
                    ))
    );

    // 40% chance for zombie, 40% chance for skeleton, 20% chance for stray
    // 40% chance to have equipment, 10% chance for equipment to be enchanted
    // Strays will always spawn with a Bow (handled in their class)
    public static final SpiritCandleTier DIAMOND = new SpiritCandleTier(
            new ResourceLocation(ArtificersArmory.MOD_ID, "diamond"),
            List.of(
                    new SummonEntry(zombie, 0.4f),
                    new SummonEntry(stray, 0.2f),
                    new SummonEntry(skeleton, 0.4f)
            ),
            new EquipmentRules(0.4f, 0.1f, 1, 2,
                    List.of(
                            new EquipmentEntry(new ItemStack(Items.IRON_SWORD), EquipmentSlot.MAINHAND,
                                    Set.of(zombie, skeleton)),
                            new EquipmentEntry(new ItemStack(Items.STONE_AXE), EquipmentSlot.MAINHAND,
                                    Set.of(zombie)),
                            new EquipmentEntry(new ItemStack(Items.BOW), EquipmentSlot.MAINHAND,
                                    Set.of(skeleton)),
                            new EquipmentEntry(new ItemStack(Items.IRON_BOOTS), EquipmentSlot.FEET,
                                    Set.of(zombie, skeleton)),
                            new EquipmentEntry(new ItemStack(Items.IRON_HELMET), EquipmentSlot.HEAD,
                                    Set.of(zombie, skeleton)),
                            new EquipmentEntry(new ItemStack(Items.IRON_CHESTPLATE), EquipmentSlot.CHEST,
                                    Set.of(zombie, skeleton)),
                            new EquipmentEntry(new ItemStack(Items.IRON_LEGGINGS), EquipmentSlot.LEGS,
                                    Set.of(zombie, skeleton))
                    ))
    );

    // 20% chance for zombie, 20% chance for skeleton, 30% chance for stray, 20% chance for wither skeleton
    // 50% chance to have equipment, 20% chance for equipment to be enchanted
    // Strays will always spawn with a Bow (handled in their class)
    // Wither Skeletons will always spawn with a Stone Sword (handled in their class)
    public static final SpiritCandleTier NETHERITE = new SpiritCandleTier(
            new ResourceLocation(ArtificersArmory.MOD_ID, "netherite"),
            List.of(
                    new SummonEntry(zombie, 0.30f),
                    new SummonEntry(wither_skeleton, 0.20f),
                    new SummonEntry(stray, 0.20f),
                    new SummonEntry(skeleton, 0.30f)
            ),
            new EquipmentRules(0.5f, 0.2f, 1, 3,
                    List.of(
                            new EquipmentEntry(new ItemStack(Items.IRON_SWORD), EquipmentSlot.MAINHAND,
                                    Set.of(zombie, skeleton)),
                            new EquipmentEntry(new ItemStack(Items.IRON_AXE), EquipmentSlot.MAINHAND,
                                    Set.of(zombie)),
                            new EquipmentEntry(new ItemStack(Items.BOW), EquipmentSlot.MAINHAND,
                                    Set.of(skeleton)),
                            new EquipmentEntry(new ItemStack(Items.IRON_BOOTS), EquipmentSlot.FEET,
                                    Set.of(zombie, skeleton)),
                            new EquipmentEntry(new ItemStack(Items.IRON_HELMET), EquipmentSlot.HEAD,
                                    Set.of(zombie, skeleton)),
                            new EquipmentEntry(new ItemStack(Items.IRON_CHESTPLATE), EquipmentSlot.CHEST,
                                    Set.of(zombie, skeleton)),
                            new EquipmentEntry(new ItemStack(Items.IRON_LEGGINGS), EquipmentSlot.LEGS,
                                    Set.of(zombie, skeleton))
                    ))
    );


}

package net.trashelemental.artificers_armory.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.entity.custom.*;
import net.trashelemental.artificers_armory.entity.custom.necromancy.*;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ArtificersArmory.MOD_ID);

    public static final RegistryObject<EntityType<FamiliarEntity>> FAMILIAR =
            ENTITY_TYPES.register("familiar", () -> EntityType.Builder.of(
                    FamiliarEntity::new, MobCategory.CREATURE).sized(0.35f, 0.6F).build("familiar"));

    // Projectiles
    public static final RegistryObject<EntityType<FireballEntity>> FIREBALL_ENTITY =
            ENTITY_TYPES.register("fireball",
                    () -> EntityType.Builder.<FireballEntity>of(FireballEntity::new, MobCategory.MISC)
                            .sized(0.3f, 0.3f).build("fireball"));
    public static final RegistryObject<EntityType<PotionCloudEntity>> POTION_CLOUD_ENTITY =
            ENTITY_TYPES.register("potion_cloud",
                    () -> EntityType.Builder.<PotionCloudEntity>of(PotionCloudEntity::new, MobCategory.MISC)
                            .sized(0.3f, 0.3f).build("potion_cloud"));

    // Minions
    public static final RegistryObject<EntityType<ZombieMinionEntity>> ZOMBIE_MINION =
            ENTITY_TYPES.register("zombie_minion", () -> EntityType.Builder
                    .of(ZombieMinionEntity::new, MobCategory.MISC).sized(0.6F, 1.95F).build("zombie_minion"));
    public static final RegistryObject<EntityType<DrownedMinionEntity>> DROWNED_MINION =
            ENTITY_TYPES.register("drowned_minion", () -> EntityType.Builder
                    .of(DrownedMinionEntity::new, MobCategory.MISC).sized(0.6F, 1.95F).build("drowned_minion"));
    public static final RegistryObject<EntityType<SkeletonMinionEntity>> SKELETON_MINION =
            ENTITY_TYPES.register("skeleton_minion", () -> EntityType.Builder
                    .of(SkeletonMinionEntity::new, MobCategory.MISC).sized(0.6F, 1.95F).build("skeleton_minion"));
    public static final RegistryObject<EntityType<StrayMinionEntity>> STRAY_MINION =
            ENTITY_TYPES.register("stray_minion", () -> EntityType.Builder
                    .of(StrayMinionEntity::new, MobCategory.MISC).sized(0.6F, 1.95F).build("stray_minion"));
    public static final RegistryObject<EntityType<WitherSkeletonMinionEntity>> WITHER_SKELETON_MINION =
            ENTITY_TYPES.register("wither_skeleton_minion", () -> EntityType.Builder
                    .of(WitherSkeletonMinionEntity::new, MobCategory.MISC).sized(0.6F, 1.95F).build("wither_skeleton_minion"));

    public static final RegistryObject<EntityType<WispEntity>> WISP =
            ENTITY_TYPES.register("wisp", () -> EntityType.Builder.of(
                    WispEntity::new, MobCategory.CREATURE).sized(0.35f, 0.6F).build("wisp"));

    //Misc
    public static final RegistryObject<EntityType<SkeletonPriestEntity>> SKELETON_PRIEST =
            ENTITY_TYPES.register("skeleton_priest",
                    () -> EntityType.Builder.of(SkeletonPriestEntity::new, MobCategory.MISC)
                            .sized(0.3f, 0.3f).build("skeleton_priest"));
    public static final RegistryObject<EntityType<PlagueRatEntity>> PLAGUE_RAT =
            ENTITY_TYPES.register("plague_rat", () -> EntityType.Builder.of(
                    PlagueRatEntity::new, MobCategory.CREATURE).sized(0.35f, 0.6F).build("plague_rat"));



    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}

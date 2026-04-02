package net.trashelemental.artificers_armory.entity;

import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import net.trashelemental.artificers_armory.entity.custom.PlagueRatEntity;
import net.trashelemental.artificers_armory.entity.custom.WispEntity;
import net.trashelemental.artificers_armory.entity.custom.necromancy.*;


@Mod.EventBusSubscriber(modid = ArtificersArmory.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.ZOMBIE_MINION.get(), ZombieMinionEntity.createAttributes().build());
        event.put(ModEntities.DROWNED_MINION.get(), DrownedMinionEntity.createAttributes().build());
        event.put(ModEntities.SKELETON_MINION.get(), SkeletonMinionEntity.createAttributes().build());
        event.put(ModEntities.STRAY_MINION.get(), StrayMinionEntity.createAttributes().build());
        event.put(ModEntities.WITHER_SKELETON_MINION.get(), WitherSkeletonMinionEntity.createAttributes().build());
        event.put(ModEntities.FAMILIAR.get(), FamiliarEntity.createAttributes().build());
        event.put(ModEntities.WISP.get(), WispEntity.createAttributes().build());
        event.put(ModEntities.PLAGUE_RAT.get(), PlagueRatEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {



    }




    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {


    }


}

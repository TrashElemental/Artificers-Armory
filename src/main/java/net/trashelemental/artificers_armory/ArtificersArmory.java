package net.trashelemental.artificers_armory;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.trashelemental.artificers_armory.block.ModBlocks;
import net.trashelemental.artificers_armory.entity.ModEntities;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarTaskRegistry;
import net.trashelemental.artificers_armory.entity.client.renderers.*;
import net.trashelemental.artificers_armory.item.ModCreativeModeTabs;
import net.trashelemental.artificers_armory.item.ModItems;
import net.trashelemental.artificers_armory.magic.brewing.ModPotions;
import net.trashelemental.artificers_armory.magic.effects.ModMobEffects;
import net.trashelemental.artificers_armory.magic.enchantments.ModEnchantments;
import net.trashelemental.artificers_armory.particle.ModParticles;
import org.slf4j.Logger;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Mod(ArtificersArmory.MOD_ID)
public class ArtificersArmory
{
    public static final String MOD_ID = "artificers_armory";

    public static final Logger LOGGER = LogUtils.getLogger();

    public ArtificersArmory()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModCreativeModeTabs.register(modEventBus);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEntities.register(modEventBus);
        ModPotions.register(modEventBus);
        ModMobEffects.register(modEventBus);
        ModEnchantments.register(modEventBus);
        ModParticles.PARTICLES.register(modEventBus);
        FamiliarTaskRegistry.register();

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {

    }
    
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {

            EntityRenderers.register(ModEntities.FIREBALL_ENTITY.get(), FireballRenderer::new);
            EntityRenderers.register(ModEntities.ZOMBIE_MINION.get(), NecromancyZombieRenderer::new);
            EntityRenderers.register(ModEntities.DROWNED_MINION.get(), NecromancyDrownedRenderer::new);
            EntityRenderers.register(ModEntities.SKELETON_MINION.get(), NecromancySkeletonRenderer::new);
            EntityRenderers.register(ModEntities.STRAY_MINION.get(), NecromancyStrayRenderer::new);
            EntityRenderers.register(ModEntities.WITHER_SKELETON_MINION.get(), NecromancyWitherSkeletonRenderer::new);
            EntityRenderers.register(ModEntities.SKELETON_PRIEST.get(), SkeletonPriestRenderer::new);
            EntityRenderers.register(ModEntities.FAMILIAR.get(), FamiliarRenderer::new);
            EntityRenderers.register(ModEntities.WISP.get(), WispRenderer::new);

        }
    }


    private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

    public static void queueServerWork(int tickDelay, Runnable action) {
        workQueue.add(new AbstractMap.SimpleEntry<>(action, tickDelay));
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            List<AbstractMap.SimpleEntry<Runnable, Integer>> actionsToRun = new ArrayList<>();
            workQueue.forEach(work -> {
                work.setValue(work.getValue() - 1);
                if (work.getValue() <= 0) {
                    actionsToRun.add(work);
                }
            });
            actionsToRun.forEach(work -> work.getKey().run());
            workQueue.removeAll(actionsToRun);
        }
    }

}

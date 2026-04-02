package net.trashelemental.artificers_armory.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.trashelemental.artificers_armory.ArtificersArmory;

@Mod.EventBusSubscriber(modid = ArtificersArmory.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, ArtificersArmory.MOD_ID);

    public static final RegistryObject<SimpleParticleType> FAMILIAR_ATTENTION =
            PARTICLES.register("familiar_attention", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> IMP =
            PARTICLES.register("imp", () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> PLAGUE =
            PARTICLES.register("plague", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> PLAGUE_RATS =
            PARTICLES.register("plague_rats", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> BLACK_DEATH =
            PARTICLES.register("black_death", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> DELIRIUM =
            PARTICLES.register("delirium", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> PLAGUE_CLOUD =
            PARTICLES.register("plague_cloud", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> DEATH_CLOUD =
            PARTICLES.register("death_cloud", () -> new SimpleParticleType(false));

    public static final RegistryObject<SimpleParticleType> POTION_CLOUD =
            PARTICLES.register("potion_cloud", () -> new SimpleParticleType(false));


    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.FAMILIAR_ATTENTION.get(), FamiliarAttentionParticles.Provider::new);
        event.registerSpriteSet(ModParticles.IMP.get(), ImpParticles.Provider::new);
        event.registerSpriteSet(ModParticles.PLAGUE.get(), PlagueParticles.Provider::new);
        event.registerSpriteSet(ModParticles.PLAGUE_RATS.get(), PlagueRatParticles.Provider::new);
        event.registerSpriteSet(ModParticles.BLACK_DEATH.get(), BlackDeathParticles.Provider::new);
        event.registerSpriteSet(ModParticles.DELIRIUM.get(), DeliriumParticles.Provider::new);
        event.registerSpriteSet(ModParticles.PLAGUE_CLOUD.get(), PlagueCloudParticles.Provider::new);
        event.registerSpriteSet(ModParticles.DEATH_CLOUD.get(), DeathCloudParticles.Provider::new);
        event.registerSpriteSet(ModParticles.POTION_CLOUD.get(), PotionCloudParticles.Provider::new);
    }

}

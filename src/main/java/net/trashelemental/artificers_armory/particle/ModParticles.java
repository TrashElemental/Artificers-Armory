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



    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.FAMILIAR_ATTENTION.get(), FamiliarAttentionParticles.Provider::new);
        event.registerSpriteSet(ModParticles.IMP.get(), ImpParticles.Provider::new);
    }

}

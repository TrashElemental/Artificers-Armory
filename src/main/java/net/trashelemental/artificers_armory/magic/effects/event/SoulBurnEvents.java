package net.trashelemental.artificers_armory.magic.effects.event;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.trashelemental.artificers_armory.junkyard_lib.util.UtilMethods;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.magic.effects.ModMobEffects;

@Mod.EventBusSubscriber
public class SoulBurnEvents {

    /**
     * If an entity can take damage from being on fire, Soul Burn increases the amount of fire damage taken by 1 per level.
     */
    @SubscribeEvent
    public static void increaseFireDamage(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();

        if (target.level().isClientSide) return;
        if (target.fireImmune()) return;
        if (!event.getSource().is(DamageTypes.ON_FIRE)) return;

        MobEffectInstance soulBurn = target.getEffect(ModMobEffects.SOUL_BURN.get());
        if (soulBurn == null) return;

        float bonus = soulBurn.getAmplifier() + 1;
        event.setAmount(event.getAmount() + bonus);
    }

    /**
     * When Soul Burn's effect activates on an inflammable target, it will deal damage equal to the effect level and
     * spawn some soul and soul fire particles.
     */
    public static void doSoulBurnDamage(LivingEntity entity) {
        if (entity.level().isClientSide) return;
        if (!entity.fireImmune()) return;

        MobEffectInstance soulBurn = entity.getEffect(ModMobEffects.SOUL_BURN.get());
        if (soulBurn == null) return;

        float damage = soulBurn.getAmplifier() + 1;

        UtilMethods.damageEntity(entity, DamageTypes.MAGIC, damage);

        ParticleMethods.ParticlesAroundServerSide(entity.level(), ParticleTypes.SOUL_FIRE_FLAME,
                entity.getX(), entity.getY() + 1, entity.getZ(), 3, 1.5
        );
        ParticleMethods.ParticlesAroundServerSide(entity.level(), ParticleTypes.SOUL,
                entity.getX(), entity.getY() + 1, entity.getZ(), 5, 1.5
        );
    }
}

package net.trashelemental.artificers_armory.magic.effects.custom;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.junkyard_lib.util.UtilMethods;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.magic.effects.ModMobEffects;
import net.trashelemental.artificers_armory.magic.effects.event.SoulBurnEvents;
import net.trashelemental.artificers_armory.particle.ModParticles;

public class PlagueEffect extends MobEffect {
    public PlagueEffect() {
        super(MobEffectCategory.HARMFUL, 9685817);

        this.addAttributeModifier(
                Attributes.ATTACK_DAMAGE,
                "3aaea4cb-d524-4bc2-b25e-9b3a47801e9d",
                -0.15D,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );

        this.addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                "d174f07e-5b23-4210-94f6-4ebd770a7799",
                -0.1D,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );

        this.addAttributeModifier(
                Attributes.ATTACK_SPEED,
                "30c51d61-2a08-4fc4-9c7d-7fd8aab0797a",
                -0.1D,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;
        float damage = 0.5f + (0.5f * amplifier);

        if (entity.hasEffect(ModMobEffects.BLACK_DEATH.get())) {
            ParticleMethods.ParticlesAroundServerSide(entity.level(), ModParticles.BLACK_DEATH.get(),
                    entity.getX(), entity.getEyeY(), entity.getZ(), 4, 1.5);

            UtilMethods.damageEntity(entity, DamageTypes.MAGIC, damage * 1.5f);
        } else {
            ParticleMethods.ParticlesAroundServerSide(entity.level(), ModParticles.PLAGUE.get(),
                    entity.getX(), entity.getEyeY(), entity.getZ(), 4, 1.5);

            UtilMethods.damageEntity(entity, DamageTypes.MAGIC, damage);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 30 == 0;
    }

    public ResourceLocation getIcon() {
        return new ResourceLocation(ArtificersArmory.MOD_ID, "textures/mob_effect/plague.png");
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return modifier.getAmount() * (amplifier + 1);
    }
}

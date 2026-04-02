package net.trashelemental.artificers_armory.magic.effects.custom;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.trashelemental.artificers_armory.ArtificersArmory;
<<<<<<<< HEAD:src/main/java/net/trashelemental/artificers_armory/magic/effects/custom/BlackDeathEffect.java

public class BlackDeathEffect extends MobEffect {
    public BlackDeathEffect() {
        super(MobEffectCategory.HARMFUL, 2039587);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {

    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 30 == 0;
    }

    public ResourceLocation getIcon() {
        return new ResourceLocation(ArtificersArmory.MOD_ID, "textures/mob_effect/black_death.png");
    }
========
import net.trashelemental.artificers_armory.magic.effects.event.SoulBurnEvents;
import net.trashelemental.artificers_armory.util.event.FirebrandEvents;

public class SoulBurnEffect extends MobEffect {
    public SoulBurnEffect() {
        super(MobEffectCategory.HARMFUL, 0x4AECD9);
    }

    @Override
    public void addAttributeModifiers(LivingEntity entity, AttributeMap attributeMap, int amplifier) {

    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        SoulBurnEvents.doSoulBurnDamage(entity);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    public ResourceLocation getIcon() {
        return new ResourceLocation(ArtificersArmory.MOD_ID, "textures/mob_effect/soul_burn.png");
    }
>>>>>>>> 1.20.1-forge/master:src/main/java/net/trashelemental/artificers_armory/magic/effects/custom/SoulBurnEffect.java
}

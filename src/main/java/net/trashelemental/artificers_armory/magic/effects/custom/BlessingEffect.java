package net.trashelemental.artificers_armory.magic.effects.custom;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class BlessingEffect extends MobEffect {
    public BlessingEffect() {
        super(MobEffectCategory.BENEFICIAL, 5832603);

        this.addAttributeModifier(
                Attributes.ATTACK_DAMAGE,
                "3aaea4cb-d524-4bc2-b25e-9b3a47801e7d",
                0.15D,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );
        this.addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                "3aaea4cb-d524-4bc2-b25e-9b3a47801e7d",
                0.05D,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );
        this.addAttributeModifier(
                Attributes.ARMOR_TOUGHNESS,
                "d174f07e-5b23-4210-94f6-4ebd770a7759",
                0.15D,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );
        this.addAttributeModifier(
                Attributes.ARMOR,
                "30c51d61-2a08-4fc4-9c7d-7fd8aab0737a",
                1,
                AttributeModifier.Operation.ADDITION
        );
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        entity.heal(amplifier);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 40 == 0;
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return modifier.getAmount() * (amplifier + 1);
    }
}

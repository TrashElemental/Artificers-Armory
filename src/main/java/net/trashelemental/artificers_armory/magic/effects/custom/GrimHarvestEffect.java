package net.trashelemental.artificers_armory.magic.effects.custom;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.trashelemental.artificers_armory.ArtificersArmory;

public class GrimHarvestEffect extends MobEffect {
    public GrimHarvestEffect() {
        super(MobEffectCategory.BENEFICIAL, 4125138);

        this.addAttributeModifier(
                Attributes.ATTACK_DAMAGE,
                "3aaea4cb-d524-4bc2-b25e-9b3a47801e7d",
                0.2D,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );
        this.addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                "d174f07e-5b23-4210-94f6-4ebd770a7759",
                0.06D,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );
        this.addAttributeModifier(
                Attributes.ARMOR,
                "30c51d61-2a08-4fc4-9c7d-7fd8aab0737a",
                2,
                AttributeModifier.Operation.ADDITION
        );
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        return modifier.getAmount() * (amplifier + 1);
    }

    public ResourceLocation getIcon() {
        return new ResourceLocation(ArtificersArmory.MOD_ID, "textures/mob_effect/grim_harvest.png");
    }
}

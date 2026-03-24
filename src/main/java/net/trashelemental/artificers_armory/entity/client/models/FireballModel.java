package net.trashelemental.artificers_armory.entity.client.models;

import net.minecraft.resources.ResourceLocation;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.entity.custom.FireballEntity;
import software.bernie.geckolib.model.GeoModel;

public class FireballModel extends GeoModel<FireballEntity> {
    @Override
    public ResourceLocation getModelResource(FireballEntity animatable) {
        return new ResourceLocation(ArtificersArmory.MOD_ID,"geo/models/fireball.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(FireballEntity animatable) {
        return animatable.getTexture();
    }

    @Override
    public ResourceLocation getAnimationResource(FireballEntity animatable) {
        return new ResourceLocation(ArtificersArmory.MOD_ID,"animations/fireball.animation.json");
    }

}

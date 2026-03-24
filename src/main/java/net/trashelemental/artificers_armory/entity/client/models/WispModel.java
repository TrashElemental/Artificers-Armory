package net.trashelemental.artificers_armory.entity.client.models;

import net.minecraft.resources.ResourceLocation;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.entity.custom.SkeletonPriestEntity;
import net.trashelemental.artificers_armory.entity.custom.WispEntity;
import software.bernie.geckolib.model.GeoModel;

public class WispModel extends GeoModel<WispEntity> {
    @Override
    public ResourceLocation getModelResource(WispEntity animatable) {
        return new ResourceLocation(ArtificersArmory.MOD_ID,"geo/models/wisp.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WispEntity animatable) {
        return new ResourceLocation(ArtificersArmory.MOD_ID, "textures/entity/wisp.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WispEntity animatable) {
        return new ResourceLocation(ArtificersArmory.MOD_ID,"animations/wisp.animation.json");
    }

}

package net.trashelemental.artificers_armory.entity.client.models;

import net.minecraft.resources.ResourceLocation;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.entity.custom.PlagueRatEntity;
import net.trashelemental.artificers_armory.entity.custom.WispEntity;
import software.bernie.geckolib.model.GeoModel;

public class PlagueRatModel extends GeoModel<PlagueRatEntity> {
    @Override
    public ResourceLocation getModelResource(PlagueRatEntity animatable) {
        return new ResourceLocation(ArtificersArmory.MOD_ID,"geo/models/plague_rat.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PlagueRatEntity animatable) {
        return new ResourceLocation(ArtificersArmory.MOD_ID, "textures/entity/plague_rat.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PlagueRatEntity animatable) {
        return new ResourceLocation(ArtificersArmory.MOD_ID,"animations/plague_rat.animation.json");
    }

}

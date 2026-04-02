package net.trashelemental.artificers_armory.entity.client.models;

import net.minecraft.resources.ResourceLocation;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.entity.custom.PlagueRatEntity;
import net.trashelemental.artificers_armory.entity.custom.PotionCloudEntity;
import software.bernie.geckolib.model.GeoModel;

public class PotionCloudModel extends GeoModel<PotionCloudEntity> {
    @Override
    public ResourceLocation getModelResource(PotionCloudEntity animatable) {
        return new ResourceLocation(ArtificersArmory.MOD_ID,"geo/models/plague_rat.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PotionCloudEntity animatable) {
        return new ResourceLocation(ArtificersArmory.MOD_ID, "textures/entity/potion_cloud.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PotionCloudEntity animatable) {
        return new ResourceLocation(ArtificersArmory.MOD_ID,"animations/plague_rat.animation.json");
    }

}

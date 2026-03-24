package net.trashelemental.artificers_armory.entity.client.models;

import net.minecraft.resources.ResourceLocation;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.entity.custom.FireballEntity;
import net.trashelemental.artificers_armory.entity.custom.SkeletonPriestEntity;
import software.bernie.geckolib.model.GeoModel;

public class SkeletonPriestModel extends GeoModel<SkeletonPriestEntity> {
    @Override
    public ResourceLocation getModelResource(SkeletonPriestEntity animatable) {
        return new ResourceLocation(ArtificersArmory.MOD_ID,"geo/models/skeleton_priest.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SkeletonPriestEntity animatable) {
        return new ResourceLocation(ArtificersArmory.MOD_ID, "textures/entity/skeleton_priest.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SkeletonPriestEntity animatable) {
        return new ResourceLocation(ArtificersArmory.MOD_ID,"animations/skeleton_priest.animation.json");
    }

}

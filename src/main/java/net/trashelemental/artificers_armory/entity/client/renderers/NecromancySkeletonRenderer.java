package net.trashelemental.artificers_armory.entity.client.renderers;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SkeletonRenderer;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.trashelemental.artificers_armory.entity.custom.necromancy.SkeletonMinionEntity;

public class NecromancySkeletonRenderer extends SkeletonRenderer {
    public NecromancySkeletonRenderer(EntityRendererProvider.Context p_174380_) {
        super(p_174380_);
    }

    @Override
    protected boolean isShaking(AbstractSkeleton pEntity) {

        if (pEntity instanceof SkeletonMinionEntity minion) {
            return minion.isNearbyIronGolem();
        }

        return super.isShaking(pEntity);
    }
}

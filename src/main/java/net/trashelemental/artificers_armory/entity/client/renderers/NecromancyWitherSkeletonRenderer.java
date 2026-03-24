package net.trashelemental.artificers_armory.entity.client.renderers;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.WitherSkeletonRenderer;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.trashelemental.artificers_armory.entity.custom.necromancy.SkeletonMinionEntity;
import net.trashelemental.artificers_armory.entity.custom.necromancy.WitherSkeletonMinionEntity;

public class NecromancyWitherSkeletonRenderer extends WitherSkeletonRenderer {
    public NecromancyWitherSkeletonRenderer(EntityRendererProvider.Context p_174447_) {
        super(p_174447_);
    }

    @Override
    protected boolean isShaking(AbstractSkeleton pEntity) {

        if (pEntity instanceof WitherSkeletonMinionEntity minion) {
            return minion.isNearbyIronGolem();
        }

        return super.isShaking(pEntity);
    }
}

package net.trashelemental.artificers_armory.entity.client.renderers;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.StrayRenderer;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.trashelemental.artificers_armory.entity.custom.necromancy.SkeletonMinionEntity;
import net.trashelemental.artificers_armory.entity.custom.necromancy.StrayMinionEntity;

public class NecromancyStrayRenderer extends StrayRenderer {
    public NecromancyStrayRenderer(EntityRendererProvider.Context p_174409_) {
        super(p_174409_);
    }

    @Override
    protected boolean isShaking(AbstractSkeleton pEntity) {

        if (pEntity instanceof StrayMinionEntity minion) {
            return minion.isNearbyIronGolem();
        }

        return super.isShaking(pEntity);
    }
}

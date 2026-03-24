package net.trashelemental.artificers_armory.entity.client.renderers;

import net.minecraft.client.renderer.entity.DrownedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Zombie;
import net.trashelemental.artificers_armory.entity.custom.necromancy.DrownedMinionEntity;
import net.trashelemental.artificers_armory.entity.custom.necromancy.ZombieMinionEntity;

/**
 * Yeah I'm making a new renderer just so I can make them shake because they're scared of golems. What of it
 */

public class NecromancyDrownedRenderer extends DrownedRenderer {
    public NecromancyDrownedRenderer(EntityRendererProvider.Context p_174456_) {
        super(p_174456_);
    }

    @Override
    protected boolean isShaking(Drowned pEntity) {

        if (pEntity instanceof DrownedMinionEntity minion) {
            return minion.isNearbyIronGolem();
        }

        return super.isShaking(pEntity);
    }
}

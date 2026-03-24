package net.trashelemental.artificers_armory.entity.client.renderers;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.world.entity.monster.Zombie;
import net.trashelemental.artificers_armory.entity.custom.necromancy.ZombieMinionEntity;

/**
 * Yeah I'm making a new renderer just so I can make them shake because they're scared of golems. What of it
 */

public class NecromancyZombieRenderer extends ZombieRenderer {
    public NecromancyZombieRenderer(EntityRendererProvider.Context p_174456_) {
        super(p_174456_);
    }

    @Override
    protected boolean isShaking(Zombie pEntity) {

        if (pEntity instanceof ZombieMinionEntity zombieMinion) {
            return zombieMinion.isNearbyIronGolem();
        }

        return super.isShaking(pEntity);
    }
}

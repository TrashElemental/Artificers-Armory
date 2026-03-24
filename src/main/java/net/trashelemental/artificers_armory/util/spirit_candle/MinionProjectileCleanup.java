package net.trashelemental.artificers_armory.util.spirit_candle;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.trashelemental.artificers_armory.entity.custom.OwnableMinion;

import java.util.UUID;

/**
 * Prevents minion friendly fire and cleans up leftover projectiles instead of leaving them in the world.
 */
@Mod.EventBusSubscriber
public class MinionProjectileCleanup {

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();
        Entity owner = projectile.getOwner();
        if (!(owner instanceof OwnableMinion firingMinion)) return;
        HitResult hit = event.getRayTraceResult();

        if (hit.getType() == HitResult.Type.ENTITY) {
            handleEntityHit(event, projectile, firingMinion, (EntityHitResult) hit);
            return;
        }

        if (hit.getType() == HitResult.Type.BLOCK) {
            projectile.discard();
        }
    }

    private static void handleEntityHit(ProjectileImpactEvent event, Projectile projectile, OwnableMinion firingMinion, EntityHitResult hit) {
        Entity target = hit.getEntity();

        if (target instanceof LivingEntity living && firingMinion.isOwnedBy(living)) {
            event.setImpactResult(ProjectileImpactEvent.ImpactResult.SKIP_ENTITY);
            return;
        }

        if (target instanceof OwnableMinion targetMinion) {
            UUID ownerA = firingMinion.getOwnerUUID();
            UUID ownerB = targetMinion.getOwnerUUID();

            if (ownerA != null && ownerA.equals(ownerB)) {
                event.setImpactResult(ProjectileImpactEvent.ImpactResult.SKIP_ENTITY);
                return;
            }
        }
        projectile.discard();
    }
}

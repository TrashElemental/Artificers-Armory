package net.trashelemental.artificers_armory.util.spirit_candle;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import net.trashelemental.artificers_armory.entity.custom.OwnableMinion;

import java.util.List;

/**
 * When a player holding a Spirit Candle dies, kill their minions as well.
 */

@Mod.EventBusSubscriber
public class KillMinionsOnOwnerKilledEvent {

    @SubscribeEvent
    public static void killMinionsOnOwnerKilled(LivingDeathEvent event) {

        LivingEntity victim = event.getEntity();
        Level level = victim.level();

        if (level.isClientSide) return;
        if (!(victim instanceof Player player)) return;

        // Check nearby owned minions
        AABB box = player.getBoundingBox().inflate(20);
        List<Mob> minions = level.getEntitiesOfClass(Mob.class, box,
                mob -> mob instanceof OwnableMinion m && m.getOwner() == player && mob.isAlive());

        // Kill nearby minions
        for (Mob mob : minions) {
            mob.kill();
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {

        Player player = event.getEntity();
        Level level = player.level();
        if (level.isClientSide) return;

        AABB box = player.getBoundingBox().inflate(20);
        List<Mob> minions = level.getEntitiesOfClass(Mob.class, box,
                mob -> mob instanceof OwnableMinion m && m.getOwner() == player && mob.isAlive());

        for (Mob minion : minions) {
            minion.discard();
        }
    }
}

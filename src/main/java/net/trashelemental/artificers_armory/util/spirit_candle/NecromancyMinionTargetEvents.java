package net.trashelemental.artificers_armory.util.spirit_candle;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.trashelemental.artificers_armory.entity.custom.OwnableMinion;
import net.trashelemental.artificers_armory.item.custom.SpiritCandleItem;
import net.trashelemental.artificers_armory.junkyard_lib.util.UtilMethods;

import java.util.Comparator;
import java.util.List;

@Mod.EventBusSubscriber
public class NecromancyMinionTargetEvents {

    /**
     * Prevent golems from attacking necromancy minions on sight.
     */
    @SubscribeEvent
    public static void protectNecromancyMinionsFromGolemsEvent(LivingChangeTargetEvent event) {

        LivingEntity entity = event.getEntity();
        LivingEntity target = event.getNewTarget();
        Level level = entity.level();
        LivingEntity lastAttacker = entity.getLastHurtByMob();

        if (level.isClientSide) return;
        if (!(entity instanceof IronGolem || entity instanceof SnowGolem)) return;
        if (!(target instanceof OwnableMinion minion)) return;

        if (lastAttacker != target && lastAttacker != minion.getOwner() &&
        minion.getOwner() != lastAttacker) event.setCanceled(true);
    }

    /**
     * Allow necromancy minions to sometimes take aggression for their owner. The default chance is 1/4, but it can
     * be boosted via the Protection enchantment.
     */
    @SubscribeEvent
    public static void maybeTakeOwnerAggroEvent(LivingChangeTargetEvent event) {

        LivingEntity attacker = event.getEntity();
        LivingEntity newTarget = event.getNewTarget();

        if (attacker.level().isClientSide) return;
        if (!(newTarget instanceof Player player)) return;
        if (!(attacker instanceof Mob mob)) return;
        ItemStack stack = ItemStack.EMPTY;
        if (player.getMainHandItem().getItem() instanceof SpiritCandleItem) {
            stack = player.getMainHandItem();
        } else if (player.getOffhandItem().getItem() instanceof SpiritCandleItem) {
            stack = player.getOffhandItem();
        }

        if (stack.isEmpty()) return;

        float chance = getAdjustedAggressionShieldChance(stack);
        if (mob.getRandom().nextFloat() > chance) return;
        double radius = 16.0;
        AABB box = player.getBoundingBox().inflate(radius);

        List<LivingEntity> candidates = mob.level().getEntitiesOfClass(
                LivingEntity.class, box, e -> e instanceof OwnableMinion minion
                        && player.getUUID().equals(minion.getOwnerUUID()) && e.isAlive() && mob.hasLineOfSight(e));

        if (candidates.isEmpty()) return;
        LivingEntity closest = candidates.stream().min(Comparator.comparingDouble(mob::distanceToSqr)).orElse(null);

        event.setNewTarget(closest);
    }

    public static float getAdjustedAggressionShieldChance(ItemStack stack) {
        if (!(stack.getItem() instanceof SpiritCandleItem)) return 0;
        int protectionLevel = UtilMethods.getEnchantmentLevel(stack, Enchantments.ALL_DAMAGE_PROTECTION);
        return Math.min(0.8f, 0.25f + 0.05f * protectionLevel);
    }
}

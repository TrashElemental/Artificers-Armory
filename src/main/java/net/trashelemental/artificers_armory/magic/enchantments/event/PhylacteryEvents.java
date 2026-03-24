package net.trashelemental.artificers_armory.magic.enchantments.event;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.trashelemental.artificers_armory.entity.custom.OwnableMinion;
import net.trashelemental.artificers_armory.item.custom.SpiritCandleItem;
import net.trashelemental.artificers_armory.junkyard_lib.util.UtilMethods;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.magic.enchantments.ModEnchantments;
import net.trashelemental.artificers_armory.util.spirit_candle.SpiritCandleEvents;

import java.util.List;

/**
 * When a player holding a Spirit Candle with the Sacrifice enchant takes lethal damage while they have three minions
 * nearby (or the maximum if it's lower), kill all the minions and let the player cheat death.
 */

@Mod.EventBusSubscriber
public class PhylacteryEvents {

    @SubscribeEvent
    public static void phylacteryEnchantActivate(LivingDamageEvent event) {

        LivingEntity victim = event.getEntity();
        Level level = victim.level();

        if (level.isClientSide) return;
        if (!(victim instanceof Player player)) return;
        if (event.getAmount() < player.getHealth()) return;

        ItemStack stack = ItemStack.EMPTY;
        if (player.getMainHandItem().getItem() instanceof SpiritCandleItem) {
            stack = player.getMainHandItem();
        } else if (player.getOffhandItem().getItem() instanceof SpiritCandleItem) {
            stack = player.getOffhandItem();
        }

        if (stack.isEmpty()) return;

        int sacrificeLevel =
                UtilMethods.getEnchantmentLevel(stack, ModEnchantments.PHYLACTERY.get());
        if (sacrificeLevel <= 0) return;

        // Check nearby owned minions
        AABB box = player.getBoundingBox().inflate(20);
        List<Mob> minions = level.getEntitiesOfClass(Mob.class, box,
                mob -> mob instanceof OwnableMinion m && m.getOwner() == player && mob.isAlive());

        int nearbyCount = minions.size();
        int maxAllowed = SpiritCandleEvents.getMaxAllowedMinions(player);

        // Check activation conditions
        if (nearbyCount < 3 && nearbyCount < maxAllowed) return;

        // Kill nearby minions
        for (Mob mob : minions) {
            mob.kill();
        }

        // Cancel lethal damage
        event.setCanceled(true);

        // Cheat death for the player
        player.setHealth(1.0F);
        player.removeAllEffects();
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));

        // Damage and put the item on cooldown for balance reasons
        InteractionHand hand =
                player.getMainHandItem() == stack ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        stack.hurtAndBreak(20, player, p -> p.broadcastBreakEvent(hand));
        player.getCooldowns().addCooldown(stack.getItem(), 100);


        // vfx
        level.broadcastEntityEvent(player, (byte) 35);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);

        ParticleMethods.ParticlesBurst(level, ParticleTypes.SOUL,
                player.getX(), player.getEyeY(), player.getZ(), 30, 0.8);
    }
}

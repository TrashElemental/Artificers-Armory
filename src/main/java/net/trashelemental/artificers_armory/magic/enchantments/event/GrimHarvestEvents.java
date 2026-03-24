package net.trashelemental.artificers_armory.magic.enchantments.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.trashelemental.artificers_armory.entity.ModEntities;
import net.trashelemental.artificers_armory.entity.custom.OwnableMinion;
import net.trashelemental.artificers_armory.entity.custom.SkeletonPriestEntity;
import net.trashelemental.artificers_armory.item.custom.SpiritCandleItem;
import net.trashelemental.artificers_armory.junkyard_lib.entity.method.SummonMethods;
import net.trashelemental.artificers_armory.junkyard_lib.util.UtilMethods;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.magic.effects.ModMobEffects;
import net.trashelemental.artificers_armory.magic.enchantments.ModEnchantments;
import net.trashelemental.artificers_armory.util.spirit_candle.SpiritCandleEvents;

import java.util.List;

/**
 * When a player or a necromancy minion of a player holding a Spirit Candle with the Grim Harvest enchantment
 * kills an entity, it has a chance to spawn a new necromancy minion if they're not at their maximum, or buff
 * the current necromancy minions if they are.
 */
@Mod.EventBusSubscriber
public class GrimHarvestEvents {

    @SubscribeEvent
    public static void grimHarvestActivate(LivingDeathEvent event) {
        LivingEntity killed = event.getEntity();
        Entity killer = event.getSource().getEntity();
        if (killer == null) return;

        Level level = killed.level();
        if (level.isClientSide) return;

        Player player;

        if (killer instanceof Player p) {
            player = p;
        } else if (killer instanceof OwnableMinion minion) {
            if (minion.getOwner() instanceof Player p) {
                player = p;
            } else {
                player = null;
            }
        } else {
            player = null;
        }

        if (player == null) return;
        if (killed instanceof OwnableEntity ownable && ownable.getOwner() == player) return;

        ItemStack stack = ItemStack.EMPTY;
        if (player.getMainHandItem().getItem() instanceof SpiritCandleItem) {
            stack = player.getMainHandItem();
        } else if (player.getOffhandItem().getItem() instanceof SpiritCandleItem) {
            stack = player.getOffhandItem();
        }

        if (stack.isEmpty()) return;

        int grimHarvestLevel =
                UtilMethods.getEnchantmentLevel(stack, ModEnchantments.GRIM_HARVEST.get());
        if (grimHarvestLevel <= 0) return;

        float procChance = Math.min(0.9f, grimHarvestLevel * 0.25f);
        if (player.getRandom().nextFloat() > procChance) return;

        int current = SpiritCandleEvents.countOwnedMinions(player, 20);
        int max = SpiritCandleEvents.getMaxAllowedMinions(player);
        int healingAmount = (3 * grimHarvestLevel);

        // If not at max minions, summon a new minion.
        if (current < max) {
            BlockPos pos = SpiritCandleEvents.findNearbySpawnPos(
                    level, player.blockPosition(), 5
            );
            SpiritCandleEvents.spawnMinion(level, pos, (SpiritCandleItem) stack.getItem(), player, stack);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SCULK_SHRIEKER_BREAK, SoundSource.PLAYERS, 0.6f, 1f);

            ParticleMethods.ParticleTrailBlockToEntity(level, ParticleTypes.SOUL,
                    pos, killed, 10);
        }

        // If at max minions, give a buff to existing minions.
        else {

            SkeletonPriestEntity priest = new SkeletonPriestEntity(ModEntities.SKELETON_PRIEST.get(), level);
            Vec3 spawnPos = killed.getOnPos().above().getCenter();
            priest.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
            level.addFreshEntity(priest);
            double dx = player.getX() - spawnPos.x;
            double dz = player.getZ() - spawnPos.z;
            float yaw = (float) (Math.atan2(dz, dx) * (180 / Math.PI)) - 90F;

            priest.setYRot(yaw);

            AABB box = player.getBoundingBox().inflate(20);
            List<Mob> minions = level.getEntitiesOfClass(Mob.class, box,
                    mob -> mob instanceof OwnableMinion m && m.getOwner() == player);

            for (Mob mob : minions) {
                mob.heal(healingAmount);
                mob.addEffect(new MobEffectInstance(
                        ModMobEffects.GRIM_HARVEST.get(), 100, grimHarvestLevel - 1, false, true));
                ParticleMethods.ParticlesAroundServerSide(level, ParticleTypes.SOUL,
                        mob.getX(), mob.getEyeY(), mob.getZ(), 5, 1);

            }
        }

    }
}

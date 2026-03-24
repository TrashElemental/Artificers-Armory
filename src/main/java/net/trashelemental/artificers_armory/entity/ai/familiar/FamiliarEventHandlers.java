package net.trashelemental.artificers_armory.entity.ai.familiar;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import net.trashelemental.artificers_armory.entity.custom.OwnableMinion;
import net.trashelemental.artificers_armory.item.ModItems;
import net.trashelemental.artificers_armory.item.custom.ChimeItem;
import net.trashelemental.artificers_armory.item.custom.SpiritCandleItem;
import net.trashelemental.artificers_armory.junkyard_lib.util.UtilMethods;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.magic.enchantments.ModEnchantments;
import net.trashelemental.artificers_armory.util.event.ChimeEvents;
import net.trashelemental.artificers_armory.util.spirit_candle.SpiritCandleEvents;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class FamiliarEventHandlers {
    /**
     * Don't send death messages for familiars, it would probably get annoying.
     */
    @SubscribeEvent
    public static void suppressFamiliarDeathMessageEvent(LivingDeathEvent event) {
        if (event != null && event.getEntity() != null) {
            if (event.getEntity() instanceof FamiliarEntity familiar && familiar.isTame()) {
                familiar.setOwnerUUID(null);
            }

        }
    }

    /**
     * Set a familiar's idle behaviors to stop if its owner takes damage and also check for on-damage triggered
     * abilities.
     */
    @SubscribeEvent
    public static void onOwnerDamage(LivingHurtEvent event) {
        LivingEntity damaged = event.getEntity();
        if (!(damaged instanceof Player player)) return;
        Level level = player.level();
        if (level.isClientSide) return;
        if (!ChimeEvents.hasActiveFamiliar(level, player)) return;
        DamageSource source = event.getSource();

        FamiliarEntity familiar = ChimeEvents.getActiveFamiliar(level, player);
        if (familiar == null) return;
        RandomSource random = familiar.getRandom();

        if (familiar.getCurrentTask() != null && familiar.getCurrentTask().isInterruptible()) {
            familiar.cancelCurrentTask();
            familiar.dropItem();
        }

        familiar.resetIdleCooldown();
        tryTriggeredAbility(familiar, source);

        // Healer intercept
        if (familiar.getRole() == FamiliarRole.HEALER) {
            float divineBlessingChance = (1 + familiar.getLevel() + (2 * familiar.getEnchantLevel())) / 100f;

            if (random.nextFloat() < divineBlessingChance) {
                float amount = event.getAmount();
                float redirected = Math.max(0, amount - familiar.getEnchantLevel());
                event.setCanceled(true);
                familiar.hurt(source, redirected);
                ParticleMethods.ParticleTrailEntityToEntity(level, ParticleTypes.DAMAGE_INDICATOR, familiar, player, 5);
                return;
            }
        }

        // Protector damage split
        if (familiar.getRole() == FamiliarRole.PROTECTOR) {
            float painSplitChance = (30 + (5 * (familiar.getLevel() + familiar.getEnchantLevel()))) / 100f;

            if (random.nextFloat() < painSplitChance && !(event.getAmount() <= 1)) {
                float newAmount = Math.max(1, (event.getAmount() / 2) - familiar.getEnchantLevel());
                event.setAmount(newAmount);
                familiar.hurt(source, newAmount);
                ParticleMethods.ParticleTrailEntityToEntity(level, ParticleTypes.DAMAGE_INDICATOR, familiar, player, 5);
            }
        }
    }

    /**
     * Try to catch an owner when they fall from a height.
     */
    @SubscribeEvent
    public static void onOwnerTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        Level level = player.level();
        if (level.isClientSide) return;
        if (!ChimeEvents.hasActiveFamiliar(level, player)) return;
        if (player.onGround()) return;
        Vec3 velocity = player.getDeltaMovement();
        if (velocity.y > -0.5) return;
        if (player.fallDistance < 4.5f) return;
        if (player.hasEffect(MobEffects.SLOW_FALLING)) return;

        FamiliarEntity familiar = ChimeEvents.getActiveFamiliar(level, player);
        if (familiar == null) return;

        if (familiar.getCurrentTask() != null && familiar.getCurrentTask().isInterruptible()) {
            familiar.cancelCurrentTask();
            familiar.dropItem();
        }
        familiar.resetIdleCooldown();
        tryTriggeredFallAbility(familiar, player.fallDistance);
    }


    /**
     * Familiars die when their owners die.
     */
    @SubscribeEvent
    public static void killFamiliarWhenOwnerKilled(LivingDeathEvent event) {
        LivingEntity victim = event.getEntity();
        Level level = victim.level();

        if (level.isClientSide) return;
        if (!(victim instanceof Player player)) return;
        if (!ChimeEvents.hasActiveFamiliar(level, player)) return;

        FamiliarEntity familiar = ChimeEvents.getActiveFamiliar(level, player);
        if (familiar == null) return;

        familiar.dropItem();
        familiar.kill();
    }

    /**
     * Protector familiar can allow the owner to cheat death, at the cost of killing the familiar and putting all
     * chime items on cooldown for 10 minutes.
     */
    @SubscribeEvent
    public static void protectorCheatDeathForOwner(LivingDamageEvent event) {
        LivingEntity victim = event.getEntity();
        Level level = victim.level();

        if (level.isClientSide) return;
        if (!(victim instanceof Player player)) return;
        if (event.getAmount() < player.getHealth()) return;
        if (!ChimeEvents.hasActiveFamiliar(level, player)) return;

        FamiliarEntity familiar = ChimeEvents.getActiveFamiliar(level, player);
        if (familiar == null) return;

        if (familiar.getRole() == FamiliarRole.PROTECTOR) {
            ParticleMethods.ParticleTrailEntityToEntity(level, ParticleTypes.DAMAGE_INDICATOR, familiar, player, 5);
            familiar.dropItem();
            familiar.kill();

            event.setCanceled(true);
            player.setHealth(1.0F);
            player.removeAllEffects();
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 900, 1));
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 1));
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 800, 0));

            player.getCooldowns().addCooldown(ModItems.WOOD_CHIME.get(), 12000);
            player.getCooldowns().addCooldown(ModItems.STONE_CHIME.get(), 12000);
            player.getCooldowns().addCooldown(ModItems.COPPER_CHIME.get(), 12000);
            player.getCooldowns().addCooldown(ModItems.IRON_CHIME.get(), 12000);
            player.getCooldowns().addCooldown(ModItems.GOLD_CHIME.get(), 12000);
            player.getCooldowns().addCooldown(ModItems.DIAMOND_CHIME.get(), 12000);
            player.getCooldowns().addCooldown(ModItems.NETHERITE_CHIME.get(), 12000);

            level.broadcastEntityEvent(player, (byte) 35);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    /**
     * Allow protector familiars to sometimes take aggression for their owner at a rate of 30% + (10x enchant level)
     */
    @SubscribeEvent
    public static void protectorMaybeTakeOwnerAggroEvent(LivingChangeTargetEvent event) {

        LivingEntity attacker = event.getEntity();
        LivingEntity newTarget = event.getNewTarget();
        Level level = attacker.level();

        if (attacker.level().isClientSide) return;
        if (!(newTarget instanceof Player player)) return;
        if (!(attacker instanceof Mob)) return;
        if (!ChimeEvents.hasActiveFamiliar(level, player)) return;

        FamiliarEntity familiar = ChimeEvents.getActiveFamiliar(level, player);

        if (familiar != null && familiar.getRole() == FamiliarRole.PROTECTOR) {
            float chance = 0.3f + (0.1f * familiar.getEnchantLevel());
            RandomSource random = familiar.getRandom();

            if (random.nextFloat() > chance) {
                event.setNewTarget(familiar);
                familiar.setTarget(attacker);
                ParticleMethods.ParticleTrailEntityToEntity(level, ParticleTypes.ANGRY_VILLAGER, familiar, attacker, 5);
                if (familiar.getCurrentTask() != null && familiar.getCurrentTask().isInterruptible()) {
                    familiar.dropItem();
                    familiar.cancelCurrentTask();
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity().level().isClientSide) return;

        Player player = event.getEntity();
        ServerLevel source = player.getServer().getLevel(event.getFrom());
        ServerLevel destination = player.getServer().getLevel(event.getTo());
        if (source == null || destination == null) return;

        if (!ChimeEvents.hasActiveFamiliar(source, player)) return;
        FamiliarEntity familiar = ChimeEvents.getActiveFamiliar(source, player);
        if (familiar == null) return;

        familiar.discard();
    }

    public static boolean tryPassiveAbility(FamiliarEntity familiar) {
        List<FamiliarTask> validTasks = new ArrayList<>();
        int totalWeight = 0;
        for (FamiliarTask task : FamiliarTaskRegistry.getPassiveAbilities()) {
            if (!task.canRun(familiar)) continue;
            int weight = task.getWeight(familiar);
            if (weight <= 0) continue;
            validTasks.add(task);
            totalWeight += weight;
        }

        if (validTasks.isEmpty() || totalWeight <= 0) {
            return false;
        }

        FamiliarTask selected = weightedRandom(familiar, validTasks, totalWeight);
        selected.start(familiar);
        familiar.startTask(selected, selected.getDuration(familiar));
        return true;
    }

    public static boolean tryPassiveBehavior(FamiliarEntity familiar) {

        List<FamiliarTask> validTasks = new ArrayList<>();
        int totalWeight = 0;

        for (FamiliarTask task : FamiliarTaskRegistry.getPassiveBehaviors()) {
            if (!task.canRun(familiar)) continue;
            int weight = task.getWeight(familiar);
            if (weight <= 0) continue;
            validTasks.add(task);
            totalWeight += weight;
        }

        if (validTasks.isEmpty() || totalWeight <= 0) {
            return false;
        }

        FamiliarTask selected = weightedRandom(familiar, validTasks, totalWeight);
        selected.start(familiar);
        familiar.startTask(selected, selected.getDuration(familiar));

        return true;
    }

    public static FamiliarTask weightedRandom(FamiliarEntity familiar, List<FamiliarTask> tasks, int totalWeight) {
        int roll = familiar.getRandom().nextInt(totalWeight);
        for (FamiliarTask task : tasks) {
            roll -= task.getWeight(familiar);
            if (roll < 0) {
                return task;
            }
        }
        return tasks.isEmpty() ? null : tasks.get(0);
    }

    public static void tryTriggeredAbility(FamiliarEntity familiar, DamageSource source) {
        if (familiar.triggeredCooldown > 0) return;
        for (FamiliarTask task : FamiliarTaskRegistry.getTriggeredAbilities()) {
            if (task.canTrigger(familiar, source)) {
                if (familiar.getCurrentTask() != null && familiar.getCurrentTask().isInterruptible()) {
                    familiar.cancelCurrentTask();
                }
                task.start(familiar);
                familiar.startTask(task, task.getDuration(familiar));
                familiar.setTriggeredCooldown(200 + familiar.getRandom().nextInt(200));
                return;
            }
        }
    }

    public static void tryTriggeredFallAbility(FamiliarEntity familiar, float distance) {
        if (familiar.triggeredCooldown > 0) return;
        for (FamiliarTask task : FamiliarTaskRegistry.getTriggeredAbilities()) {
            if (task.canTriggerFall(familiar, distance)) {
                if (familiar.getCurrentTask() != null && familiar.getCurrentTask().isInterruptible()) {
                    familiar.cancelCurrentTask();
                }
                task.start(familiar);
                familiar.startTask(task, task.getDuration(familiar));
                familiar.setTriggeredCooldown(200 + familiar.getRandom().nextInt(200));
                return;
            }
        }
    }

    public static boolean tryCombatAbility(FamiliarEntity familiar) {
        List<FamiliarTask> validTasks = new ArrayList<>();
        int totalWeight = 0;
        for (FamiliarTask task : FamiliarTaskRegistry.getCombatAbilities()) {
            if (!task.canRun(familiar)) continue;
            int weight = task.getWeight(familiar);
            if (weight <= 0) continue;
            validTasks.add(task);
            totalWeight += weight;
        }

        if (validTasks.isEmpty() || totalWeight <= 0) {
            return false;
        }

        FamiliarTask selected = weightedRandom(familiar, validTasks, totalWeight);
        familiar.startTask(selected, selected.getDuration(familiar));
        selected.start(familiar);
        return true;
    }

    public static void giveProtectorBonusEffects(FamiliarEntity familiar, Player owner) {
        if (!(familiar.getRole() == FamiliarRole.PROTECTOR)) return;

        int duration = familiar.getLevel() * 20;
        int amplifier = Math.max(0, familiar.getEnchantLevel() - 1);

        UtilMethods.applyEffectNoParticles(owner, MobEffects.DAMAGE_RESISTANCE, duration, amplifier);
        UtilMethods.applyEffectNoParticles(owner, MobEffects.ABSORPTION, duration, amplifier);
    }

    public static void giveHealerBonusEffects(FamiliarEntity familiar, Player owner) {
        if (!(familiar.getRole() == FamiliarRole.HEALER)) return;

        int duration = familiar.getLevel() * 20;
        int amplifier = Math.max(0, familiar.getEnchantLevel() - 1);

        UtilMethods.applyEffectNoParticles(owner, MobEffects.REGENERATION, duration, amplifier);
    }
}

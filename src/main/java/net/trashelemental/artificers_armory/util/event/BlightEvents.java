package net.trashelemental.artificers_armory.util.event;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.entity.ModEntities;
import net.trashelemental.artificers_armory.entity.custom.PlagueRatEntity;
import net.trashelemental.artificers_armory.item.custom.BlightItem;
import net.trashelemental.artificers_armory.junkyard_lib.util.UtilMethods;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.magic.effects.ModMobEffects;
import net.trashelemental.artificers_armory.magic.enchantments.ModEnchantments;
import net.trashelemental.artificers_armory.particle.ModParticles;
import net.trashelemental.artificers_armory.util.ModTags;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Mod.EventBusSubscriber
public class BlightEvents {

    public static float maxEffectReduction = 0.8f;

    public static int getAdjustedEffectLevel(ItemStack stack) {
        if (!(stack.getItem() instanceof BlightItem blight)) return 0;

        return Math.max(0, blight.effectLevel + UtilMethods.getEnchantmentLevel(stack, Enchantments.POWER_ARROWS) - 1);
    }

    public static float getAdjustedAreaDamage(ItemStack stack) {
        if (!(stack.getItem() instanceof BlightItem blight)) return 0;

        return (float) Math.max(1, blight.getAttackDamage() / 2 + UtilMethods.getEnchantmentLevel(stack, Enchantments.POWER_ARROWS));
    }

    public static void blightUseItem(ItemStack stack, Player player) {
        int remaining = player.getUseItemRemainingTicks();
        int max = stack.getUseDuration();
        int elapsedTicks = max - remaining;

        if (!(stack.getItem() instanceof BlightItem blight)) return;

        if (elapsedTicks < 20) return;
        if (elapsedTicks >= stack.getUseDuration() - 1) {
            player.stopUsingItem();
            player.getCooldowns().addCooldown(stack.getItem(), blight.cooldownTime);
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
        }

        if (elapsedTicks == 20) player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_BREATH, SoundSource.PLAYERS, 0.3F, 1F);

        if (elapsedTicks % 4 == 0) doConeParticles(stack, player);
        if (elapsedTicks % 20 == 0) performBreathAttack(stack, player);
    }

    public static void performBreathAttack(ItemStack stack, Player player) {
        if (!(stack.getItem() instanceof BlightItem blight)) return;

        Level level = player.level();
        if (level.isClientSide) return;

        if (UtilMethods.hasEnchantment(stack, ModEnchantments.PESTILENCE.get())) {
            maybeSpawnPlagueRat(stack, player);
        }

        Vec3 origin = player.getEyePosition();
        Vec3 lookDir = player.getLookAngle().normalize();

        double range = 6;
        double maxWidth = 6.25;
        double halfWidth = maxWidth / 2.0;

        AABB searchBox = player.getBoundingBox().inflate(range);

        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, searchBox)) {
            if (target == player) continue;
            if (FirebrandEvents.isAlly(player, target)) continue;
            Vec3 toTarget = target.getBoundingBox().getCenter().subtract(origin);
            double distance = toTarget.length();
            if (distance > range) continue;
            Vec3 directionToTarget = toTarget.normalize();
            double dot = lookDir.dot(directionToTarget);
            if (dot < 0.7) continue;
            Vec3 perpendicular = toTarget.subtract(lookDir.scale(toTarget.dot(lookDir)));
            if (perpendicular.length() > halfWidth) continue;

            HitResult hit = level.clip(new ClipContext(origin, target.getEyePosition(),
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));

            if (hit.getType() == HitResult.Type.BLOCK) continue;

            applyPlagueEffect(stack, player, target);
        }
    }

    public static void applyPlagueEffect(ItemStack stack, Player player, LivingEntity target) {
        if (!(stack.getItem() instanceof BlightItem blight)) return;
        int duration = blight.effectDuration * 20;
        int amplifier = getAdjustedEffectLevel(stack);
        float damage = getAdjustedAreaDamage(stack);

        if (UtilMethods.hasEnchantment(stack, ModEnchantments.PESTILENCE.get())) {
            UtilMethods.applyEffectNoParticles(target, ModMobEffects.PESTILENCE.get(), duration, amplifier);
        }
        if (UtilMethods.hasEnchantment(stack, ModEnchantments.ASHES_ASHES.get())) {
            UtilMethods.applyEffectNoParticles(target, ModMobEffects.BLACK_DEATH.get(), duration, amplifier);
        }
        if (UtilMethods.hasEnchantment(stack, ModEnchantments.DELIRIUM.get())) {
            UtilMethods.applyEffectNoParticles(target, ModMobEffects.DELIRIUM.get(), duration, amplifier);
        }

        UtilMethods.applyEffectNoParticles(target, ModMobEffects.PLAGUE.get(), duration, amplifier);
        target.hurt(player.damageSources().magic(), damage);
        target.getPersistentData().putUUID("lastPlagueApplier", player.getUUID());
    }

    public static boolean consumePlagueEffect(Player player) {

        Level level = player.level();
        int healing = 0;
        double radius = 8.0;
        AABB area = player.getBoundingBox().inflate(radius);

        boolean strength = false;
        boolean speed = false;
        boolean haste = false;
        int duration = 0;

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area)) {
            if (entity == player) continue;
            boolean removed = false;
            boolean increaseDuration = false;
            if (entity.hasEffect(ModMobEffects.PLAGUE.get())) {
                entity.removeEffect(ModMobEffects.PLAGUE.get());
                removed = true;
            }
            if (entity.hasEffect(ModMobEffects.PESTILENCE.get())) {
                entity.removeEffect(ModMobEffects.PESTILENCE.get());
                removed = true;
                speed = true;
                increaseDuration = true;
            }
            if (entity.hasEffect(ModMobEffects.DELIRIUM.get())) {
                entity.removeEffect(ModMobEffects.DELIRIUM.get());
                removed = true;
                haste = true;
                increaseDuration = true;
            }
            if (entity.hasEffect(ModMobEffects.BLACK_DEATH.get())) {
                entity.removeEffect(ModMobEffects.BLACK_DEATH.get());
                removed = true;
                strength = true;
                increaseDuration = true;
            }
            if (removed) {
                ParticleMethods.ParticleTrailEntityToEntity(level, ModParticles.PLAGUE.get(), player, entity, 5);
                healing++;
            }

            if (increaseDuration) duration++;
        }

        if (healing <= 0) return false;

        int finalHealingAmount = healing * 2;
        int finalDuration = 60 + duration * 20;

        float playerMaxHealth = player.getMaxHealth();
        float playerCurrentHealth = player.getHealth();

        if (playerCurrentHealth + finalHealingAmount >= playerMaxHealth) {
            player.setHealth(playerMaxHealth);
            UtilMethods.applyEffectNoParticles(player, MobEffects.ABSORPTION, 100, 0);
        } else {
            player.heal(finalHealingAmount);
        }

        if (strength) {
            UtilMethods.applyEffectNoParticles(player, MobEffects.DAMAGE_BOOST, finalDuration, 0);
        }
        if (speed) {
            UtilMethods.applyEffectNoParticles(player, MobEffects.MOVEMENT_SPEED, finalDuration, 0);
        }
        if (haste) {
            UtilMethods.applyEffectNoParticles(player, MobEffects.DIG_SPEED, finalDuration, 0);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.HUSK_CONVERTED_TO_ZOMBIE, SoundSource.PLAYERS, 1F, 1.0F);

        return true;
    }

    public static void doConeParticles(ItemStack stack, Player player) {

        Level level = player.level();
        if (level.isClientSide) return;

        Vec3 origin = player.getEyePosition();
        Vec3 lookDir = player.getLookAngle().normalize();

        ParticleOptions particles;

        if (UtilMethods.hasEnchantment(stack, ModEnchantments.ASHES_ASHES.get())) {
            particles = ModParticles.DEATH_CLOUD.get();
        } else {
            particles = ModParticles.PLAGUE_CLOUD.get();
        }

        double range = 5.0;
        double maxWidth = 6.0;
        double halfWidth = maxWidth / 2.0;

        int particleSteps = 10;

        Vec3 up = Math.abs(lookDir.y) < 0.99
                ? new Vec3(0, 1, 0)
                : new Vec3(1, 0, 0);

        Vec3 right = lookDir.cross(up).normalize();
        Vec3 upPerp = right.cross(lookDir).normalize();

        for (int i = 1; i <= particleSteps; i++) {
            double t = i / (double) particleSteps;
            double currentRange = range * t;
            double currentWidth = halfWidth * t;
            double visualWidth = currentWidth * 1.25;

            Vec3 center = origin.add(lookDir.scale(currentRange + 0.4));

            int particlesPerStep = Mth.clamp((int) (6 * t), 1, 6);

            for (int j = 0; j < particlesPerStep; j++) {
                double angle = level.random.nextDouble() * Math.PI * 2;

                double edgeBias = Mth.lerp(t, 0.3, 1.0);
                double radius = Math.pow(level.random.nextDouble(), edgeBias) * visualWidth;

                double verticalBias = (level.random.nextDouble() - 0.5) * 0.5;
                Vec3 offset = right.scale(Math.cos(angle) * radius)
                        .add(upPerp.scale(verticalBias * visualWidth));

                Vec3 pos = center.add(offset);
                Vec3 forward = lookDir.normalize();
                double edgeFactor = radius / visualWidth;
                Vec3 forwardVel = forward.scale(0.03 + 0.05 * t);
                Vec3 outward = offset.normalize().scale(0.03 * edgeFactor);
                Vec3 jitter = right.scale((level.random.nextDouble() - 0.5) * 0.06)
                        .add(upPerp.scale((level.random.nextDouble() - 0.5) * 0.06));

                Vec3 velocity = forwardVel.add(outward).add(jitter);

                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(particles,
                            pos.x, pos.y, pos.z, 0, velocity.x, velocity.y, velocity.z, 1.0);
                }
            }
        }
    }

    public static void maybeSpawnPlagueRat(ItemStack stack, Player player) {
        if (player.level().isClientSide) return;
        if (player.getRandom().nextInt(3) != 0) return;

        ServerLevel level = (ServerLevel) player.level();
        PlagueRatEntity rat = new PlagueRatEntity(ModEntities.PLAGUE_RAT.get(), level);

        rat.moveTo(player.getX(), player.getY() + 0.1, player.getZ(), level.random.nextFloat() * 360F, 0);
        rat.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(getAdjustedEffectLevel(stack) + 1);
        rat.tame(player);
        rat.setOwnerUUID(player.getUUID());
        rat.setLifespan(200, false);
        level.addFreshEntity(rat);
    }


    /**
     * When a blight is held in either hand, it will reduce the duration of incoming negative effects, up to 80%.
     */
    @SubscribeEvent
    public static void shortenNegativeEffectDuration(MobEffectEvent.Added event) {

        LivingEntity entity = event.getEntity();
        MobEffectInstance instance = event.getEffectInstance();
        MobEffect effect = instance.getEffect();

        if (effect.isBeneficial() || effect.isInstantenous()) return;
        if (instance.isInfiniteDuration()) return;
        if (entity.getPersistentData().getBoolean("aa_blight_effect_processing")) return;

        BlightItem blight = getHighestLevelHeldBlight(entity);
        if (blight == null) return;

        ArtificersArmory.queueServerWork(3, () -> applyReducedEffect(entity, instance, blight));
    }

    private static BlightItem getHighestLevelHeldBlight(LivingEntity entity) {
        ItemStack main = entity.getMainHandItem();
        ItemStack off = entity.getOffhandItem();

        BlightItem m = main.getItem() instanceof BlightItem b ? b : null;
        BlightItem o = off.getItem() instanceof BlightItem b ? b : null;

        if (m != null && o != null) {
            return m.effectLevel >= o.effectLevel ? m : o;
        }
        return m != null ? m : o;
    }

    public static void applyReducedEffect(LivingEntity entity, MobEffectInstance instance, BlightItem blight) {
        if (entity.getPersistentData().getBoolean("aa_blight_effect_processing")) return;
        entity.getPersistentData().putBoolean("aa_blight_effect_processing", true);

        try {
            MobEffect effect = instance.getEffect();
            MobEffectInstance current = entity.getEffect(effect);
            if (current == null || current.isInfiniteDuration()) return;
            float reduction = Math.min(blight.negativeEffectResistance, maxEffectReduction);
            int originalDuration = current.getDuration();
            int newDuration = (int)(originalDuration * (1.0f - reduction));
            if (newDuration <= 0) return;

            //System.out.println("Reduced Duration: " + newDuration / 20);

            entity.removeEffect(effect);
            entity.addEffect(new MobEffectInstance(effect, newDuration, current.getAmplifier(), current.isAmbient(), current.isVisible(), current.showIcon()));

        } finally {
            entity.getPersistentData().putBoolean("aa_blight_effect_processing", false);
        }
    }

    @SubscribeEvent
    public static void entityDieWithBlackDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Level level = entity.level();

        if (level.isClientSide) return;

        if (!entity.hasEffect(ModMobEffects.BLACK_DEATH.get())) return;

        blackDeathBurstEffect(entity);
    }

    public static void blackDeathBurstEffect(LivingEntity initialEntity) {
        Level level = initialEntity.level();
        if (level.isClientSide) return;

        CompoundTag data = initialEntity.getPersistentData();
        UUID ownerID = data.hasUUID("lastPlagueApplier") ? data.getUUID("lastPlagueApplier") : null;

        Queue<LivingEntity> toProcess = new ArrayDeque<>();
        toProcess.add(initialEntity);

        while (!toProcess.isEmpty()) {
            LivingEntity entity = toProcess.poll();

            MobEffectInstance instance = entity.getEffect(ModMobEffects.BLACK_DEATH.get());
            int amplifier = instance != null ? instance.getAmplifier() : 0;
            float burstDamage = (amplifier + 1) * 2.0f;
            double radius = 3.0;

            doDeathBurstVFX(entity);

            List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class,
                    entity.getBoundingBox().inflate(radius), target -> {
                        if (target == entity) return false;
                        if (!target.isAlive()) return false;

                        if (target instanceof Player player && player.getUUID().equals(ownerID)) return false;
                        if (target instanceof OwnableEntity ownable && ownerID != null) {
                            return !Objects.equals(ownable.getOwnerUUID(), ownerID);
                        }

                        return true;
                    });

            for (LivingEntity target : targets) {
                UtilMethods.damageEntity(target, DamageTypes.MAGIC, burstDamage);
                if (!target.isAlive()) {
                    ArtificersArmory.queueServerWork(20, () -> blackDeathBurstEffect(target));
                }
            }
        }
    }

    public static void doDeathBurstVFX(LivingEntity entity) {
        Level level = entity.level();
        if (!(level instanceof ServerLevel serverLevel)) return;

        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.ZOMBIE_INFECT, SoundSource.PLAYERS, 1F, 1.0F);

        double radius = 3.0;
        double yMin = entity.getY() + entity.getBbHeight() / 2.0 - 0.2;
        double yMax = entity.getY() + entity.getBbHeight() / 2.0 + 0.2;

        AABB box = new AABB(entity.getX() - radius, yMin, entity.getZ() - radius, entity.getX() + radius, yMax, entity.getZ() + radius);

        ParticleMethods.ParticlesInBox(serverLevel, box, ModParticles.DEATH_CLOUD.get(), 40);
    }
}

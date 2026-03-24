package net.trashelemental.artificers_armory.util.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.trashelemental.artificers_armory.Config;
import net.trashelemental.artificers_armory.entity.custom.FireballEntity;
import net.trashelemental.artificers_armory.item.custom.FirebrandItem;
import net.trashelemental.artificers_armory.junkyard_lib.util.UtilMethods;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.magic.effects.ModMobEffects;
import net.trashelemental.artificers_armory.magic.enchantments.ModEnchantments;
import net.trashelemental.artificers_armory.util.EnchantmentChecker;
import net.trashelemental.artificers_armory.util.ModTags;

import java.util.*;

public class FirebrandEvents {

    public static int getSoulBurnLevel(ItemStack stack) {
        int sources = 0;

        if (stack.getItem() instanceof FirebrandItem item) {
            if (item.isSoulFire) sources++;
            if (UtilMethods.hasEnchantment(stack, ModEnchantments.SOUL_BLAZE.get())) sources++;
        }
        if (sources == 0) return -1;
        return sources - 1;
    }

    public static int getAdjustedDamage(ItemStack stack) {
        if (stack.getItem() instanceof FirebrandItem item) {
            int powerLevel = UtilMethods.getEnchantmentLevel(stack, Enchantments.POWER_ARROWS);
            return item.projectileDamage + powerLevel;
        }
        return 0;
    }

    public static int getAdjustedBurnTime(ItemStack stack) {
        if (stack.getItem() instanceof FirebrandItem item) {
            int flame = UtilMethods.getEnchantmentLevel(stack, Enchantments.FLAMING_ARROWS);
            int fireAspect = UtilMethods.getEnchantmentLevel(stack, Enchantments.FIRE_ASPECT);
            int totalLevels = flame + fireAspect;

            float multiplier = 1.0F + (0.25F * totalLevels);
            return Math.max(1, Math.round(item.burnSeconds * multiplier));
        }
        return 0;
    }

    public static boolean canTargetBeAffected(LivingEntity entity, FirebrandItem item, Player player) {
        ItemStack stack = player.getUseItem();

        if (item.isSoulFire) {
            return true;
        }

        if (!entity.fireImmune()) {
            return true;
        }

        return UtilMethods.hasEnchantment(stack, ModEnchantments.SOUL_BLAZE.get());
    }

    public static ParticleOptions getParticles(ItemStack stack) {
        if (stack.getItem() instanceof FirebrandItem item) {
            if (item.isSoulFire || UtilMethods.hasEnchantment(stack, ModEnchantments.SOUL_BLAZE.get())) {
                return ParticleTypes.SOUL_FIRE_FLAME;
            }
        }
        return ParticleTypes.SMALL_FLAME;
    }

    /**
     * Creates a projectile and fires it in the direction the player is looking, passing on the damage and whether
     * the projectile should be Soul Fire. If the weapon is enchanted with Multishot, it will create additional
     * projectiles.
     */
    public static void performProjectileAttack(Player player, ItemStack stack) {
        if (player.level().isClientSide) return;

        Vec3 lookDirection = player.getLookAngle().normalize();
        if (!(stack.getItem() instanceof FirebrandItem item)) return;

        EnchantmentChecker check = new EnchantmentChecker();
        int bonusShots = check.checkEnchantmentLevel(stack, ModTags.Enchantments.MULTISHOT);

        int totalProjectiles = 1 + bonusShots;
        double spacing = 0.5;
        double startOffset = -((totalProjectiles - 1) / 2.0) * spacing;

        Vec3 up = Math.abs(lookDirection.y) > 0.99
                ? new Vec3(1, 0, 0)
                : new Vec3(0, 1, 0);
        Vec3 rightVector = lookDirection.cross(up).normalize();

        for (int i = 0; i < totalProjectiles; i++) {
            double offset = startOffset + i * spacing;
            Vec3 spawnPosition = player.position().add(0, player.getEyeHeight() - 0.5, 0).add(rightVector.scale(offset));

            FireballEntity projectile = new FireballEntity(player.level(), player, lookDirection,
                    getAdjustedDamage(stack), getAdjustedBurnTime(stack), item.isSoulFire);
            projectile.setPos(spawnPosition.x, spawnPosition.y, spawnPosition.z);

            if (item.isSoulFire || UtilMethods.hasEnchantment(stack, ModEnchantments.SOUL_BLAZE.get())) {
                projectile.setSoulFire(true);
            }

            projectile.setSoulBurnLevel(getSoulBurnLevel(stack));

            player.level().addFreshEntity(projectile);
        }

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 0.3F, 1.8F);
    }


    public static void handleChanneledAttack(Player player, ItemStack stack, int ticksUsed) {
        if (!(stack.getItem() instanceof FirebrandItem item)) return;

        if (UtilMethods.hasEnchantment(stack, ModEnchantments.CHARGE_BLAST.get())) {
            performChargeBlastAttack(player, item, ticksUsed);
            return;
        }

        if (UtilMethods.hasEnchantment(stack, ModEnchantments.FLAMETHROWER.get())) {
            performFlamethrowerAttack(player, item, ticksUsed);
            return;
        }

        if (UtilMethods.hasEnchantment(stack, ModEnchantments.WARMING_LIGHT.get())) {
            performWarmingLightAttack(player, item, ticksUsed);
        }
    }

    public static void performFlamethrowerAttack(Player player, FirebrandItem item, int ticksUsed) {

        ItemStack stack = player.getUseItem();

        int remaining = player.getUseItemRemainingTicks();
        int max = stack.getUseDuration();
        int elapsedTicks = max - remaining;

        if (elapsedTicks < 20) return;

        if (elapsedTicks % 8 != 0) doConeParticles(stack, player);

        int baseInterval = item.cooldownTime;
        if (UtilMethods.hasEnchantment(stack, Enchantments.MULTISHOT)) {
            baseInterval = (int) Math.round(baseInterval * 0.75);
        }
        if (elapsedTicks < baseInterval) return;
        if (elapsedTicks % baseInterval != 0) return;

        Level level = player.level();
        if (level.isClientSide) return;

        Vec3 origin = player.getEyePosition();
        Vec3 lookDir = player.getLookAngle().normalize();

        int flameLevel = UtilMethods.getEnchantmentLevel(stack, Enchantments.FLAMING_ARROWS);
        int fireAspectLevel = UtilMethods.getEnchantmentLevel(stack, Enchantments.FIRE_ASPECT);

        double range = 5.0 * (1.0 + 0.25 * (flameLevel + fireAspectLevel));
        double maxWidth = 5.0 * (1.0 + 0.25 * (flameLevel + fireAspectLevel));
        double halfWidth = maxWidth / 2.0;

        int damage = getAdjustedFlamethrowerDamage(stack);
        int burnSeconds = item.burnSeconds;

        AABB searchBox = player.getBoundingBox().inflate(range);

        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, searchBox)) {
            if (target == player) continue;
            if (!canTargetBeAffected(target, item, player)) continue;
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

            target.hurt(level.damageSources().playerAttack(player), damage);
            target.setSecondsOnFire(burnSeconds);
            applySoulBurn(target, stack);
        }

        BlockPos playerPos = player.blockPosition();
        int intRange = (int) Math.ceil(range);

        for (int x = -intRange; x <= intRange; x++) {
            for (int y = -intRange; y <= intRange; y++) {
                for (int z = -intRange; z <= intRange; z++) {
                    BlockPos pos = playerPos.offset(x, y, z);
                    BlockState state = level.getBlockState(pos);

                    if (!(state.is(Blocks.SNOW) || state.is(Blocks.SNOW_BLOCK)
                            || state.is(Blocks.POWDER_SNOW) || state.is(Blocks.ICE)
                            || state.is(Blocks.COBWEB))) continue;

                    Vec3 blockCenter = Vec3.atCenterOf(pos);
                    Vec3 toBlock = blockCenter.subtract(origin);
                    double distance = toBlock.length();
                    if (distance > range) continue;

                    double dot = lookDir.normalize().dot(toBlock.normalize());
                    if (dot < 0.7) continue;

                    if (level.random.nextFloat() < 0.7f) {
                        level.removeBlock(pos, false);
                    }
                }
            }
        }

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 0.1F, 1.8F);
        stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
    }

    public static void performWarmingLightAttack(Player player, FirebrandItem item, int ticksUsed) {
        ItemStack stack = player.getUseItem();
        if (!(stack.getItem() instanceof FirebrandItem)) return;

        int baseInterval = item.cooldownTime;
        if (UtilMethods.hasEnchantment(stack, Enchantments.MULTISHOT)) {
            baseInterval = (int) Math.round(baseInterval * 0.75);
        }

        if (!player.level().isClientSide && ticksUsed % 10 == 0) {
            spawnWarmingLightAura(player, stack, 8.0);
        }

        if (ticksUsed < baseInterval) return;
        if (ticksUsed % baseInterval != 0) return;

        Level level = player.level();
        if (level.isClientSide) return;

        int flameLevel = UtilMethods.getEnchantmentLevel(stack, Enchantments.FLAMING_ARROWS);
        int fireAspectLevel = UtilMethods.getEnchantmentLevel(stack, Enchantments.FIRE_ASPECT);

        double radius = 8.0 + fireAspectLevel + flameLevel;
        AABB area = player.getBoundingBox().inflate(radius);

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, area)) {
            applyWarmingLightEffect(player, stack, entity);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1F, 1.0F);
        stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
    }

    public static final int MAX_CHARGE_TICKS = 100;
    public static final float MIN_MULTIPLIER = 1.0f;
    public static final float MAX_MULTIPLIER = 5.0f;
    public static final float MIN_EFFECTIVE_CHARGE = 0.1f;

    private static final Map<UUID, Integer> CHARGE_TICKS = new HashMap<>();
    private static final Set<UUID> FULL_CHARGE_PLAYED = new HashSet<>();

    public static void performChargeBlastAttack(Player player, FirebrandItem item, int ticksUsed) {
        UUID id = player.getUUID();
        int charge = CHARGE_TICKS.getOrDefault(id, 0);
        ItemStack stack = player.getUseItem();

        int effectiveMaxCharge;
        int flameLevel = UtilMethods.getEnchantmentLevel(stack, Enchantments.FLAMING_ARROWS);
        int fireAspectLevel = UtilMethods.getEnchantmentLevel(stack, Enchantments.FIRE_ASPECT);

        float reduction = 0.15f * (fireAspectLevel + flameLevel);
        effectiveMaxCharge = Math.max(1, (int) (MAX_CHARGE_TICKS * (1.0f - reduction)));

        charge = Math.min(charge + 1, effectiveMaxCharge);
        CHARGE_TICKS.put(id, charge);

        if (!player.level().isClientSide && charge % 5 == 0) {
            ParticleMethods.ParticlesAroundServerSide(player.level(), getParticles(stack),
                    player.getX(), player.getY(), player.getZ(), 4, 2);
        }

        if (!player.level().isClientSide && charge >= effectiveMaxCharge / 2) {
            ParticleMethods.ParticlesAroundServerSide(player.level(), ParticleTypes.SMOKE,
                    player.getX(), player.getY(), player.getZ(), 6, 2);
            ParticleMethods.ParticlesAroundServerSide(player.level(), getParticles(stack),
                    player.getX(), player.getY(), player.getZ(), 4, 1.5);
        }

        if (charge >= effectiveMaxCharge && FULL_CHARGE_PLAYED.add(id)) {
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 0.8F, 1.2F);
            ParticleMethods.ParticlesAroundServerSide(player.level(), ParticleTypes.LAVA,
                    player.getX(), player.getEyeY(), player.getZ(), 5, 1);
        }
    }


    public static void doConeParticles(ItemStack stack, Player player) {

        Level level = player.level();
        if (level.isClientSide) return;

        Vec3 origin = player.getEyePosition();
        Vec3 lookDir = player.getLookAngle().normalize();

        double range = 5.0;
        double maxWidth = 6.0;
        double halfWidth = maxWidth / 2.0;

        ParticleOptions particle = getParticles(stack);

        int particleSteps = 6;

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

            int particlesPerStep = Mth.clamp((int) (6 * t), 1, 1);

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
                Vec3 outward = offset.normalize().scale(0.1 * edgeFactor);
                Vec3 forwardVel = forward.scale(0.12 + 0.18 * t);
                Vec3 jitter = right.scale((level.random.nextDouble() - 0.5) * 0.03 * t)
                        .add(upPerp.scale((level.random.nextDouble() - 0.5) * 0.03 * t));
                Vec3 velocity = forwardVel.add(outward).add(jitter);

                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(particle, pos.x, pos.y, pos.z, 0, velocity.x, velocity.y, velocity.z, 1.0);
                }
            }
        }
    }

    /**
     * If an entity is allied to the player holding the Firebrand, heal them or provide the Absorption effect if needed.
     * If the entity is a monster, damage it.
     */
    public static void applyWarmingLightEffect(Player player, ItemStack stack, LivingEntity entity) {

        ItemStack useItem = player.getUseItem();
        if (!(useItem.getItem() instanceof FirebrandItem item)) return;

        int adjustedDamage = getAdjustedWarmingLightDamage(stack);

        int healAmount = adjustedDamage;
        int damageAmount = Math.max(1, adjustedDamage / 3);

        boolean isHostilePlayer = false;
        if (entity instanceof Player otherPlayer) {
            isHostilePlayer = player.getLastAttacker() == otherPlayer;
        }

        if (isAlly(player, entity) && !isHostilePlayer) {
            float health = entity.getHealth();
            MobEffectInstance absorption = new MobEffectInstance(MobEffects.ABSORPTION, 20 * healAmount, Math.max(0, healAmount / 3));

            if (health < entity.getMaxHealth()) {
                entity.heal(healAmount);
            } else if (!entity.hasEffect(MobEffects.ABSORPTION)) {
                entity.addEffect(absorption);
            }
            ParticleMethods.ParticlesAroundServerSide(player.level(), ParticleTypes.HAPPY_VILLAGER,
                    entity.getX(), entity.getY(), entity.getZ(), 4, 2);
        }

        else if ((entity instanceof Mob mob && mob.getTarget() == player || isHostilePlayer)
                && canTargetBeAffected(entity, item, player)) {
            UtilMethods.damageEntity(entity, DamageTypes.MAGIC, damageAmount);
            if (entity.getRemainingFireTicks() <= (item.burnSeconds * 20)) {
                entity.setRemainingFireTicks(item.burnSeconds * 20);
            } else {
                entity.setSecondsOnFire(item.burnSeconds);
            }
            applySoulBurn(entity, stack);
            ParticleMethods.ParticlesAroundServerSide(player.level(), getParticles(stack),
                    entity.getX(), entity.getEyeY(), entity.getZ(), 10, 2);
        }
    }

    public static boolean isAlly(Player player, LivingEntity entity) {
        if (entity == player) return true;
        if (entity instanceof Player otherPlayer) {
            if (!Config.SUPPORT_OTHER_PLAYERS.get()) return false;
            if (player.getLastAttacker() == otherPlayer) return false;
            if (otherPlayer.getLastAttacker() == player) return false;
        }
        if (entity instanceof OwnableEntity ownable && ownable.getOwnerUUID() == player.getUUID()) return true;
        return entity.isAlliedTo(player);
    }

    public static void spawnWarmingLightAura(Player player, ItemStack stack, double radius) {
        Level level = player.level();

        int particleCount = 20;

        for (int i = 0; i < particleCount; i++) {
            double angle = level.random.nextDouble() * Math.PI * 2;
            double distance = level.random.nextDouble() * radius;

            double x = player.getX() + Math.cos(angle) * distance;
            double z = player.getZ() + Math.sin(angle) * distance;

            double y = player.getY() + 0.05 + level.random.nextDouble() * 0.15;

            double dx = 0.0;
            double dy = 0.005 + level.random.nextDouble() * 0.01;
            double dz = 0.0;

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(getParticles(stack), x, y, z, 1, dx, dy, dz, 0.0);
            }
        }
    }

    public static float getChargeProgress(float chargeTicks) {
        return Math.min(chargeTicks / MAX_CHARGE_TICKS, 1.0f);
    }

    public static float getChargeMultiplier(int chargeTicks) {
        float t = Math.min(chargeTicks / (float) MAX_CHARGE_TICKS, 1.0f);

        if (t < MIN_EFFECTIVE_CHARGE) {
            return 1.0f;
        }

        float adjustedT = (t - MIN_EFFECTIVE_CHARGE) / (1.0f - MIN_EFFECTIVE_CHARGE);
        return Mth.lerp(adjustedT, MIN_MULTIPLIER, MAX_MULTIPLIER);
    }

    public static void releaseChargeShot(Player player, ItemStack stack) {

        if (!(stack.getItem() instanceof FirebrandItem item)) return;

        UUID id = player.getUUID();
        int charge = CHARGE_TICKS.getOrDefault(id, 0);

        if (charge <= 0) return;

        float multiplier = getChargeMultiplier(charge);
        float progress = getChargeProgress(charge);

        CHARGE_TICKS.remove(id);
        FULL_CHARGE_PLAYED.remove(id);

        if (player.level().isClientSide) return;

        spawnChargedFireball(player, stack, multiplier, progress);
        player.getCooldowns().addCooldown(stack.getItem(), item.cooldownTime);
    }

    public static void spawnChargedFireball(Player player, ItemStack stack, float multiplier, float progress) {
        if (player.level().isClientSide) return;
        if (!(stack.getItem() instanceof FirebrandItem item)) return;

        Vec3 lookDirection = player.getLookAngle().normalize();
        float pitch = Mth.lerp(progress, 1.8f, 0.6F);

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 0.3F, pitch);

        int baseDamage = getAdjustedDamage(stack);
        int finalDamage = Mth.floor(baseDamage * multiplier);
        boolean shouldExplode = progress >= 0.5f;

        EnchantmentChecker check = new EnchantmentChecker();
        int bonusShots = check.checkEnchantmentLevel(stack, ModTags.Enchantments.MULTISHOT);
        int totalProjectiles = 1 + bonusShots;
        double baseSpacing = 0.5;
        double spacing = baseSpacing * (multiplier * 0.5);
        double startOffset = -((totalProjectiles - 1) / 2.0) * spacing;

        Vec3 up = Math.abs(lookDirection.y) > 0.99 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
        Vec3 rightVector = lookDirection.cross(up).normalize();

        for (int i = 0; i < totalProjectiles; i++) {
            double offset = startOffset + i * spacing;
            Vec3 spawnPos = player.position().add(0, player.getEyeHeight() - 0.5, 0).add(rightVector.scale(offset));

            FireballEntity projectile = new FireballEntity(player.level(), player, lookDirection,
                    finalDamage, getAdjustedBurnTime(stack), item.isSoulFire);
            projectile.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
            projectile.setSoulFire(item.isSoulFire || UtilMethods.hasEnchantment(stack, ModEnchantments.SOUL_BLAZE.get()));
            projectile.setSoulBurnLevel(getSoulBurnLevel(stack));
            projectile.setDamage(finalDamage);
            projectile.setShouldExplode(shouldExplode);
            projectile.setSize(multiplier);

            player.level().addFreshEntity(projectile);
        }
    }

    public static int getAdjustedFlamethrowerDamage(ItemStack stack) {
        if (!(stack.getItem() instanceof FirebrandItem item)) return 0;

        int powerLevel = UtilMethods.getEnchantmentLevel(stack, Enchantments.POWER_ARROWS);
        int flamethrowerLevel = UtilMethods.getEnchantmentLevel(stack, ModEnchantments.FLAMETHROWER.get());
        int reductionFactor = Math.max(1, 3 - flamethrowerLevel);
        int scaledDamage = Math.max(1, item.projectileDamage / reductionFactor);
        int maxAllowed = Math.max(1, (int) Math.floor(item.projectileDamage * 0.75f));
        int cappedDamage = Math.min(scaledDamage, maxAllowed);

        return cappedDamage + powerLevel;
    }

    public static int getAdjustedWarmingLightDamage(ItemStack stack) {
        if (!(stack.getItem() instanceof FirebrandItem item)) return 0;

        int powerLevel = UtilMethods.getEnchantmentLevel(stack, Enchantments.POWER_ARROWS);
        int warmingLightLevel = UtilMethods.getEnchantmentLevel(stack, ModEnchantments.WARMING_LIGHT.get());
        int baseDamage = item.projectileDamage + powerLevel;
        int reductionFactor = Math.max(1, 4 - warmingLightLevel);

        return Math.max(1, baseDamage / reductionFactor);
    }

    public static void applySoulBurn(LivingEntity entity, ItemStack stack) {
        if (!(stack.getItem() instanceof FirebrandItem item)) return;

        int level = getSoulBurnLevel(stack);
        if (level < 0) return;

        MobEffect effect = ModMobEffects.SOUL_BURN.get();
        MobEffectInstance soulBurn = new MobEffectInstance(effect, item.burnSeconds * 20, level, false, false);

        MobEffectInstance existing = entity.getEffect(effect);
        if (existing == null || existing.getAmplifier() < level) {
            entity.addEffect(soulBurn);
        }
    }

}

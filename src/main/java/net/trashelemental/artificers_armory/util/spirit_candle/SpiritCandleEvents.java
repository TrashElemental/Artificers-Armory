package net.trashelemental.artificers_armory.util.spirit_candle;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.trashelemental.artificers_armory.entity.custom.OwnableMinion;
import net.trashelemental.artificers_armory.item.custom.SpiritCandleItem;
import net.trashelemental.artificers_armory.junkyard_lib.util.UtilMethods;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.magic.effects.ModMobEffects;
import net.trashelemental.artificers_armory.magic.enchantments.ModEnchantments;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class SpiritCandleEvents {

    public static boolean canSpawnAt(Level level, BlockPos pos) {
        BlockPos above = pos.above();
        return level.getBlockState(pos).isAir() && level.getBlockState(above).isAir() &&
                level.getBlockState(pos.below()).isSolid();
    }

    public static int getMaxAllowedMinions(Player player) {
        int max = 0;

        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();

        if (main.getItem() instanceof SpiritCandleItem candle) {
            max += candle.maxSummons;
        }

        if (off.getItem() instanceof SpiritCandleItem candle) {
            max += candle.maxSummons;
        }

        return max;
    }

    public static int getAdjustedActiveCooldown(ItemStack stack) {
        if (!(stack.getItem() instanceof SpiritCandleItem item)) return 0;

        int efficiencyLevel = UtilMethods.getEnchantmentLevel(stack, Enchantments.BLOCK_EFFICIENCY);
        if (efficiencyLevel <= 0) return item.cooldown;

        double cooldownReduction = 0.15 * efficiencyLevel;
        cooldownReduction = Math.min(cooldownReduction, 0.9);

        return (int) Math.max(1, item.cooldown * (1.0 - cooldownReduction));
    }

    public static float getBonusChanceForEquipmentAndEnchantment(ItemStack stack) {
        if (!(stack.getItem() instanceof SpiritCandleItem item)) return 0;

        int fortuneLevel = UtilMethods.getEnchantmentLevel(stack, Enchantments.BLOCK_FORTUNE);
        return (float) Math.min(0.9, 0.15 * fortuneLevel);
    }

    public static int getAdjustedMinionDamage(ItemStack stack) {
        if (!(stack.getItem() instanceof SpiritCandleItem item)) return 0;
        int powerLevel = UtilMethods.getEnchantmentLevel(stack, Enchantments.POWER_ARROWS);

        return item.summonDamage + powerLevel;
    }

    public static double getAdjustedMinionHealth(ItemStack stack) {
        if (!(stack.getItem() instanceof SpiritCandleItem item)) return 0;
        int protectionLevel = UtilMethods.getEnchantmentLevel(stack, Enchantments.ALL_DAMAGE_PROTECTION);

        return item.summonHealth + protectionLevel;
    }

    /**
     * While channeling, continually apply a supportive effect to each minion.
     * Additional hooks here for enchantments.
     */
    public static void doChanneling(Player player, ItemStack stack, int ticksUsed) {
        Level level = player.level();
        if (level.isClientSide) return;
        if (ticksUsed < 20) return;
        if (!(stack.getItem() instanceof SpiritCandleItem item)) return;
        int focusLevel = UtilMethods.getEnchantmentLevel(stack, ModEnchantments.FOCUS.get());
        int lifedrainLevel = UtilMethods.getEnchantmentLevel(stack, ModEnchantments.LIFEDRAIN.get());

        int drainInterval = 80;

        if (ticksUsed % drainInterval == 0) {
            stack.hurtAndBreak(1, player, p ->
                    p.broadcastBreakEvent(player.getUsedItemHand())
            );

            if (focusLevel > 0 && !player.isCreative()) {
                FoodData food = player.getFoodData();
                if (food.getFoodLevel() > 0) {
                    food.setFoodLevel(food.getFoodLevel() - 1);
                }
            }
        }

        int radius = 20;
        AABB box = player.getBoundingBox().inflate(radius);

        int amplifier = item.supportLevel;
        int baseHealing = (Math.max(1, item.supportLevel / 2));
        int adjustedAmplifier = amplifier + focusLevel * 2;
        int adjustedHealing = baseHealing + focusLevel * 2;

        List<Mob> minions = level.getEntitiesOfClass(Mob.class, box, mob -> mob instanceof OwnableMinion minion
                        && minion.getOwner() == player && mob.isAlive());

        for (Mob minion : minions) {
            minion.addEffect(new MobEffectInstance(ModMobEffects.EMPOWERED.get(), 5, adjustedAmplifier, true, false));
            if (ticksUsed % 40 == 0) {
                minion.heal(adjustedHealing);
                ParticleMethods.ParticlesAroundServerSide(level, ParticleTypes.SOUL,
                        minion.getX(), minion.getY(), minion.getZ(), 3, 1);
            }
        }

        if (lifedrainLevel > 0 && ticksUsed % 40 == 0) {
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, box, LivingEntity::isAlive);
            boolean healPlayer = false;

            for (LivingEntity entity : entities) {
                if (doLifedrainEnchant(entity, player, lifedrainLevel)) {
                    healPlayer = true;
                }
            }

            if (healPlayer) {
                player.heal(lifedrainLevel);
            }
        }
    }

    /**
     * If the player is looking at a valid target, 'mark' it for your minions to prioritize.
     */
    public static boolean tryMarkTarget(Player player, ItemStack stack) {
        Level level = player.level();
        if (level.isClientSide) return false;

        Vec3 eyePos = player.getEyePosition(1f);
        Vec3 lookVec = player.getLookAngle();
        double maxDistance = 30.0;

        AABB scanBox = player.getBoundingBox()
                .expandTowards(lookVec.scale(maxDistance))
                .inflate(0.5);
        List<LivingEntity> candidates = level.getEntitiesOfClass(
                LivingEntity.class, scanBox,
                e -> e.isAlive() && !isAlly(player, e)
        );

        LivingEntity closest = null;
        double closestDist = maxDistance;

        for (LivingEntity entity : candidates) {
            AABB bb = entity.getBoundingBox().inflate(0.1);
            Optional<Vec3> intersection = bb.clip(eyePos, eyePos.add(lookVec.scale(maxDistance)));

            if (intersection.isPresent()) {
                double distance = eyePos.distanceTo(intersection.get());
                if (distance < closestDist) {
                    HitResult hit = level.clip(new ClipContext(eyePos, intersection.get(),
                            ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                    if (hit.getType() == HitResult.Type.MISS) {
                        closest = entity;
                        closestDist = distance;
                    }
                }
            }
        }

        if (closest == null) return false;

        tryHexEnchant(closest, stack);

        int radius = 20;
        AABB box = player.getBoundingBox().inflate(radius);
        List<Mob> minions = level.getEntitiesOfClass(Mob.class, box,
                mob -> mob instanceof OwnableMinion minion && minion.getOwner() == player
        );

        for (Mob minion : minions) {
            minion.setTarget(null);
            minion.setTarget(closest);
        }

        ParticleMethods.ParticlesAroundServerSide(level, ParticleTypes.SONIC_BOOM,
                closest.getX(), closest.getEyeY(), closest.getZ(), 1, 0);

        return true;
    }


    @Nullable
    public static BlockPos findNearbySpawnPos(Level level, BlockPos center, int radius) {
        RandomSource random = level.getRandom();

        for (int i = 0; i < 20; i++) {
            int dx = random.nextInt(radius * 2 + 1) - radius;
            int dz = random.nextInt(radius * 2 + 1) - radius;

            BlockPos pos = center.offset(dx, 0, dz);

            pos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos);

            if (canSpawnAt(level, pos)) {
                return pos;
            }
        }

        return null;
    }

    public static boolean tryPassiveSummon(Player player, ItemStack stack, SpiritCandleItem item) {
        Level level = player.level();
        if (level.isClientSide) return false;

        int current = countOwnedMinions(player, 20);
        int maxAllowed = getMaxAllowedMinions(player);
        if (current >= maxAllowed) return false;

        BlockPos spawnPos = findNearbySpawnPos(level, player.blockPosition(), 5);
        if (spawnPos == null) return false;


        return spawnMinion(level, spawnPos, item, player, stack);
    }

    public static boolean tryActiveSummon(Player player, BlockPos pos, SpiritCandleItem item, ItemStack stack) {
        Level level = player.level();
        if (level.isClientSide) return false;
        if (pos == null) return false;

        int current = countOwnedMinions(player, 20);
        int maxAllowed = getMaxAllowedMinions(player);
        if (current >= maxAllowed) return false;

        return spawnMinion(level, pos, item, player, stack);
    }


    public static void applyEquipmentRules(Mob mob, EquipmentRules rules, RandomSource random, ResourceLocation entityId, @Nullable ItemStack stack) {
        float fortuneBonus = stack != null ? getBonusChanceForEquipmentAndEnchantment(stack) : 0f;

        // Checks to see if mobs get equipment at all
        float equipChance = Math.min(1.0f, rules.equipChance() + fortuneBonus);
        if (random.nextFloat() > equipChance) {
            return;
        }

        // Checks what equipment from the pool the mob is allowed to get.
        // If a mob can't have any equipment, ignore it
        Map<EquipmentSlot, List<EquipmentEntry>> bySlot = rules.equipmentPool()
                .stream().filter(entry -> entry.allowedEntities().contains(entityId))
                .collect(Collectors.groupingBy(EquipmentEntry::slot));

        boolean equippedAnything = false;

        // Chance to get an item for each slot
        for (Map.Entry<EquipmentSlot, List<EquipmentEntry>> entry : bySlot.entrySet()) {
            EquipmentSlot slot = entry.getKey();

            if (random.nextFloat() > 0.5f) continue;

            List<EquipmentEntry> options = entry.getValue();
            EquipmentEntry chosen = options.get(random.nextInt(options.size()));

            ItemStack copy = chosen.stack().copy();
            mob.setItemSlot(slot, copy);
            equippedAnything = true;

            // Per-item enchant roll
            float enchantChance = Math.min(1.0f, rules.enchantChance() + fortuneBonus);
            if (enchantChance > 0f && random.nextFloat() < enchantChance) {
                EnchantmentHelper.enchantItem(random, copy, random.nextInt(
                        rules.maxEnchantLevel() - rules.minEnchantLevel() + 1) + rules.minEnchantLevel(), false
                );
            }
        }

        // Guarantees mobs that proc'd an equipment roll will get at least one item
        if (!equippedAnything && !bySlot.isEmpty()) {
            List<EquipmentEntry> fallback = bySlot.values().stream().flatMap(List::stream).toList();

            EquipmentEntry forced = fallback.get(random.nextInt(fallback.size()));
            ItemStack copy = forced.stack().copy();
            mob.setItemSlot(forced.slot(), copy);

            float enchantChance = Math.min(1.0f, rules.enchantChance() + fortuneBonus);
            if (enchantChance > 0f && random.nextFloat() < enchantChance) {
                EnchantmentHelper.enchantItem(random, copy, random.nextInt(
                        rules.maxEnchantLevel() - rules.minEnchantLevel() + 1) + rules.minEnchantLevel(), false
                );
            }
        }
    }

    public static int countOwnedMinions(Player player, double radius) {
        Level level = player.level();
        AABB box = player.getBoundingBox().inflate(radius);

        return level.getEntitiesOfClass(Mob.class, box, mob -> mob instanceof OwnableMinion ownable &&
                        player.getUUID().equals(ownable.getOwnerUUID())).size();
    }

    public static boolean spawnMinion(Level level, BlockPos pos, SpiritCandleItem item, Player owner, ItemStack stack) {
        if (level.isClientSide) return false;
        EntityType<? extends Mob> type = item.tier.getRandomSummon(level.getRandom());

        Mob mob = type.create(level);
        if (mob == null) return false;

        mob.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, level.getRandom().nextFloat() * 360F, 0);
        if (mob instanceof OwnableMinion ownable) {
            ownable.setOwner(owner);
        }

        // Set max health and attack damage to correspond to the tier of Spirit Candle and related enchantments.
        double maxHealth = getAdjustedMinionHealth(stack);
        mob.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHealth);
        mob.setHealth((float) maxHealth);
        if (mob.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            mob.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(getAdjustedMinionDamage(stack));
        }
        if (mob.getAttribute(Attributes.ARMOR) != null) {
            mob.getAttribute(Attributes.ARMOR).setBaseValue(0);
        }
        if (mob.getAttribute(Attributes.ARMOR_TOUGHNESS) != null) {
            mob.getAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(0);
        }

        mob.finalizeSpawn((ServerLevelAccessor) level, level.getCurrentDifficultyAt(pos),
                MobSpawnType.MOB_SUMMONED, null, null);

        ResourceLocation entityId = BuiltInRegistries.ENTITY_TYPE.getKey(type);

        applyEquipmentRules(mob, item.tier.getEquipmentRule(), level.getRandom(), entityId, stack);
        level.addFreshEntity(mob);
        level.playSound(null, pos, SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 0.8F, 1.2F);
        ParticleMethods.ParticlesBurst(level, ParticleTypes.SOUL,
                mob.getX(), mob.getY() + 0.5, mob.getZ(), 5, 0.05);

        if (owner instanceof ServerPlayer serverPlayer) {
            UtilMethods.grantAdvancement(serverPlayer, "spirit_candle_raise_minion");
        }

        return true;
    }

    public static void despawnMinion(LivingEntity entity) {
        if (entity.level().isClientSide) return;

        entity.discard();
    }

    public static boolean isAlly(Player player, LivingEntity entity) {
        if (entity == player) return true;
        if (entity instanceof OwnableEntity ownable && ownable.getOwnerUUID() == player.getUUID()) return true;
        return entity.isAlliedTo(player);
    }

    public static void tryHexEnchant(LivingEntity entity, ItemStack stack) {

        if (!(stack.getItem() instanceof SpiritCandleItem)) return;
        int enchantLevel = UtilMethods.getEnchantmentLevel(stack, ModEnchantments.HEX.get());
        if (enchantLevel <= 0) return;

        int amplifier = enchantLevel - 1;
        int duration = 100 + (20 * enchantLevel);

        MobEffectInstance weakness = new MobEffectInstance(MobEffects.WEAKNESS, duration, amplifier, false, true);
        MobEffectInstance slowness = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, amplifier, false, true);
        MobEffectInstance fatigue = new MobEffectInstance(MobEffects.DIG_SLOWDOWN, duration, amplifier, false, true);

        entity.addEffect(weakness);
        entity.addEffect(slowness);
        entity.addEffect(fatigue);
    }

    public static boolean doLifedrainEnchant(LivingEntity entity, Player player, int damage) {
        boolean dealtDamage = false;

        boolean isHostilePlayer = false;
        if (entity instanceof Player otherPlayer) {
            isHostilePlayer = player.getLastAttacker() == otherPlayer;
        }

        if ((entity instanceof Monster && !isAlly(player, entity)) || isHostilePlayer) {
            UtilMethods.damageEntity(entity, DamageTypes.MAGIC, damage);
            ParticleMethods.ParticlesBurst(player.level(), ParticleTypes.ENCHANTED_HIT,
                    entity.getX(), entity.getEyeY(), entity.getZ(), 5, 0.5);
            dealtDamage = true;
        }

        return dealtDamage;
    }

}

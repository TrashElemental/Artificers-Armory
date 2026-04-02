package net.trashelemental.artificers_armory.util.event;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.trashelemental.artificers_armory.entity.ModEntities;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarAI;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarRole;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarSkin;
import net.trashelemental.artificers_armory.item.custom.ChimeItem;
import net.trashelemental.artificers_armory.junkyard_lib.util.UtilMethods;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.magic.effects.ModMobEffects;
import net.trashelemental.artificers_armory.magic.enchantments.ModEnchantments;
import net.trashelemental.artificers_armory.particle.ModParticles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ChimeEvents {

    public static void useChime(Level level, Player player, ItemStack stack) {

        if (!(stack.getItem() instanceof ChimeItem chime)) return;

        if (!player.isCrouching()) {
            if (!hasActiveFamiliar(level, player)) {
                spawnFamiliar(level, player, stack);
            } else {
                applySupport(player, chime);
            }
            stack.hurtAndBreak(1, player, (p_43296_) -> {
                p_43296_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        } else {
            tryTarget(level, player, stack);
        }

        level.playSound(null, player.getOnPos(), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS);
    }

    public static void tryTarget(Level level, Player player, ItemStack stack) {
        FamiliarEntity familiar = getActiveFamiliar(level, player);
        if (familiar == null) return;
        recallFamiliar(familiar);

        float range = 32.0f;
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        Vec3 end = eye.add(look.scale(range));
        AABB box = player.getBoundingBox().expandTowards(look.scale(range)).inflate(0.5);

        LivingEntity closest = null;
        double closestDist = range;

        List<LivingEntity> candidates = level.getEntitiesOfClass(LivingEntity.class, box,
                e -> e.isAlive() && e != player && e != familiar && !FirebrandEvents.isAlly(player, e));

        for (LivingEntity entity : candidates) {
            AABB bb = entity.getBoundingBox().inflate(0.1);
            Optional<Vec3> intersection = bb.clip(eye, end);

            if (intersection.isPresent()) {
                double distance = eye.distanceTo(intersection.get());
                if (distance < closestDist) {
                    HitResult blockHit = level.clip(new ClipContext(eye, intersection.get(),
                            ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                    if (blockHit.getType() == HitResult.Type.MISS) {
                        closest = entity;
                        closestDist = distance;
                    }
                }
            }
        }

        if (closest != null) {
            setTarget(familiar, closest);
            ParticleMethods.ParticleTrailEntityToEntity(player.level(), ModParticles.FAMILIAR_ATTENTION.get(),
                    player, closest, 10);

            stack.hurtAndBreak(1, player, (p_43296_) -> {
                p_43296_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
            return;
        }

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, box, Entity::isAlive);
        ItemEntity closestItem = null;
        closestDist = range;

        for (ItemEntity item : items) {
            AABB bb = item.getBoundingBox().inflate(0.25);
            Optional<Vec3> intersection = bb.clip(eye, end);

            if (intersection.isPresent()) {
                double distance = eye.distanceTo(intersection.get());
                if (distance < closestDist) {
                    HitResult blockHit = level.clip(new ClipContext(eye, intersection.get(),
                            ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
                    if (blockHit.getType() == HitResult.Type.MISS) {
                        closestItem = item;
                        closestDist = distance;
                    }
                }
            }
        }

        if (closestItem != null) {
            FamiliarAI.forceFetchItem(familiar, closestItem);
            ParticleMethods.ParticleTrailEntityToEntity(player.level(), ModParticles.FAMILIAR_ATTENTION.get(),
                    player, closestItem, 10);

            stack.hurtAndBreak(1, player, (p_43296_) -> {
                p_43296_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        }
    }

    public static void recallFamiliar(FamiliarEntity familiar) {
        if (!(familiar.getOwner() instanceof Player owner)) return;
        familiar.getNavigation().stop();
        familiar.setTarget(null);

        if (familiar.isTaskActive() && familiar.getCurrentTask().isInterruptible()) {
            familiar.cancelCurrentTask();
        }

        familiar.triggerAnim("teleportIn", "TELEPORT_IN");
        familiar.teleportTo(owner.getX(), owner.getY() + 0.5, owner.getZ());
    }

    public static void setTarget(Mob mob, LivingEntity target) {
        mob.setTarget(target);
    }


    public static boolean hasActiveFamiliar(Level level, Player player) {
        return getActiveFamiliar(level, player) != null;
    }

    public static double getAdjustedMaxHealth(ItemStack stack) {
        if (stack.getItem() instanceof ChimeItem chime) {
            return chime.familiarHealth +
                    (UtilMethods.getEnchantmentLevel(stack, Enchantments.ALL_DAMAGE_PROTECTION) * 2) + //Add 2 for each level of protection
                    (UtilMethods.getEnchantmentLevel(stack, ModEnchantments.PROTECTOR.get()) * 15) + //Add 15 for each level of Protector
                    (UtilMethods.getEnchantmentLevel(stack, ModEnchantments.BRUISER.get()) * 10);   //Add 10 for each level of Bruiser
        }
        return 14;
    }

    public static int getAdjustedArmorPoints(ItemStack stack) {
        if (stack.getItem() instanceof ChimeItem chime) {
            return UtilMethods.getEnchantmentLevel(stack, Enchantments.ALL_DAMAGE_PROTECTION) +     //Add 1 for each level of protection
                    UtilMethods.getEnchantmentLevel(stack, ModEnchantments.PROTECTOR.get());        //Add 1 for each level of Protector
        }
        return 0;
    }

    public static int getAdjustedDamage(ItemStack stack) {
        if (stack.getItem() instanceof ChimeItem chime) {
            return chime.familiarDamage + (UtilMethods.getEnchantmentLevel(stack, Enchantments.POWER_ARROWS))   //Add 1 for each level of power
                    + (UtilMethods.getEnchantmentLevel(stack, ModEnchantments.BRUISER.get()));                  //Add 1 for each level of Bruiser
        }
        return 2;
    }

    public static void spawnFamiliar(Level level, Player player, ItemStack stack) {
        if (!(stack.getItem() instanceof ChimeItem chime)) return;
        FamiliarEntity familiar = ModEntities.FAMILIAR.get().create(level);

        if (familiar != null) {
            Vec3 spawnPos = findSafeSpawn(player, familiar);
            familiar.moveTo(spawnPos.x, spawnPos.y, spawnPos.z, player.getYRot(), 0);
            familiar.tame(player);

            if (UtilMethods.hasEnchantment(stack, Enchantments.INFINITY_ARROWS)) {
                familiar.setShouldDespawn(false);
            } else {
                familiar.setLifespan(chime.familiarLifespanSeconds * 20);
            }

            if (stack.hasCustomHoverName()) {
                familiar.setCustomName(stack.getHoverName());
                if (familiar.hasCustomName()) {
                    String name = ChatFormatting.stripFormatting(familiar.getName().getString());
                    familiar.setSkin(FamiliarSkin.fromName(name));
                }
            }

            level.addFreshEntity(familiar);

            FamiliarRole role = FamiliarRole.NONE;
            int enchantLevel = 0;

            if (UtilMethods.getEnchantmentLevel(stack, ModEnchantments.PROTECTOR.get()) > 0) {
                role = FamiliarRole.PROTECTOR;
                enchantLevel = UtilMethods.getEnchantmentLevel(stack, ModEnchantments.PROTECTOR.get());
                UtilMethods.grantAdvancement((ServerPlayer) player, "protector_summon");
            }
            else if (UtilMethods.getEnchantmentLevel(stack, ModEnchantments.HEALER.get()) > 0) {
                role = FamiliarRole.HEALER;
                enchantLevel = UtilMethods.getEnchantmentLevel(stack, ModEnchantments.HEALER.get());
                UtilMethods.grantAdvancement((ServerPlayer) player, "healer_summon");
            }
            else if (UtilMethods.getEnchantmentLevel(stack, ModEnchantments.PRANKSTER.get()) > 0) {
                role = FamiliarRole.PRANKSTER;
                enchantLevel = UtilMethods.getEnchantmentLevel(stack, ModEnchantments.PRANKSTER.get());
                UtilMethods.grantAdvancement((ServerPlayer) player, "prankster_summon");
            }
            else if (UtilMethods.getEnchantmentLevel(stack, ModEnchantments.BRUISER.get()) > 0) {
                role = FamiliarRole.BRUISER;
                enchantLevel = UtilMethods.getEnchantmentLevel(stack, ModEnchantments.BRUISER.get());
                UtilMethods.grantAdvancement((ServerPlayer) player, "bruiser_summon");
            }

            familiar.setRole(role);
            familiar.setEnchantLevel(enchantLevel);

            if (!stack.hasCustomHoverName() || familiar.getSkin() == FamiliarSkin.DEFAULT) {
                switch (role) {
                    case PROTECTOR -> familiar.setSkin(FamiliarSkin.PROTECTOR);
                    case HEALER -> familiar.setSkin(FamiliarSkin.HEALER);
                    case PRANKSTER -> familiar.setSkin(FamiliarSkin.PRANKSTER);
                    case BRUISER -> familiar.setSkin(FamiliarSkin.BRUISER);
                }
            }

            familiar.getAttribute(Attributes.MAX_HEALTH).setBaseValue(getAdjustedMaxHealth(stack));
            familiar.setHealth((float) getAdjustedMaxHealth(stack));
            familiar.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(getAdjustedDamage(stack));
            familiar.getAttribute(Attributes.ARMOR).setBaseValue(getAdjustedArmorPoints(stack));
            familiar.setLevel(chime.familiarLevel);

            if (player instanceof ServerPlayer serverPlayer) {
                UtilMethods.grantAdvancement(serverPlayer, "chime_summon");
            }

            FamiliarEntity old = getActiveFamiliar(level, player);

            if (old != null) {
                old.discard();
            }

            registerFamiliar(player, familiar);

        }
    }

    private static Vec3 findSafeSpawn(Player player, FamiliarEntity familiar) {
        Level level = player.level();
        Vec3 look = player.getLookAngle();
        Vec3 base = player.position().add(look.scale(1.5));

        List<Vec3> offsets = List.of(
                new Vec3(0, 0, 0), new Vec3(0.8, 0, 0), new Vec3(-0.8, 0, 0),
                new Vec3(0, 0, 0.8), new Vec3(0, 0, -0.8), new Vec3(0.8, 0, 0.8),
                new Vec3(-0.8, 0, -0.8), new Vec3(0, 0.6, 0));

        for (Vec3 offset : offsets) {
            Vec3 pos = base.add(offset);
            AABB box = familiar.getBoundingBox().move(pos.subtract(familiar.position()));
            if (level.noCollision(box)) {
                return pos;
            }
        }

        return player.position().add(0, 1, 0);
    }

    public static void registerFamiliar(Player player, FamiliarEntity familiar) {
        player.getPersistentData().putUUID("artificers_armory_familiar", familiar.getUUID());
    }

    public static FamiliarEntity getActiveFamiliar(Level level, Player player) {
        if (!(level instanceof ServerLevel server)) return null;
        CompoundTag tag = player.getPersistentData();
        if (!tag.hasUUID("artificers_armory_familiar")) return null;
        UUID id = tag.getUUID("artificers_armory_familiar");
        Entity entity = server.getEntity(id);

        if (entity instanceof FamiliarEntity familiar && familiar.isAlive()) {
            return familiar;
        }


        tag.remove("artificers_armory_familiar");

        return null;
    }

    public static void applySupport(Player player, ChimeItem chime) {
        if (player.level().isClientSide) return;
        int duration = 300;
        int amplifier = (Math.max(0, chime.supportLevel - 1));

        List<LivingEntity> allies = player.level().getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(8), e -> FirebrandEvents.isAlly(player, e));

        for (LivingEntity ally : allies) {
            UtilMethods.applyEffectNoParticles(ally, ModMobEffects.BLESSING.get(), duration, amplifier);
            ally.heal(chime.supportLevel * 2);
            ParticleMethods.ParticlesAroundServerSide(player.level(), ParticleTypes.HAPPY_VILLAGER,
                    ally.getX(), ally.getEyeY(), ally.getZ(), 4, 1.5);
        }
    }
}

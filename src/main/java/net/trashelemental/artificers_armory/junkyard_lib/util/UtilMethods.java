package net.trashelemental.artificers_armory.junkyard_lib.util;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.trashelemental.artificers_armory.ArtificersArmory;

import java.util.UUID;

public class UtilMethods {

    /**
     * Easy method for damaging entities without having to create a holder in the class.
     */
    public static void damageEntity(LivingEntity target, ResourceKey<DamageType> damageType, float amount) {
        if (target.level().isClientSide) return;
        Holder<DamageType> holder = target.level().registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(damageType);

        target.hurt(new DamageSource(holder), amount);
    }

    /**
     * Methods for applying and removing attribute modifiers to entities. You will
     * still need to create the modifier to reference it in this method.
     */
    public static void applyModifier(LivingEntity entity, Attribute attribute, AttributeModifier modifier) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null && !instance.hasModifier(modifier)) {
            instance.addPermanentModifier(modifier);
        }
    }

    public static void removeModifier(LivingEntity entity, Attribute attribute, UUID uuid) {
        AttributeInstance instance = entity.getAttribute(attribute);
        if (instance != null) {
            instance.removeModifier(uuid);
        }
    }

    /**
     * Easy advancement granter, just pass in the player to get the advancement and the string name of the advancement.
     */
    public static void grantAdvancement(ServerPlayer player, String advancementPath) {
        grantAdvancement(player, new ResourceLocation(ArtificersArmory.MOD_ID, advancementPath));
    }

    public static void grantAdvancement(ServerPlayer player, ResourceLocation advancementId) {
        var advancement = player.server.getAdvancements().getAdvancement(advancementId);
        if (advancement != null) {
            var progress = player.getAdvancements().getOrStartProgress(advancement);
            if (!progress.isDone()) {
                for (String criterion : progress.getRemainingCriteria()) {
                    player.getAdvancements().award(advancement, criterion);
                }
            }
        }
    }

    /**
     * Checks if an item has a specific enchantment.
     */
    public static boolean hasEnchantment(ItemStack stack, Enchantment enchantment) {
        return EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack) > 0;
    }

    /**
     * Gets the level of a specific enchantment.
     */
    public static int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
        return EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack);
    }

    /**
     * Allows you to apply an effect easily without overriding an existing one that would be stronger.
     */
    public static void applyEffectNoParticles(LivingEntity entity, MobEffect effect, int duration, int amplifier) {
        MobEffectInstance current = entity.getEffect(effect);
        if (current == null || current.getAmplifier() <= amplifier) {
            entity.addEffect(new MobEffectInstance(effect, duration, amplifier, false, false));
        }
    }

    /**
     * Allows you to apply an effect easily without overriding an existing one that would be stronger.
     */
    public static void applyEffectWithParticles(LivingEntity entity, MobEffect effect, int duration, int amplifier) {
        MobEffectInstance current = entity.getEffect(effect);
        if (current == null || current.getAmplifier() <= amplifier) {
            entity.addEffect(new MobEffectInstance(effect, duration, amplifier, false, true));
        }
    }

}

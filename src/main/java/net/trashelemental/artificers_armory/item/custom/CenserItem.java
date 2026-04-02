package net.trashelemental.artificers_armory.item.custom;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import net.trashelemental.artificers_armory.Config;
import net.trashelemental.artificers_armory.entity.ModEntities;
import net.trashelemental.artificers_armory.entity.custom.PotionCloudEntity;
import net.trashelemental.artificers_armory.junkyard_lib.util.UtilMethods;
import net.trashelemental.artificers_armory.magic.effects.ModMobEffects;
import net.trashelemental.artificers_armory.magic.enchantments.ModEnchantments;
import net.trashelemental.artificers_armory.util.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CenserItem extends AbstractWeaponItem{

    public final int lifespan;
    public final int amplifier;
    public final int duration;
    public final int cooldownTime;
    protected final int enchantmentValue;

    public CenserItem(Properties properties, double attackDamage, float attackSpeed, int lifespan, int amplifier, int duration, int cooldownTime, int enchantmentValue) {
        super(attackDamage, attackSpeed, properties);
        this.lifespan = lifespan ;
        this.amplifier = amplifier;
        this.duration = duration;
        this.cooldownTime = cooldownTime;
        this.enchantmentValue = enchantmentValue;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        List<MobEffectInstance> effects = getEffects(stack);

        if (effects.isEmpty()) {
            effects = new ArrayList<>(List.of(new MobEffectInstance(ModMobEffects.BLESSING.get(), getAdjustedDuration(stack), getAdjustedAmplifier(stack))));
            setEffects(stack, effects);
        }

        for (MobEffectInstance effectInstance : effects) {
            Component effectName = Component.translatable(effectInstance.getDescriptionId());
            int amplifier = getAdjustedAmplifier(stack);
            int durationSeconds = getAdjustedDuration(stack) / 20;
            ChatFormatting color = effectInstance.getEffect().isBeneficial() ? ChatFormatting.BLUE : ChatFormatting.RED;
            Component amplifierText = Component.literal(amplifier > 0 ? UtilMethods.toRoman(amplifier + 1) : "");
            Component durationText = Component.literal(UtilMethods.formatTimeFromSeconds(durationSeconds));

            components.add(Component.translatable("tooltip.artificers_armory.censer_effect",
                    effectName, amplifierText, durationText).withStyle(color));
        }

        components.add(Component.translatable("tooltip.artificers_armory.censer_cloud_duration",
                UtilMethods.formatTimeFromTicks(getAdjustedLifespan(stack))).withStyle(ChatFormatting.BLUE));

        if (ModList.get().isLoaded("irons_spellbooks") && Config.ISS_COMPAT.get()) {
            components.add(Component.translatable("tooltip.artificers_armory.iss_holy_spell_power",
                    formatPercent(Config.ISS_BOOST_AMOUNT.get())).withStyle(ChatFormatting.BLUE));
        }

        if (hasDescriptionRelevantEnchants(stack) && Config.ENCHANT_TOOLTIPS.get()) {
            if (!Screen.hasShiftDown()) {
                components.add(Component.translatable("tooltip.artificers_armory.hold_shift_enchant").withStyle(ChatFormatting.DARK_GRAY));
            } else {
                if (UtilMethods.hasEnchantment(stack, ModEnchantments.DISPERSAL.get())) {
                    components.add(Component.translatable("enchantment.artificers_armory.dispersal.desc").withStyle(ChatFormatting.BLUE));
                }
                if (UtilMethods.hasEnchantment(stack, ModEnchantments.PURIFYING.get())) {
                    components.add(Component.translatable("enchantment.artificers_armory.purifying.desc").withStyle(ChatFormatting.BLUE));
                }
                if (UtilMethods.hasEnchantment(stack, ModEnchantments.TRANSMUTATION.get())) {
                    components.add(Component.translatable("enchantment.artificers_armory.transmutation.desc").withStyle(ChatFormatting.BLUE));
                }
            }
        }

        super.appendHoverText(stack, level, components, flag);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ItemStack offhand = player.getOffhandItem();

        if (!(offhand.getItem() instanceof PotionItem) && !(offhand.getItem() instanceof MilkBucketItem)) {
            doProjectile(player, stack);

            player.getCooldowns().addCooldown(this, cooldownTime);
            stack.hurtAndBreak(1, player, (p_43296_) -> p_43296_.broadcastBreakEvent(EquipmentSlot.MAINHAND));
            player.swing(hand);

            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.3F, 1.2F);

            if (player instanceof ServerPlayer serverPlayer) {
                UtilMethods.grantAdvancement(serverPlayer, "censer_use");
            }
            return InteractionResultHolder.consume(stack);
        }

        if (offhand.getItem() instanceof PotionItem) {
            List<MobEffectInstance> originalEffects = PotionUtils.getMobEffects(offhand);
            List<MobEffectInstance> convertedEffects = new ArrayList<>();

            for (MobEffectInstance instance : originalEffects) {
                if (instance.getEffect().isInstantenous()) {
                    if (instance.getEffect() == MobEffects.HEAL) {
                        convertedEffects.add(new MobEffectInstance(MobEffects.REGENERATION, getAdjustedDuration(stack), getAdjustedInstantEffectAmplifier(stack)));
                    } else if (instance.getEffect() == MobEffects.HARM) {
                        convertedEffects.add(new MobEffectInstance(MobEffects.WITHER, getAdjustedDuration(stack), getAdjustedInstantEffectAmplifier(stack)));
                    } else {
                        player.displayClientMessage(Component.translatable("tooltip.artificers_armory.censer_effect_fail"), true);
                    }
                }
                else convertedEffects.add(new MobEffectInstance(instance.getEffect(), getAdjustedDuration(stack), getAdjustedAmplifier(stack)));
            }

            setEffects(stack, convertedEffects);

            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BREWING_STAND_BREW, SoundSource.PLAYERS, 0.3F, 1.5F);

            if (!player.isCreative()) {
                player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.GLASS_BOTTLE));
            }

            player.swing(hand);

            return InteractionResultHolder.consume(stack);
        }

        if (offhand.getItem() instanceof MilkBucketItem) {
            setEffects(stack, List.of(new MobEffectInstance(ModMobEffects.BLESSING.get(), getAdjustedDuration(stack), getAdjustedAmplifier(stack))));
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BREWING_STAND_BREW, SoundSource.PLAYERS, 0.3F, 1.5F);

            return InteractionResultHolder.consume(stack);
        }

        return super.use(level, player, hand);
    }


    public static int getAdjustedLifespan(ItemStack stack) {
        if (!(stack.getItem() instanceof CenserItem censer)) return 0;

        return (censer.lifespan * 20) + (100 * UtilMethods.getEnchantmentLevel(stack, ModEnchantments.DISPERSAL.get()));
    }

    public static int getAdjustedAmplifier(ItemStack stack) {
        if (!(stack.getItem() instanceof CenserItem censer)) return 0;

        return censer.amplifier;
    }

    public static int getAdjustedInstantEffectAmplifier(ItemStack stack) {
        if (!(stack.getItem() instanceof CenserItem censer)) return 0;

        return Math.max(0, getAdjustedAmplifier(stack) / 2);
    }

    public static int getAdjustedDuration(ItemStack stack) {
        if (!(stack.getItem() instanceof CenserItem censer)) return 0;

        return (censer.duration * 20) + (20 * UtilMethods.getEnchantmentLevel(stack, Enchantments.POWER_ARROWS));
    }

    public void doProjectile(Player player, ItemStack stack) {
        Level level = player.level();
        if (!(stack.getItem() instanceof CenserItem)) return;
        if (level.isClientSide) return;

        List<MobEffectInstance> effects = getEffects(stack);

        if (effects.isEmpty()) {
            effects = new ArrayList<>(List.of(new MobEffectInstance(ModMobEffects.BLESSING.get(), getAdjustedDuration(stack), getAdjustedAmplifier(stack))));
            setEffects(stack, effects);
        }

        Vec3 lookDirection = player.getLookAngle().normalize();
        Vec3 basePosition = player.position().add(0, player.getEyeHeight() - 0.5, 0);
        Vec3 spawnPosition = basePosition.add(lookDirection.scale(1.0));

        List<MobEffectInstance> stored = getEffects(stack);
        List<MobEffectInstance> adjustedEffects = new ArrayList<>();

        for (MobEffectInstance inst : stored) {
            adjustedEffects.add(new MobEffectInstance(inst.getEffect(), getAdjustedDuration(stack), inst.getAmplifier()));
        }

        PotionCloudEntity projectile = new PotionCloudEntity(ModEntities.POTION_CLOUD_ENTITY.get(), player, lookDirection, level,
                getAdjustedLifespan(stack), adjustedEffects);

        if (UtilMethods.hasEnchantment(stack, ModEnchantments.DISPERSAL.get())) projectile.setSize(1.5f);
        if (UtilMethods.hasEnchantment(stack, ModEnchantments.PURIFYING.get())) projectile.setPurifying(true);
        if (UtilMethods.hasEnchantment(stack, ModEnchantments.TRANSMUTATION.get())) projectile.setTransmutation(true);
        projectile.setPos(spawnPosition.x, spawnPosition.y, spawnPosition.z);
        projectile.setLifetime(getAdjustedLifespan(stack));
        player.level().addFreshEntity(projectile);
    }

    private static final String EFFECTS_TAG = "StoredEffects";

    public static void setEffects(ItemStack stack, List<MobEffectInstance> effects) {
        CompoundTag tag = stack.getOrCreateTag();
        ListTag list = new ListTag();
        for (MobEffectInstance effect : effects) {
            list.add(effect.save(new CompoundTag()));
        }
        tag.put(EFFECTS_TAG, list);
    }

    public static List<MobEffectInstance> getEffects(ItemStack stack) {
        List<MobEffectInstance> effects = new ArrayList<>();
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(EFFECTS_TAG)) return effects;
        ListTag list = tag.getList(EFFECTS_TAG, Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            effects.add(MobEffectInstance.load(list.getCompound(i)));
        }
        return effects;
    }

    /**
     * Enchanting
     */
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return isEnchantmentInTags(enchantment);
    }

    @Override
    public boolean isEnchantable(ItemStack pStack) {
        return true;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return this.enchantmentValue;
    }

    public static boolean isEnchantmentInTags(Enchantment enchantment) {
        Holder<Enchantment> holder = BuiltInRegistries.ENCHANTMENT.wrapAsHolder(enchantment);
        return holder.is(ModTags.Enchantments.CENSER_ALLOWED);
    }

    public static boolean hasDescriptionRelevantEnchants(ItemStack stack) {
        return UtilMethods.hasEnchantment(stack, ModEnchantments.DISPERSAL.get()) || UtilMethods.hasEnchantment(stack, ModEnchantments.PURIFYING.get())
                || UtilMethods.hasEnchantment(stack, ModEnchantments.TRANSMUTATION.get());
    }



    /**
     * ISS Compatibility
     */
    private static final ResourceLocation HOLY_SPELL_POWER = new ResourceLocation("irons_spellbooks", "holy_spell_power");

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {

        Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create(super.getAttributeModifiers(slot, stack));

        if ((slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) && ModList.get().isLoaded("irons_spellbooks")
                && Config.ISS_COMPAT.get()) {
            addISSAttribute(modifiers, HOLY_SPELL_POWER, Config.ISS_BOOST_AMOUNT.get(), "iss_holy_spell_power_bonus");
        }

        return modifiers;
    }

    private void addISSAttribute(Multimap<Attribute, AttributeModifier> modifiers, ResourceLocation attributeId, double amount, String uuidName) {

        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(attributeId);

        if (attribute != null && amount != 0) {
            AttributeModifier modifier =
                    new AttributeModifier(UUID.nameUUIDFromBytes(uuidName.getBytes()), uuidName, amount, AttributeModifier.Operation.MULTIPLY_TOTAL);
            modifiers.put(attribute, modifier);
        }
    }

    private String formatPercent(double value) {
        return String.valueOf((int) Math.round(value * 100));
    }
}

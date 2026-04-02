package net.trashelemental.artificers_armory.item.custom;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import net.trashelemental.artificers_armory.Config;
import net.trashelemental.artificers_armory.junkyard_lib.util.UtilMethods;
import net.trashelemental.artificers_armory.magic.enchantments.ModEnchantments;
import net.trashelemental.artificers_armory.util.ModTags;
import net.trashelemental.artificers_armory.util.event.BlightEvents;
import net.trashelemental.artificers_armory.util.event.FirebrandEvents;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class BlightItem extends AbstractWeaponItem{

    public final int effectLevel;
    public final int effectDuration;
    public final int cooldownTime;
    protected final int enchantmentValue;
    public final float negativeEffectResistance;
    public static int maxUseTime = 80;

    public BlightItem(Properties properties, double attackDamage, float attackSpeed, int effectLevel, int effectSeconds, int cooldownTime, int enchantmentValue, float negativeEffectResistance) {
        super(attackDamage, attackSpeed, properties);
        this.effectLevel = effectLevel;
        this.effectDuration = effectSeconds;
        this.cooldownTime = cooldownTime;
        this.enchantmentValue = enchantmentValue;
        this.negativeEffectResistance = negativeEffectResistance;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {

        components.add(Component.translatable("tooltip.artificers_armory.blight_plague",
                UtilMethods.toRoman(BlightEvents.getAdjustedEffectLevel(stack) + 1), UtilMethods.formatTimeFromSeconds(effectDuration))
                .withStyle(ChatFormatting.BLUE));
        components.add(Component.translatable("tooltip.artificers_armory.blight_area_damage",
                BlightEvents.getAdjustedAreaDamage(stack)).withStyle(ChatFormatting.BLUE));
        components.add(Component.translatable("tooltip.artificers_armory.blight_effect_reduction",
                formatPercent(Math.min(negativeEffectResistance, 0.8f))).withStyle(ChatFormatting.BLUE));

        if (ModList.get().isLoaded("irons_spellbooks") && Config.ISS_COMPAT.get()) {
            components.add(Component.translatable("tooltip.artificers_armory.iss_nature_spell_power",
                    formatPercent(Config.ISS_BOOST_AMOUNT.get())).withStyle(ChatFormatting.BLUE));
        }

        if (hasDescriptionRelevantEnchants(stack) && Config.ENCHANT_TOOLTIPS.get()) {
            if (!Screen.hasShiftDown()) {
                components.add(Component.translatable("tooltip.artificers_armory.hold_shift_enchant").withStyle(ChatFormatting.DARK_GRAY));
            } else {
                if (UtilMethods.hasEnchantment(stack, ModEnchantments.PESTILENCE.get())) {
                    components.add(Component.translatable("enchantment.artificers_armory.pestilence.desc").withStyle(ChatFormatting.BLUE));
                }
                if (UtilMethods.hasEnchantment(stack, ModEnchantments.DELIRIUM.get())) {
                    components.add(Component.translatable("enchantment.artificers_armory.delirium.desc").withStyle(ChatFormatting.BLUE));
                }
                if (UtilMethods.hasEnchantment(stack, ModEnchantments.ASHES_ASHES.get())) {
                    components.add(Component.translatable("enchantment.artificers_armory.ashes_ashes.desc").withStyle(ChatFormatting.BLUE));
                }
            }
        }

        super.appendHoverText(stack, level, components, flag);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        if (player.isCrouching()) {
            if (BlightEvents.consumePlagueEffect(player)) {
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
                player.getCooldowns().addCooldown(this, cooldownTime);
                player.swing(hand);
            }
        } else {
            player.startUsingItem(hand);

            if (player instanceof ServerPlayer serverPlayer) {
                UtilMethods.grantAdvancement(serverPlayer, "blight_use");
            }
        }

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return maxUseTime;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remaining) {
        if (level.isClientSide) return;
        if (!(entity instanceof Player player)) return;

        BlightEvents.blightUseItem(stack, player);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged) {
        if (level.isClientSide) return;
        if (!(entity instanceof Player player)) return;
        int elapsed = stack.getUseDuration() - timeCharged;

        if (elapsed < 20) return;

        player.getCooldowns().addCooldown(this, cooldownTime);
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
        return holder.is(ModTags.Enchantments.BLIGHT_ALLOWED);
    }

    public static boolean hasDescriptionRelevantEnchants(ItemStack stack) {
        return UtilMethods.hasEnchantment(stack, ModEnchantments.PESTILENCE.get()) ||
                UtilMethods.hasEnchantment(stack, ModEnchantments.ASHES_ASHES.get()) ||
                UtilMethods.hasEnchantment(stack, ModEnchantments.DELIRIUM.get());
    }



    /**
     * ISS Compatibility
     */
    private static final ResourceLocation HOLY_SPELL_POWER = new ResourceLocation("irons_spellbooks", "nature_spell_power");

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {

        Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create(super.getAttributeModifiers(slot, stack));

        if ((slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) && ModList.get().isLoaded("irons_spellbooks")
                && Config.ISS_COMPAT.get()) {
            addISSAttribute(modifiers, HOLY_SPELL_POWER, Config.ISS_BOOST_AMOUNT.get(), "iss_nature_spell_power_bonus");
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

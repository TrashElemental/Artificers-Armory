package net.trashelemental.artificers_armory.item.custom;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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
import net.trashelemental.artificers_armory.util.event.ChimeEvents;
import net.trashelemental.artificers_armory.util.spirit_candle.SpiritCandleEvents;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ChimeItem extends Item {
    public final int familiarLifespanSeconds;
    public final int familiarLevel;
    public final double familiarHealth;
    public final int familiarDamage;
    public final int supportLevel;
    public final int cooldown;
    public final int enchantmentValue;

    public ChimeItem(Properties pProperties, int familiarLifespanSeconds, int familiarLevel, double familiarHealth, int familiarDamage, int supportLevel, int cooldown, int enchantmentValue) {
        super(pProperties);
        this.familiarLifespanSeconds = familiarLifespanSeconds;
        this.familiarLevel = familiarLevel;
        this.familiarHealth = familiarHealth;
        this.familiarDamage = familiarDamage;
        this.supportLevel = supportLevel;
        this.cooldown = cooldown;
        this.enchantmentValue = enchantmentValue;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {

        int protectionLevel = UtilMethods.getEnchantmentLevel(stack, Enchantments.ALL_DAMAGE_PROTECTION);

        if (!UtilMethods.hasEnchantment(stack, Enchantments.INFINITY_ARROWS)) {
            components.add(Component.translatable("tooltip.artificers_armory.chime_lifespan", familiarLifespanSeconds / 60 + "m")
                    .withStyle(ChatFormatting.DARK_GREEN));
        } else {
            components.add(Component.translatable("tooltip.artificers_armory.chime_lifespan_infinity")
                    .withStyle(ChatFormatting.DARK_GREEN));
        }
        components.add(Component.translatable("tooltip.artificers_armory.chime_health",
                (int) ChimeEvents.getAdjustedMaxHealth(stack)).withStyle(ChatFormatting.DARK_GREEN));
        if (protectionLevel > 0) {
            components.add(Component.translatable("tooltip.artificers_armory.chime_armor", protectionLevel)
                    .withStyle(ChatFormatting.DARK_GREEN));
        }

        if (!UtilMethods.hasEnchantment(stack, ModEnchantments.HEALER.get()) && !UtilMethods.hasEnchantment(stack, ModEnchantments.PRANKSTER.get())) {
            components.add(Component.translatable("tooltip.artificers_armory.chime_damage",
                    ChimeEvents.getAdjustedDamage(stack)).withStyle(ChatFormatting.DARK_GREEN));
        }

        if (ModList.get().isLoaded("irons_spellbooks") && Config.ISS_COMPAT.get()) {
            components.add(Component.translatable("tooltip.artificers_armory.iss_evocation_spell_power",
                    formatPercent(Config.ISS_BOOST_AMOUNT.get())).withStyle(ChatFormatting.BLUE));
        }

        if (hasDescriptionRelevantEnchants(stack) && Config.ENCHANT_TOOLTIPS.get()) {
            if (!Screen.hasShiftDown()) {
                components.add(Component.translatable("tooltip.artificers_armory.hold_shift_enchant").withStyle(ChatFormatting.DARK_GRAY));
            } else {
                if (UtilMethods.hasEnchantment(stack, ModEnchantments.PROTECTOR.get())) {
                    components.add(Component.translatable("enchantment.artificers_armory.protector.desc").withStyle(ChatFormatting.BLUE));
                }
                if (UtilMethods.hasEnchantment(stack, ModEnchantments.HEALER.get())) {
                    components.add(Component.translatable("enchantment.artificers_armory.healer.desc").withStyle(ChatFormatting.BLUE));
                }
                if (UtilMethods.hasEnchantment(stack, ModEnchantments.PRANKSTER.get())) {
                    components.add(Component.translatable("enchantment.artificers_armory.prankster.desc").withStyle(ChatFormatting.BLUE));
                }
                if (UtilMethods.hasEnchantment(stack, ModEnchantments.BRUISER.get())) {
                    components.add(Component.translatable("enchantment.artificers_armory.bruiser.desc").withStyle(ChatFormatting.BLUE));
                }
            }
        }


        super.appendHoverText(stack, level, components, flag);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            ChimeEvents.useChime(level, player, stack);
        }

        player.getCooldowns().addCooldown(this, cooldown);

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
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
        return holder.is(ModTags.Enchantments.CHIME_ALLOWED);
    }

    public static boolean hasDescriptionRelevantEnchants(ItemStack stack) {
        return UtilMethods.hasEnchantment(stack, ModEnchantments.PROTECTOR.get()) ||
                UtilMethods.hasEnchantment(stack, ModEnchantments.HEALER.get()) ||
                UtilMethods.hasEnchantment(stack, ModEnchantments.PRANKSTER.get()) ||
                UtilMethods.hasEnchantment(stack, ModEnchantments.BRUISER.get());
    }

    /**
     * ISS Compatibility
     */
    private static final ResourceLocation EVOCATION_POWER = new ResourceLocation("irons_spellbooks", "evocation_spell_power");

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {

        Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create(super.getAttributeModifiers(slot, stack));

        if ((slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) && ModList.get().isLoaded("irons_spellbooks")
                && Config.ISS_COMPAT.get()) {
            addISSAttribute(modifiers, EVOCATION_POWER, Config.ISS_BOOST_AMOUNT.get(), "iss_evocation_spell_power_bonus");
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

package net.trashelemental.artificers_armory.item.custom;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
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
import net.trashelemental.artificers_armory.util.event.FirebrandEvents;
import net.trashelemental.artificers_armory.util.spirit_candle.SpiritCandleEvents;
import net.trashelemental.artificers_armory.util.spirit_candle.SpiritCandleTier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class SpiritCandleItem extends Item {

    public final int maxSummons;
    public final SpiritCandleTier tier;
    public final int cooldown;
    public final double summonHealth;
    public final int summonDamage;
    public final int enchantmentValue;
    public final int supportLevel;

    private static final Map<ItemStack, Integer> PASSIVE_TIMER = new WeakHashMap<>();

    public SpiritCandleItem(Properties pProperties, int maxSummons, SpiritCandleTier tier, int cooldown, double summonHealth, int summonDamage, int supportLevel, int enchantmentValue) {
        super(pProperties);
        this.maxSummons = maxSummons;
        this.tier = tier;
        this.cooldown = cooldown;
        this.summonHealth = summonHealth;
        this.summonDamage = summonDamage;
        this.enchantmentValue = enchantmentValue;
        this.supportLevel = supportLevel;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {

        components.add(Component.translatable("tooltip.artificers_armory.sc_minions", maxSummons).withStyle(ChatFormatting.DARK_GREEN));
        components.add(Component.translatable("tooltip.artificers_armory.sc_health",
                (int) SpiritCandleEvents.getAdjustedMinionHealth(stack)).withStyle(ChatFormatting.DARK_GREEN));
        components.add(Component.translatable("tooltip.artificers_armory.sc_damage",
                SpiritCandleEvents.getAdjustedMinionDamage(stack)).withStyle(ChatFormatting.DARK_GREEN));

        if (ModList.get().isLoaded("irons_spellbooks") && Config.ISS_COMPAT.get()) {
            components.add(Component.translatable("tooltip.artificers_armory.iss_blood_spell_power",
                    formatPercent(Config.ISS_BOOST_AMOUNT.get())).withStyle(ChatFormatting.BLUE));
        }

        if (hasDescriptionRelevantEnchants(stack) && Config.ENCHANT_TOOLTIPS.get()) {
            if (!Screen.hasShiftDown()) {
                components.add(Component.translatable("tooltip.artificers_armory.hold_shift_enchant").withStyle(ChatFormatting.DARK_GRAY));
            } else {
                if (UtilMethods.hasEnchantment(stack, Enchantments.BLOCK_FORTUNE)) {
                    components.add(Component.translatable("tooltip.artificers_armory.sc_fortune").withStyle(ChatFormatting.BLUE));
                }
                if (UtilMethods.hasEnchantment(stack, Enchantments.INFINITY_ARROWS)) {
                    components.add(Component.translatable("tooltip.artificers_armory.sc_infinity").withStyle(ChatFormatting.BLUE));
                }
                if (UtilMethods.hasEnchantment(stack, ModEnchantments.HEX.get())) {
                    components.add(Component.translatable("enchantment.artificers_armory.hex.desc").withStyle(ChatFormatting.BLUE));
                }
                if (UtilMethods.hasEnchantment(stack, ModEnchantments.GRIM_HARVEST.get())) {
                    components.add(Component.translatable("enchantment.artificers_armory.grim_harvest.desc").withStyle(ChatFormatting.BLUE));
                }
                if (UtilMethods.hasEnchantment(stack, ModEnchantments.FOCUS.get())) {
                    components.add(Component.translatable("enchantment.artificers_armory.focus.desc").withStyle(ChatFormatting.BLUE));
                }
                if (UtilMethods.hasEnchantment(stack, ModEnchantments.LIFEDRAIN.get())) {
                    components.add(Component.translatable("enchantment.artificers_armory.lifedrain.desc").withStyle(ChatFormatting.BLUE));
                }
                if (UtilMethods.hasEnchantment(stack, ModEnchantments.PHYLACTERY.get())) {
                    components.add(Component.translatable("enchantment.artificers_armory.phylactery.desc").withStyle(ChatFormatting.BLUE));
                }
            }
        }


        super.appendHoverText(stack, level, components, flag);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 720000;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (level.isClientSide) return;
        if (!(entity instanceof Player player)) return;
        if (player.isSpectator() || !player.isAlive()) return;

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        if (stack != mainHand && stack != offHand) return;

        int timer = PASSIVE_TIMER.getOrDefault(stack, this.cooldown * 3);
        timer--;
        PASSIVE_TIMER.put(stack, timer);

        if (SpiritCandleEvents.countOwnedMinions(player, 20) < SpiritCandleEvents.getMaxAllowedMinions(player) && timer <= 0) {
            if (SpiritCandleEvents.tryPassiveSummon(player, stack, this)) {
                EquipmentSlot breakSlot = (stack == mainHand) ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;

                // Don't damage the item for passive spawns if it has infinity
                if (!UtilMethods.hasEnchantment(stack, Enchantments.INFINITY_ARROWS)) {
                    stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(breakSlot));
                }
            }
            PASSIVE_TIMER.put(stack, this.cooldown * 3);
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos().above();

        if (player == null) return InteractionResult.PASS;
        if (player.isCrouching()) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;

        ItemStack stack = context.getItemInHand();

        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResult.FAIL;
        }

        boolean canSpawn = SpiritCandleEvents.canSpawnAt(level, pos);

        if (!canSpawn) {
            return InteractionResult.FAIL;
        }

        if (SpiritCandleEvents.tryActiveSummon(player, pos, this, stack)) {
            player.getCooldowns().addCooldown(this, SpiritCandleEvents.getAdjustedActiveCooldown(stack));
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));
            PASSIVE_TIMER.put(stack, SpiritCandleEvents.getAdjustedActiveCooldown(stack) * 3);
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);

        if (player.isCrouching()) {
            if (!level.isClientSide && SpiritCandleEvents.tryMarkTarget(player, stack)) {
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                player.getCooldowns().addCooldown(this, 10);
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.EVOKER_PREPARE_ATTACK, SoundSource.PLAYERS, 0.8f, 1.0f);
            }
            return InteractionResultHolder.consume(stack);
        } else {
            player.startUsingItem(hand);
        }

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remaining) {
        if (level.isClientSide) return;
        if (!(entity instanceof Player player)) return;

        int ticksUsed = getUseDuration(stack) - remaining;

        SpiritCandleEvents.doChanneling(player, stack, ticksUsed);
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
        return holder.is(ModTags.Enchantments.SPIRIT_CANDLE_ALLOWED);
    }

    public static boolean hasDescriptionRelevantEnchants(ItemStack stack) {
        return UtilMethods.hasEnchantment(stack, Enchantments.INFINITY_ARROWS) ||
                UtilMethods.hasEnchantment(stack, Enchantments.BLOCK_FORTUNE) ||
                UtilMethods.hasEnchantment(stack, ModEnchantments.LIFEDRAIN.get()) ||
                UtilMethods.hasEnchantment(stack, ModEnchantments.FOCUS.get()) ||
                UtilMethods.hasEnchantment(stack, ModEnchantments.HEX.get()) ||
                UtilMethods.hasEnchantment(stack, ModEnchantments.PHYLACTERY.get()) ||
                UtilMethods.hasEnchantment(stack, ModEnchantments.GRIM_HARVEST.get());
    }

    /**
     * ISS Compatibility
     */
    private static final ResourceLocation BLOOD_SPELL_POWER = new ResourceLocation("irons_spellbooks", "blood_spell_power");

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {

        Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create(super.getAttributeModifiers(slot, stack));

        if ((slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) && ModList.get().isLoaded("irons_spellbooks")
                && Config.ISS_COMPAT.get()) {
            addISSAttribute(modifiers, BLOOD_SPELL_POWER, Config.ISS_BOOST_AMOUNT.get(), "iss_blood_spell_power_bonus");
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

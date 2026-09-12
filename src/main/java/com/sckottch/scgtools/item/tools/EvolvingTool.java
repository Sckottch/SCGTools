package com.sckottch.scgtools.item.tools;

import com.sckottch.scgtools.components.ModDataComponents;
import com.sckottch.scgtools.components.ToolLevelData;
import com.sckottch.scgtools.item.tools.upgrades.ToolUpgrade;
import com.sckottch.scgtools.utility.enums.ToolTier;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.Repairable;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Consumer;

public interface EvolvingTool {
    TagKey<Block> getMineableTag();
    float getBaseAttackDamage();
    float getBaseAttackSpeed();
    int xpForNextLevel(int level);
    
    default void applyTier(ItemStack stack, ToolTier tier, HolderLookup.Provider lookup) {
        ToolMaterial mat = tier.getMaterial();
        HolderGetter<Block> blocks = lookup.lookupOrThrow(Registries.BLOCK);
        HolderGetter<Item> items = lookup.lookupOrThrow(Registries.ITEM);
        
        stack.set(DataComponents.TOOL, buildTool(mat, blocks));
        
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, getBaseAttackDamage() + mat.attackDamageBonus(),
                    AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED,
                new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, getBaseAttackSpeed(), AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND)
            .build()
        );
        
        stack.set(DataComponents.ENCHANTABLE, new Enchantable(mat.enchantmentValue()));
        stack.set(DataComponents.REPAIRABLE, new Repairable(items.getOrThrow(mat.repairItems())));
        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(), List.of(), List.of(tier.getSerializedName()), List.of()));
        stack.set(DataComponents.RARITY, tier.getRarity());

        stack.set(ModDataComponents.TOOL_TIER.get(), tier);
        recalculateMaxDamage(stack);
    }
    
    default Tool buildTool(ToolMaterial mat, HolderGetter<Block> blocks) {
        return new Tool(
            List.of(
                Tool.Rule.deniesDrops(blocks.getOrThrow(mat.incorrectBlocksForDrops())),
                Tool.Rule.minesAndDrops(blocks.getOrThrow(getMineableTag()), mat.speed())
            ), 1.0F, 1, true
        );
    }
    
    default void recalculateMaxDamage(ItemStack stack) {
        ToolTier tier = stack.get(ModDataComponents.TOOL_TIER.get());
        ToolLevelData level = stack.get(ModDataComponents.TOOL_LEVEL.get());
        if (tier == null || level == null) return;
        
        if (tier.isUnbreakable()) {
             stack.remove(DataComponents.MAX_DAMAGE);
             stack.remove(DataComponents.DAMAGE);
             return;
        }
        
        if (!stack.has(DataComponents.DAMAGE)) stack.set(DataComponents.DAMAGE, 0);
        
        int maxDamage = tier.getMaterial().durability() + (50 * tier.getDurabilityMultiplier() * level.level());
        stack.set(DataComponents.MAX_DAMAGE, maxDamage);
    }
    
    default void addXp(ItemStack stack, int amount) {
        ToolLevelData data = stack.get(ModDataComponents.TOOL_LEVEL.get());
        if (data == null) return;
        if (data.level() >= ToolLevelData.MAX_LEVEL) return;
        
        int newLevel = data.level();
        int newXp = data.xp() + amount;
        
        while (newLevel < ToolLevelData.MAX_LEVEL && newXp >= xpForNextLevel(newLevel)) {
            newXp -= xpForNextLevel(newLevel);
            newLevel++;
        }
        
        if (newLevel >= ToolLevelData.MAX_LEVEL) newXp = 0;
        
        stack.set(ModDataComponents.TOOL_LEVEL.get(), new ToolLevelData(newLevel, newXp));
        
        if (newLevel != data.level()) {
            recalculateMaxDamage(stack);
        }
    }
    
    default void evolvingTooltip(ItemStack stack, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        ToolTier tier = stack.get(ModDataComponents.TOOL_TIER.get());
        ToolLevelData level = stack.get(ModDataComponents.TOOL_LEVEL.get());
        List<ToolUpgrade> upgrades = stack.get(ModDataComponents.TOOL_UPGRADES.get());
        
        if (tier != null) {
            builder.accept(Component.literal("Tier: " + tier.getDisplayName()));
        }
        
        if (level != null) {
            if (level.level() == ToolLevelData.MAX_LEVEL) {
                builder.accept(Component.literal("Lv: " + level.level() + " (MAX)"));
            } else {
                builder.accept(Component.literal("Lv: " + level.level() + " (XP: " + level.xp() + "/" + xpForNextLevel(level.level()) + ")"));
            }
        }
        
        if (level != null && upgrades != null) {
            builder.accept(Component.literal("Slots: " + upgrades.size() + "/" + level.slotCount()));
            if(tooltipFlag.hasShiftDown()) {
                for (ToolUpgrade upgrade : upgrades) {
                    builder.accept(Component.literal("- " + upgrade.getDisplayName()));
                }
            }
        }
    }
    
    default int getDamageItem(ItemStack stack, int amount) {
        int max = stack.getMaxDamage();
        int current = stack.getDamageValue();
        
        if (current + amount >= max) {
            stack.setDamageValue(max);
            return 0;
        }
        
        return amount;
    }
    
    default boolean enchantmentBlocked(Holder<Enchantment> enchantment) {
        return  enchantment.is(Enchantments.MENDING);
    }
    
    default boolean tierAllowsDamage(ItemStack stack, DamageSource source) {
        ToolTier tier = stack.get(ModDataComponents.TOOL_TIER.get());
        if (tier == null || !tier.isDamageResistant()) return true;
        
        return !source.is(DamageTypeTags.IS_FIRE) && !source.is(DamageTypeTags.IS_EXPLOSION);
    }
    
    default boolean isDepleted(ItemStack stack) {
        return stack.isDamageableItem() && stack.getDamageValue() >= stack.getMaxDamage();
    }
}

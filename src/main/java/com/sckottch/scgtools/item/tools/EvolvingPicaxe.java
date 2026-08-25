package com.sckottch.scgtools.item.tools;

import com.sckottch.scgtools.components.ModDataComponents;
import com.sckottch.scgtools.components.ToolLevelData;
import com.sckottch.scgtools.item.tools.upgrades.ToolUpgrade;
import com.sckottch.scgtools.utility.enums.ToolTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Consumer;

public class EvolvingPicaxe extends Item {
    private static final int XP_PER_BLOCK = 1;
    
    public EvolvingPicaxe(Properties props) {
        super(props);
    }
    
    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> builder, TooltipFlag tooltipFlag) {
        ToolTier tier = itemStack.get(ModDataComponents.TOOL_TIER.get());
        ToolLevelData level = itemStack.get(ModDataComponents.TOOL_LEVEL.get());
        List<ToolUpgrade> upgrades = itemStack.get(ModDataComponents.TOOL_UPGRADES.get());
        
        if (tier != null) {
            builder.accept(Component.literal("Tier: " + tier.getDisplayName()));
        }
        
        if (level != null) {
            if (level.level() == ToolLevelData.MAX_LEVEL) {
                builder.accept(Component.literal("Lv: " + level.level() + " (MAX)"));
            } else {
                builder.accept(Component.literal("Lv: " + level.level() + " (XP: " + level.xp() + ")"));
            }
        }
        
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
        
        if (level != null && upgrades != null) {
            builder.accept(Component.literal("Slots: " + upgrades.size() + "/" + level.slotCount()));
            if(tooltipFlag.hasShiftDown()) {
                for (ToolUpgrade upgrade : upgrades) {
                    builder.accept(Component.literal("- " + upgrade.getDisplayName()));
                }
            }
        }
        
    }
    
    @Override
    public boolean mineBlock(ItemStack itemStack, Level level, BlockState state, BlockPos pos, LivingEntity owner) {
        if (isCorrectToolForDrops(itemStack, state)) {
            addXp(itemStack, XP_PER_BLOCK);
        }
        
        return super.mineBlock(itemStack, level, state, pos, owner);
    }
    
    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        if (enchantment.is(Enchantments.MENDING)){
            return false;
        }
        return super.supportsEnchantment(stack, enchantment);
    }
    
    private void addXp(ItemStack stack, int amount) {
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
    
    public static void applyTier(ItemStack stack, ToolTier tier, HolderGetter.Provider lookup) {
        ToolMaterial mat = tier.getMaterial();
        HolderGetter<Block> blocks = lookup.lookupOrThrow(Registries.BLOCK);
        HolderGetter<Item> items = lookup.lookupOrThrow(Registries.ITEM);
        
        stack.set(DataComponents.TOOL, new Tool(
            List.of(
                Tool.Rule.deniesDrops(blocks.getOrThrow(mat.incorrectBlocksForDrops())),
                Tool.Rule.minesAndDrops(blocks.getOrThrow(BlockTags.MINEABLE_WITH_PICKAXE), mat.speed())
            ), 1.0F, 1, true
        ));
        
        stack.set(DataComponents.WEAPON, new Weapon(2, 0f));
        stack.set(DataComponents.ENCHANTABLE, new Enchantable(mat.enchantmentValue()));
        stack.set(DataComponents.REPAIRABLE, new Repairable(items.getOrThrow(mat.repairItems())));
        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(), List.of(), List.of(tier.getSerializedName()), List.of()));
        
        if (!stack.has(DataComponents.ENCHANTMENTS)) {
            stack.set(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        }
        
        stack.set(ModDataComponents.TOOL_TIER.get(), tier);
        recalculateMaxDamage(stack);
    }
    
    private int xpForNextLevel(int level) {
        return 2 * (level + 1);
    }
    
    private static void recalculateMaxDamage(ItemStack stack) {
        ToolTier tier = stack.get(ModDataComponents.TOOL_TIER.get());
        ToolLevelData level = stack.get(ModDataComponents.TOOL_LEVEL.get());
        if (tier == null || level == null) return;
        
        int maxDamage = tier.getMaterial().durability() + (50 * tier.getDurabilityMultiplier() * level.level());
        
        stack.set(DataComponents.MAX_DAMAGE, maxDamage);
    }
}

package com.sckottch.scgtools.item.tools;

import com.sckottch.scgtools.components.ModDataComponents;
import com.sckottch.scgtools.components.ToolLevelData;
import com.sckottch.scgtools.utility.enums.ToolTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredRegister;

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
    }
    
    @Override
    public boolean mineBlock(ItemStack itemStack, Level level, BlockState state, BlockPos pos, LivingEntity owner) {
        if (isCorrectToolForDrops(itemStack, state)) {
            addXp(itemStack, XP_PER_BLOCK);
        }
        
        return super.mineBlock(itemStack, level, state, pos, owner);
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
    
    private int xpForNextLevel(int level) {
        return 2 * (level + 1);
    }
    
    private void recalculateMaxDamage(ItemStack stack) {
        ToolTier tier = stack.get(ModDataComponents.TOOL_TIER.get());
        ToolLevelData level = stack.get(ModDataComponents.TOOL_LEVEL.get());
        if (tier == null || level == null) return;
        
        int maxDamage = tier.getBaseDurability() + (50 * tier.getDurabilityMultiplier() * level.level());
        
        stack.set(DataComponents.MAX_DAMAGE, maxDamage);
    }
}
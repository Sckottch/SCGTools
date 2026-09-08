package com.sckottch.scgtools.item.tools;

import com.sckottch.scgtools.components.ModDataComponents;
import com.sckottch.scgtools.item.tools.upgrades.BlockBreakingUpgrade;
import com.sckottch.scgtools.item.tools.upgrades.ToolUpgrade;
import com.sckottch.scgtools.utility.enums.MiningMode;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public interface MiningEvolvingTool extends EvolvingTool {
    default List<MiningMode> availableModes(ItemStack stack) {
        List<MiningMode> modes = new ArrayList<>();
        modes.add(MiningMode.NORMAL);
        
        List<ToolUpgrade> upgrades = stack.get(ModDataComponents.TOOL_UPGRADES.get());
        if (upgrades == null) return modes;
        
        for (ToolUpgrade upgrade : upgrades) {
            if (upgrade instanceof BlockBreakingUpgrade breakingUpgrade && !modes.contains(breakingUpgrade.getMiningMode())) {
                modes.add(breakingUpgrade.getMiningMode());
            }
        }
        
        modes.sort(Comparator.comparingInt(MiningMode::getId));
        return modes;
    }
    
    default void cycleMiningMode(ItemStack stack) {
        List<MiningMode> modes = availableModes(stack);
        if (modes.size() <= 1) return;
        
        MiningMode current = stack.getOrDefault(ModDataComponents.MINING_MODE.get(), MiningMode.NORMAL);
        int idx = modes.indexOf(current);
        stack.set(ModDataComponents.MINING_MODE.get(), modes.get((idx + 1) % modes.size()));
    }
    
    default void miningModeTooltip(ItemStack stack, Consumer<Component> builder) {
        List<MiningMode> modes = availableModes(stack);
        if (modes.size() > 1) {
            MiningMode mode = stack.getOrDefault(ModDataComponents.MINING_MODE.get(), MiningMode.NORMAL);
            builder.accept(Component.literal("Mode: " + mode.getDisplayName()));
        }
    }
    
    default InteractionResult changeMiningMode(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (availableModes(stack).size() <= 1) return InteractionResult.PASS;
        
        if (level instanceof ServerLevel) {
            cycleMiningMode(stack);
        }
        
        return InteractionResult.CONSUME;
    }
}

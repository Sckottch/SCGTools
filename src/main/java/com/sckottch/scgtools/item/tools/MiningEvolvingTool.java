package com.sckottch.scgtools.item.tools;

import com.sckottch.scgtools.components.ModDataComponents;
import com.sckottch.scgtools.item.tools.upgrades.BlockBreakingUpgrade;
import com.sckottch.scgtools.item.tools.upgrades.HammerUpgrade;
import com.sckottch.scgtools.item.tools.upgrades.ToolUpgrade;
import com.sckottch.scgtools.utility.enums.MiningMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

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
        if (modes.size() <= 1) return;
        
        MiningMode mode = stack.getOrDefault(ModDataComponents.MINING_MODE.get(), MiningMode.NORMAL);
        String line = "Mode: " + mode.getDisplayName();
        
        List<ToolUpgrade> upgrades = stack.get(ModDataComponents.TOOL_UPGRADES.get());
        if (upgrades != null) {
            for (ToolUpgrade upgrade : upgrades) {
                if (upgrade instanceof BlockBreakingUpgrade breaker && breaker.getMiningMode() == mode) {
                    String descriptor = breaker.getModeDescriptor(stack);
                    if (!descriptor.isEmpty()) line += " (" + descriptor + ")";
                    break;
                }
            }
        }
        
        builder.accept(Component.literal(line));
    }
    
    default InteractionResult changeMiningMode(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        if (availableModes(stack).size() <= 1) return InteractionResult.PASS;
        
        if (level instanceof ServerLevel) {
            cycleMiningMode(stack);

            MiningMode mode = stack.getOrDefault(ModDataComponents.MINING_MODE.get(), MiningMode.NORMAL);
            player.sendOverlayMessage(Component.literal("Mining Mode: " + mode.getDisplayName()));
        }

        return InteractionResult.CONSUME;
    }

    default void useOnArea(UseOnContext context, Function<UseOnContext, InteractionResult> action, boolean horizontalOnly) {
        if (!(context.getLevel() instanceof ServerLevel)) return;

        ItemStack stack = context.getItemInHand();
        if (stack.getOrDefault(ModDataComponents.MINING_MODE.get(), MiningMode.NORMAL) != MiningMode.HAMMER) return;

        List<ToolUpgrade> upgrades = stack.get(ModDataComponents.TOOL_UPGRADES.get());
        if (upgrades == null) return;

        for (ToolUpgrade upgrade : upgrades) {
            if (!(upgrade instanceof HammerUpgrade hammer)) continue;

            Direction.Axis axis = horizontalOnly ? Direction.Axis.Y : context.getClickedFace().getAxis();
            for (BlockPos pos : hammer.getAreaPositions(stack, context.getClickedPos(), axis)) {
                if (isDepleted(stack)) return;

                // contexto novo por posição porque a checagem de ar acima da pá/enxada usa o clickedPos do contexto
                BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), context.getClickedFace(), pos, context.isInside());
                action.apply(new UseOnContext(context.getLevel(), context.getPlayer(), context.getHand(), stack, hit));
            }
            return;
        }
    }
}

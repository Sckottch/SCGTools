package com.sckottch.scgtools.utility.events;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.components.ModDataComponents;
import com.sckottch.scgtools.item.tools.EvolvingTool;
import com.sckottch.scgtools.item.tools.upgrades.BlockBreakingUpgrade;
import com.sckottch.scgtools.item.tools.upgrades.ToolUpgrade;
import com.sckottch.scgtools.utility.enums.MiningMode;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.block.BreakBlockEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EventBusSubscriber(modid = SCGTools.MOD_ID)
public class ModBlockBreakEvents {
    private static final Set<BlockPos> BREAKING = new HashSet<>();
    
    @SubscribeEvent
    public static void onBlockBreak(BreakBlockEvent event) {
        if (event.isCanceled()) return;
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        
        BlockPos origin = event.getPos();
        if (BREAKING.contains(origin)) return;
        
        ItemStack tool = player.getMainHandItem();
        if (!(tool.getItem() instanceof EvolvingTool)) return;
        
        List<ToolUpgrade> upgrades = tool.get(ModDataComponents.TOOL_UPGRADES.get());
        if (upgrades == null || upgrades.isEmpty()) return;
        
        MiningMode mode = tool.getOrDefault(ModDataComponents.MINING_MODE.get(), MiningMode.NORMAL);
        if (mode == MiningMode.NORMAL) return;
        
        for (ToolUpgrade upgrade : upgrades) {
            if (!(upgrade instanceof BlockBreakingUpgrade breaker)) continue;
            if (breaker.getMiningMode() != mode) continue;
            
            for (BlockPos pos : breaker.getBlocksToBreak(tool, level, origin, player)) {
                if (!tool.isCorrectToolForDrops(level.getBlockState(pos))) continue;
                if (!BREAKING.add(pos)) continue;
                
                try {
                    player.gameMode.destroyBlock(pos);
                } finally {
                    BREAKING.remove(pos);
                }
            }
        }
    }
}

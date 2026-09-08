package com.sckottch.scgtools.utility.events;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.components.ModDataComponents;
import com.sckottch.scgtools.item.tools.MiningEvolvingTool;
import com.sckottch.scgtools.item.tools.upgrades.BlockBreakingUpgrade;
import com.sckottch.scgtools.item.tools.upgrades.ToolUpgrade;
import com.sckottch.scgtools.utility.enums.MiningMode;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.List;

@EventBusSubscriber(modid = SCGTools.MOD_ID)
public class ModBreakSpeedHandler {
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        ItemStack tool = event.getEntity().getMainHandItem();
        if (!(tool.getItem() instanceof MiningEvolvingTool)) return;
        
        MiningMode mode = tool.getOrDefault(ModDataComponents.MINING_MODE.get(), MiningMode.NORMAL);
        if (mode == MiningMode.NORMAL) return;
        
        List<ToolUpgrade> upgrades = tool.get(ModDataComponents.TOOL_UPGRADES.get());
        if (upgrades == null) return;
        
        for (ToolUpgrade upgrade : upgrades) {
            if (!(upgrade instanceof BlockBreakingUpgrade breakingUpgrade)) continue;
            if (breakingUpgrade.getMiningMode() != mode) continue;
            
            event.setNewSpeed(event.getNewSpeed() * breakingUpgrade.getSpeedMultiplier(tool));
            return;
        }
    }
}

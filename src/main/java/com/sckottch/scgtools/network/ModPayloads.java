package com.sckottch.scgtools.network;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.components.ModDataComponents;
import com.sckottch.scgtools.item.tools.MiningEvolvingTool;
import com.sckottch.scgtools.item.tools.upgrades.BlockBreakingUpgrade;
import com.sckottch.scgtools.item.tools.upgrades.ToolUpgrade;
import com.sckottch.scgtools.utility.enums.MiningMode;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.List;

@EventBusSubscriber(modid = SCGTools.MOD_ID)
public class ModPayloads {
    
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        
        registrar.playToServer(
            CycleUpgradeModePayload.TYPE,
            CycleUpgradeModePayload.STREAM_CODEC,
            ModPayloads::onCycleUpgradeMode
        );
    }
    
    private static void onCycleUpgradeMode(CycleUpgradeModePayload payload, IPayloadContext context) {
        if (!(context.player() instanceof ServerPlayer player)) return;
        
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof MiningEvolvingTool)) return;
        
        MiningMode mode = stack.getOrDefault(ModDataComponents.MINING_MODE.get(), MiningMode.NORMAL);
        if (mode == MiningMode.NORMAL) return;
        
        List<ToolUpgrade> upgrades = stack.get(ModDataComponents.TOOL_UPGRADES.get());
        if (upgrades == null) return;
        
        for (ToolUpgrade upgrade : upgrades) {
            if (upgrade instanceof BlockBreakingUpgrade breaker && breaker.getMiningMode() == mode) {
                breaker.cycleUpgradeMode(stack);

                String descriptor = breaker.getModeDescriptor(stack);
                if (!descriptor.isEmpty()) {
                    player.sendOverlayMessage(Component.literal(upgrade.getDisplayName() + ": " + descriptor));
                }

                return;
            }
        }
    }
}
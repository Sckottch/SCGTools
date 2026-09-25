package com.sckottch.scgtools.network;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.item.tools.EvolvingTool;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

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
        if (stack.getItem() instanceof EvolvingTool tool) {
            tool.handleCycleKey(player, stack);
        }
    }
}
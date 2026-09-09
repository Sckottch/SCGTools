package com.sckottch.scgtools.utility.keybinds;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.network.CycleUpgradeModePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.vehicle.minecart.Minecart;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

@EventBusSubscriber(modid = SCGTools.MOD_ID, value = Dist.CLIENT)
public class ModClientInputHandler {
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().player == null) return;
        
        while (ModKeyBinds.CYCLE_UPGRADE_MODE.consumeClick()) {
            ClientPacketDistributor.sendToServer(CycleUpgradeModePayload.INSTANCE);
        }
    }
}

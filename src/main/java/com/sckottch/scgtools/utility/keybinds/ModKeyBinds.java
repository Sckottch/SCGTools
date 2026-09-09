package com.sckottch.scgtools.utility.keybinds;

import com.mojang.blaze3d.platform.InputConstants;
import com.sckottch.scgtools.SCGTools;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = SCGTools.MOD_ID, value = Dist.CLIENT)
public class ModKeyBinds {
    public static final KeyMapping CYCLE_UPGRADE_MODE = new KeyMapping(
        "key.scgtools.cycle_upgrade_mode",
        InputConstants.KEY_H,
        KeyMapping.Category.GAMEPLAY
    );
    
    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(CYCLE_UPGRADE_MODE);
    }
}

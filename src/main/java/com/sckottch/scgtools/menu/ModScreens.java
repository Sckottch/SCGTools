package com.sckottch.scgtools.menu;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.forge.ForgeScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = SCGTools.MODID, value = Dist.CLIENT)
public class ModScreens {
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.FORGE.get(), ForgeScreen::new);
    }
}

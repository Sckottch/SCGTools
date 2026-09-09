package com.sckottch.scgtools.utility.events;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.recipe.ModRecipes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

@EventBusSubscriber(modid = SCGTools.MOD_ID)
public class ModRecipeSyncHandler {
    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        event.sendRecipes(
            ModRecipes.FORGE_SIMPLE_TYPE.get(),
            ModRecipes.FORGE_TOOL_TYPE.get()
        );
    }
}

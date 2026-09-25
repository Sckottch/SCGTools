package com.sckottch.scgtools;

import com.mojang.logging.LogUtils;
import com.sckottch.scgtools.block.ModBlocks;
import com.sckottch.scgtools.block.entities.ModBlockEntities;
import com.sckottch.scgtools.components.ModDataComponents;
import com.sckottch.scgtools.forge.operation.ModForgeOperations;
import com.sckottch.scgtools.item.ModItems;
import com.sckottch.scgtools.item.tools.upgrades.ModUpgrades;
import com.sckottch.scgtools.menu.ModMenus;
import com.sckottch.scgtools.recipe.ModRecipes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(SCGTools.MOD_ID)
public class SCGTools {
    public static final String MOD_ID = "scgtools";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SCGTools(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModCreativeTab.register(modEventBus);
        ModMenus.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModUpgrades.register(modEventBus);
        ModForgeOperations.register(modEventBus);
        
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }
}

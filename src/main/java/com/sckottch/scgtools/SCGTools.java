package com.sckottch.scgtools;

import com.sckottch.scgtools.components.ModDataComponents;
import com.sckottch.scgtools.item.ModItems;
import com.sckottch.scgtools.item.tools.upgrades.ModUpgrades;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(SCGTools.MODID)
public class SCGTools {
    public static final String MODID = "scgtools";
    public static final Logger LOGGER = LogUtils.getLogger();

    public SCGTools(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        
        ModItems.register(modEventBus);
        ModCreativeTab.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModUpgrades.register(modEventBus);
        
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }
}

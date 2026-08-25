package com.sckottch.scgtools.item.tools.upgrades;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.utility.enums.ToolTier;
import com.sckottch.scgtools.utility.registries.ModRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModUpgrades {
    public static DeferredRegister<ToolUpgrade> UPGRADES =
        DeferredRegister.create(ModRegistries.UPGRADE_REGISTRY_KEY, SCGTools.MODID);
    
    public static final Supplier<ToolUpgrade> MENDING =
        UPGRADES.register("mending", () -> new MendingUpgrade(ToolTier.IRON, "Mending"));
    
    public static void register(IEventBus eventBus) {
        UPGRADES.register(eventBus);
    }
}

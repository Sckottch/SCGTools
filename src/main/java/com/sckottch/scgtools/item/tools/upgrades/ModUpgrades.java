package com.sckottch.scgtools.item.tools.upgrades;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.utility.enums.ToolTier;
import com.sckottch.scgtools.utility.registries.ModRegistries;
import com.sckottch.scgtools.utility.tags.ModTags;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModUpgrades {
    public static DeferredRegister<ToolUpgrade> UPGRADES =
        DeferredRegister.create(ModRegistries.UPGRADE_REGISTRY_KEY, SCGTools.MOD_ID);
    
    public static final Supplier<ToolUpgrade> MENDING =
        UPGRADES.register("mending", () -> new MendingUpgrade(ToolTier.FLINT, "Mending", ModTags.Items.EVOLVING_ITEM));
    
    public static final Supplier<ToolUpgrade> HAMMER =
        UPGRADES.register("hammer", () -> new HammerUpgrade(ToolTier.IRON, "Hammer", ModTags.Items.MINING_TOOL));
    
    public static void register(IEventBus eventBus) {
        UPGRADES.register(eventBus);
    }
}

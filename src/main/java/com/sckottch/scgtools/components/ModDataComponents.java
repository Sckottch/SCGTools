package com.sckottch.scgtools.components;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.utility.enums.ToolTier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
        DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, SCGTools.MODID);
    
    public static final Supplier<DataComponentType<ToolLevelData>> TOOL_LEVEL =
        DATA_COMPONENTS.registerComponentType("tool_level",
            builder -> builder
                .persistent(ToolLevelData.CODEC)
                .networkSynchronized(ToolLevelData.STREAM_CODEC)
        );
    
    public static final Supplier<DataComponentType<ToolTier>> TOOL_TIER =
        DATA_COMPONENTS.registerComponentType("tool_tier",
            buider -> buider
                .persistent(ToolTier.CODEC)
                .networkSynchronized(ToolTier.STREAM_CODEC)
        );
    
    public static void register(IEventBus eventBus) {
        DATA_COMPONENTS.register(eventBus);
    }
}

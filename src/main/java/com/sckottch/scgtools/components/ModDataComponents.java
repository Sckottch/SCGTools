package com.sckottch.scgtools.components;

import com.mojang.serialization.Codec;
import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.item.tools.upgrades.ToolUpgrade;
import com.sckottch.scgtools.utility.enums.ToolTier;
import com.sckottch.scgtools.utility.registries.ModRegistries;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
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
            builder -> builder
                .persistent(ToolTier.CODEC)
                .networkSynchronized(ToolTier.STREAM_CODEC)
        );
    
    public static final Codec<List<ToolUpgrade>> CODEC =
        ModRegistries.UPGRADE_REGISTRY.byNameCodec().listOf();
    
    public static final StreamCodec<RegistryFriendlyByteBuf, List<ToolUpgrade>> STREAM_CODEC =
        ByteBufCodecs.registry(ModRegistries.UPGRADE_REGISTRY_KEY).apply(ByteBufCodecs.list());
    
    public static final Supplier<DataComponentType<List<ToolUpgrade>>> TOOL_UPGRADES =
        DATA_COMPONENTS.registerComponentType("tool_upgrade",
            builder -> builder
                .persistent(CODEC)
                .networkSynchronized(STREAM_CODEC)
            );
    
    public static void register(IEventBus eventBus) {
        DATA_COMPONENTS.register(eventBus);
    }
}

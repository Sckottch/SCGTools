package com.sckottch.scgtools.utility.registries;

import com.mojang.serialization.MapCodec;
import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.forge.operation.ForgeOperation;
import com.sckottch.scgtools.item.tools.upgrades.ToolUpgrade;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@EventBusSubscriber(modid = SCGTools.MODID)
public class ModRegistries {
    public static final ResourceKey<Registry<ToolUpgrade>> UPGRADE_REGISTRY_KEY =
        ResourceKey.createRegistryKey(
            Identifier.fromNamespaceAndPath(SCGTools.MODID, "tool_upgrade")
        );
    
    public static final Registry<ToolUpgrade> UPGRADE_REGISTRY =
        new RegistryBuilder<>(UPGRADE_REGISTRY_KEY)
            .sync(true)
            .create();
    
    public static final ResourceKey<Registry<MapCodec<? extends ForgeOperation>>> OPERATION_REGISTRY_KEY =
        ResourceKey.createRegistryKey(
            Identifier.fromNamespaceAndPath(SCGTools.MODID, "forge_operation")
        );
    
    public static final Registry<MapCodec<? extends ForgeOperation>> OPERATION_REGISTRY =
        new RegistryBuilder<>(OPERATION_REGISTRY_KEY).sync(true).create();
    
    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(UPGRADE_REGISTRY);
        event.register(OPERATION_REGISTRY);
    }
}

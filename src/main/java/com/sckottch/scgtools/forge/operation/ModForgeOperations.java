package com.sckottch.scgtools.forge.operation;

import com.mojang.serialization.MapCodec;
import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.utility.registries.ModRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModForgeOperations {
    public static final DeferredRegister<MapCodec<? extends ForgeOperation>> OPERATIONS =
        DeferredRegister.create(ModRegistries.OPERATION_REGISTRY_KEY, SCGTools.MOD_ID);
    
    public static final Supplier<MapCodec<? extends ForgeOperation>> TIER_UP = 
        OPERATIONS.register("tier_up", () -> TierUpOperation.CODEC);
    
    public static final Supplier<MapCodec<? extends ForgeOperation>> APPLY_UPGRADE =
        OPERATIONS.register("apply_upgrade", () -> ApplyUpgradeOperation.CODEC);
    
    public static void register(IEventBus eventBus) {
        OPERATIONS.register(eventBus);
    }
}

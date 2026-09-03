package com.sckottch.scgtools.block.entities;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.block.ModBlocks;
import com.sckottch.scgtools.forge.ForgeBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(
        Registries.BLOCK_ENTITY_TYPE, SCGTools.MOD_ID
    );
    
    public static final Supplier<BlockEntityType<ForgeBlockEntity>> FORGE =
        BLOCK_ENTITIES.register("forge", () ->
            new BlockEntityType<>(ForgeBlockEntity::new, ModBlocks.FORGE.get())
        );
    
    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}

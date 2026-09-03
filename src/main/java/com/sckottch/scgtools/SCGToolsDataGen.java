package com.sckottch.scgtools;

import com.sckottch.scgtools.datagen.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = SCGTools.MOD_ID)
public class SCGToolsDataGen {
    @SubscribeEvent
    public static void gatherClientData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();
        PackOutput output = generator.getPackOutput();
        
        generator.addProvider(true, new ModItemTagsProvider(output, lookup));
        generator.addProvider(true, new ModBlockTagsProvider(output, lookup));
        generator.addProvider(true, new ModModelProvider(output));
        generator.addProvider(true, new ModRecipeProvider.Runner(output, lookup));
        generator.addProvider(true, new LootTableProvider(output, Collections.emptySet(),
            List.of(
                new LootTableProvider.SubProviderEntry(ModBlockLootProvider::new, LootContextParamSets.BLOCK)
            ), lookup)
        );
    }
}

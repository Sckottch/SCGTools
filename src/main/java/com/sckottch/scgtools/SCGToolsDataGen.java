package com.sckottch.scgtools;

import com.sckottch.scgtools.datagen.ModItemTagsProvider;
import com.sckottch.scgtools.datagen.ModModelProvider;
import com.sckottch.scgtools.datagen.ModRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = SCGTools.MODID)
public class SCGToolsDataGen {
    @SubscribeEvent
    public static void gatherClientData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();
        PackOutput output = generator.getPackOutput();
        
        generator.addProvider(true, new ModItemTagsProvider(output, lookup));
        generator.addProvider(true, new ModModelProvider(output));
        generator.addProvider(true, new ModRecipeProvider.Runner(output, lookup));
    }
}

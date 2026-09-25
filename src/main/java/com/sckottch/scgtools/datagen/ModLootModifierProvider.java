package com.sckottch.scgtools.datagen;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.loot.ModLootTables;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

public class ModLootModifierProvider extends GlobalLootModifierProvider {
    public ModLootModifierProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, SCGTools.MOD_ID);
    }
    
    @Override
    protected void start() {
        add("dragon_tooth", new AddTableLootModifier(
            new LootItemCondition[] {
                LootTableIdCondition.builder(Identifier.withDefaultNamespace("entities/ender_dragon")).build()
            },
            IGlobalLootModifier.DEFAULT_PRIORITY,
            ModLootTables.DRAGON_TOOTH
        ));
        
        add("draconic_template", new AddTableLootModifier(
            new LootItemCondition[] {
                LootTableIdCondition.builder(Identifier.withDefaultNamespace("entities/enderman")).build()
            },
            IGlobalLootModifier.DEFAULT_PRIORITY,
            ModLootTables.DRACONIC_TEMPLATE
        ));
    }
}

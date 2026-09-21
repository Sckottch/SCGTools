package com.sckottch.scgtools.datagen;

import com.sckottch.scgtools.item.ModItems;
import com.sckottch.scgtools.loot.ModLootTables;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

public class ModEntityLootProvider implements LootTableSubProvider {
    private final HolderLookup.Provider registries;
    
    public ModEntityLootProvider(HolderLookup.Provider registries) {
        this.registries = registries;
    }
    
    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(ModLootTables.DRAGON_TOOTH, LootTable.lootTable().withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(ModItems.DRAGON_TOOTH.get()))
                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)))
        ));
        
        output.accept(ModLootTables.DRACONIC_TEMPLATE, LootTable.lootTable().withPool(
            LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .when(LootItemKilledByPlayerCondition.killedByPlayer())
                .when(LootItemRandomChanceWithEnchantedBonusCondition.randomChanceAndLootingBoost(registries, 0.0005F, 0.0001F))
                .add(LootItem.lootTableItem(ModItems.DRACONIC_TEMPLATE.get()))
        ));
    }
}

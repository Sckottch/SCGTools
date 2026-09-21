package com.sckottch.scgtools.loot;

import com.sckottch.scgtools.SCGTools;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public class ModLootTables {
    public static final ResourceKey<LootTable> DRAGON_TOOTH = ResourceKey.create(
        Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(SCGTools.MOD_ID, "entities/dragon_tooth")
    );
    
    public static final ResourceKey<LootTable> DRACONIC_TEMPLATE = ResourceKey.create(
        Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(SCGTools.MOD_ID, "entities/draconic_template")
    );
}

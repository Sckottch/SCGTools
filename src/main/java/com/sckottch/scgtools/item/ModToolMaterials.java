package com.sckottch.scgtools.item;

import com.sckottch.scgtools.SCGTools;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

public class ModToolMaterials {
    public static final TagKey<Item> FLINT_REPAIR_ITEMS = TagKey.create(
        Registries.ITEM,
        Identifier.fromNamespaceAndPath(SCGTools.MOD_ID, "flint_repair_items")
    );
    
    public static final ToolMaterial FLINT_MATERIAL = new ToolMaterial(
        BlockTags.INCORRECT_FOR_STONE_TOOL,
        120,
        5f,
        1f,
        5,
        FLINT_REPAIR_ITEMS
    );
}

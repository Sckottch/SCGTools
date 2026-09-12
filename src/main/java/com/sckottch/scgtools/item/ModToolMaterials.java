package com.sckottch.scgtools.item;

import com.sckottch.scgtools.utility.tags.ModTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ToolMaterial;

public class ModToolMaterials {
    public static final ToolMaterial FLINT_MATERIAL = new ToolMaterial(
        BlockTags.INCORRECT_FOR_STONE_TOOL,
        120,
        5f,
        1f,
        5,
        ModTags.Items.FLINT_REPAIR_ITEMS
    );
    
    public static final ToolMaterial DRACONIC_MATERIAL = new ToolMaterial(
        BlockTags.INCORRECT_FOR_NETHERITE_TOOL,
        3031,
        15.0F,
        6.0F,
        25,
        ModTags.Items.DRACONIC_REPAIR_ITEMS
    );
}

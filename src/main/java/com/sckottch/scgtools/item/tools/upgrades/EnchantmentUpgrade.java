package com.sckottch.scgtools.item.tools.upgrades;

import com.sckottch.scgtools.utility.enums.ToolTier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class EnchantmentUpgrade extends ToolUpgrade {
    public EnchantmentUpgrade(ToolTier minTier, String displayName, TagKey<Item> applicableTo) {
        super(minTier,displayName, applicableTo);
    }
}

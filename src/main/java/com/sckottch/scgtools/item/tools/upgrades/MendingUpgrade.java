package com.sckottch.scgtools.item.tools.upgrades;

import com.sckottch.scgtools.utility.enums.ToolTier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class MendingUpgrade extends ToolUpgrade {
    public MendingUpgrade(ToolTier tier, String displayName, TagKey<Item> applicableTo) {
        super(tier, displayName, applicableTo);
    }
}

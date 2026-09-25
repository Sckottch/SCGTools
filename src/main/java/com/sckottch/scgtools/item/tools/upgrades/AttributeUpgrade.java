package com.sckottch.scgtools.item.tools.upgrades;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

public interface AttributeUpgrade {
    void applyAttributes(ItemStack tool, ItemAttributeModifierEvent event);
}

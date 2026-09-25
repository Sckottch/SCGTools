package com.sckottch.scgtools.item.tools.upgrades;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface UseUpgrade {
    InteractionResult onUse(Level level, Player player, InteractionHand hand, ItemStack tool);
    boolean isEnabled(ItemStack tool);
    void toggle(ItemStack tool);
}

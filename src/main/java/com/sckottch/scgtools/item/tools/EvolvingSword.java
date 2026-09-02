package com.sckottch.scgtools.item.tools;

import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class EvolvingSword extends Item implements EvolvingTool {
    public EvolvingSword(Properties props) {
        super(props);
    }
    
    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        evolvingTooltip(itemStack, builder, tooltipFlag);
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
    }
    
    @Override
    public TagKey<Block> getMineableTag() {
        return BlockTags.SWORD_INSTANTLY_MINES;
    }
    
    @Override
    public float getBaseAttackDamage() {
        return 3;
    }
    
    @Override
    public float getBaseAttackSpeed() {
        return -2.4F;
    }
    
    @Override
    public int xpForNextLevel(int level) {
        return (level + 1) * 2;
    }
}

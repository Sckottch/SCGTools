package com.sckottch.scgtools.item.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class EvolvingPicaxe extends Item implements EvolvingTool {
    private static final int XP_PER_BLOCK = 1;
    
    public EvolvingPicaxe(Properties props) {
        super(props);
    }
    
    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> builder, TooltipFlag tooltipFlag) {
        evolvingTooltip(itemStack, builder, tooltipFlag);
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
    }
    
    @Override
    public boolean mineBlock(ItemStack itemStack, Level level, BlockState state, BlockPos pos, LivingEntity owner) {
        if (isCorrectToolForDrops(itemStack, state)) {
            addXp(itemStack, XP_PER_BLOCK);
        }
        
        return super.mineBlock(itemStack, level, state, pos, owner);
    }
    
    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        if (enchantmentBlocked(enchantment)) {
            return false;
        }
        return super.supportsEnchantment(stack, enchantment);
    }
    
    @Override
    public int xpForNextLevel(int level) {
        return 2 * (level + 1);
    }
    
    @Override
    public float getBaseAttackDamage() {
        return 1;
    }
    
    @Override
    public float getBaseAttackSpeed() {
        return -2.8f;
    }
    
    @Override
    public TagKey<Block> getMineableTag() {
        return BlockTags.MINEABLE_WITH_PICKAXE;
    }
}

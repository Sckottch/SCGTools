package com.sckottch.scgtools.item.tools;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
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
    public void postHurtEnemy(ItemStack itemStack, LivingEntity mob, LivingEntity attacker) {
        super.postHurtEnemy(itemStack, mob, attacker);
        
        if (!(attacker.level() instanceof ServerLevel level)) return;
        if(!(mob.isDeadOrDying())) return;
        
        addXp(itemStack, mob.getExperienceReward(level, attacker));
    }
    
    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        if (enchantmentBlocked(enchantment)) return false;
        return super.supportsEnchantment(stack, enchantment);
    }
    
    @Override
    public boolean canBeHurtBy(ItemStack stack, DamageSource source) {
        if (!tierAllowsDamage(stack, source)) return false;
        return super.canBeHurtBy(stack, source);
    }
    
    @Override
    public Tool buildTool(ToolMaterial mat, HolderGetter<Block> blocks) {
        return new Tool(
            List.of(
                Tool.Rule.minesAndDrops(HolderSet.direct(Blocks.COBWEB.builtInRegistryHolder()), 15.0F),
                Tool.Rule.overrideSpeed(blocks.getOrThrow(BlockTags.SWORD_INSTANTLY_MINES), Float.MAX_VALUE),
                Tool.Rule.overrideSpeed(blocks.getOrThrow(getMineableTag()), 1.5F)
            ), 1.0F, 2, false
        );
    }
    
    @Override
    public TagKey<Block> getMineableTag() {
        return BlockTags.SWORD_EFFICIENT;
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

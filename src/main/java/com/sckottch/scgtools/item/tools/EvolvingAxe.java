package com.sckottch.scgtools.item.tools;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class EvolvingAxe extends AxeItem implements MiningEvolvingTool {
    public EvolvingAxe(ToolMaterial material, float baseAttackDamage, float baseAttackSpeed, Properties props) {
        super(material, baseAttackDamage, baseAttackSpeed, props);
    }
    
    @Override
    public boolean mineBlock(ItemStack itemStack, Level level, BlockState state, BlockPos pos, LivingEntity owner) {
        if (isCorrectToolForDrops(itemStack, state)) addXp(itemStack, 1);
        return super.mineBlock(itemStack, level, state, pos, owner);
    }
    
    @Override
    public void postHurtEnemy(ItemStack itemStack, LivingEntity mob, LivingEntity attacker) {
        super.postHurtEnemy(itemStack, mob, attacker);
        
        if (!(attacker.level() instanceof ServerLevel level)) return;
        if(!(mob.isDeadOrDying())) return;
        
        addXp(itemStack, mob.getExperienceReward(level, attacker));
    }
    
    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        evolvingTooltip(itemStack, builder, tooltipFlag);
        miningModeTooltip(itemStack, builder);
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!player.isShiftKeyDown()) return super.use(level, player, hand);
        return changeMiningMode(level, player, hand);
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
    public int xpForNextLevel(int level) {
        return 2 * (level + 1);
    }
    
    @Override
    public float getBaseAttackDamage() {
        return 7.0F;
    }
    
    @Override
    public float getBaseAttackSpeed() {
        return -3.2F;
    }
    
    @Override
    public TagKey<Block> getMineableTag() {
        return BlockTags.MINEABLE_WITH_AXE;
    }
}

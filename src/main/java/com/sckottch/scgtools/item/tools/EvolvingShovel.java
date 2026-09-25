package com.sckottch.scgtools.item.tools;

import com.sckottch.scgtools.item.tools.upgrades.CompactorUpgrade;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class EvolvingShovel extends ShovelItem implements MiningEvolvingTool {
    private final float baseAttackDamage;
    private final float baseAttackSpeed;
    
    public EvolvingShovel(ToolMaterial material, float baseAttackDamage, float baseAttackSpeed, Properties props) {
        this.baseAttackDamage = baseAttackDamage;
        this.baseAttackSpeed = baseAttackSpeed;
        super(material, baseAttackDamage, baseAttackSpeed, props);
    }
    
    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        evolvingTooltip(itemStack, builder, tooltipFlag);
        miningModeTooltip(itemStack, builder);
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
    }
    
    @Override
    public boolean mineBlock(ItemStack itemStack, Level level, BlockState state, BlockPos pos, LivingEntity owner) {
        if (isCorrectToolForDrops(itemStack, state)) addXp(itemStack, 1);
        return super.mineBlock(itemStack, level, state, pos, owner);
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!player.isShiftKeyDown()) return super.use(level, player, hand);
        return changeMiningMode(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        CompactorUpgrade compactor = CompactorUpgrade.active(stack);
        if (compactor != null && !isDepleted(stack)) return compactor.tryCompact(context);

        InteractionResult result = super.useOn(context);
        if (result.consumesAction()) useOnArea(context, super::useOn, true);
        return result;
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
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
        return getDamageItem(stack, amount);
    }
    
    @Override
    public float getDestroySpeed(ItemStack itemStack, BlockState state) {
        if (isDepleted(itemStack)) return 1.0F;
        return super.getDestroySpeed(itemStack, state);
    }
    
    @Override
    public boolean isCorrectToolForDrops(ItemStack itemStack, BlockState state) {
        if (isDepleted(itemStack)) return false;
        return super.isCorrectToolForDrops(itemStack, state);
    }
    
    @Override
    public TagKey<Block> getMineableTag() {
        return BlockTags.MINEABLE_WITH_SHOVEL;
    }
    
    @Override
    public float getBaseAttackDamage() {
        return baseAttackDamage;
    }
    
    @Override
    public float getBaseAttackSpeed() {
        return baseAttackSpeed;
    }
}

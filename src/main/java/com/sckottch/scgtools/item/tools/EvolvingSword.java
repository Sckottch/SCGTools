package com.sckottch.scgtools.item.tools;

import com.sckottch.scgtools.components.ModDataComponents;
import com.sckottch.scgtools.item.tools.upgrades.ToolUpgrade;
import com.sckottch.scgtools.item.tools.upgrades.UseUpgrade;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class EvolvingSword extends Item implements EvolvingTool {
    public EvolvingSword(Properties props) {
        super(props);
    }
    
    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        evolvingTooltip(itemStack, builder, tooltipFlag);

        List<ToolUpgrade> upgrades = itemStack.get(ModDataComponents.TOOL_UPGRADES.get());
        if (upgrades != null) {
            for (ToolUpgrade upgrade : upgrades) {
                if (upgrade instanceof UseUpgrade useUpgrade) {
                    builder.accept(Component.literal(upgrade.getDisplayName() + ": " + (useUpgrade.isEnabled(itemStack) ? "ON" : "OFF")));
                }
            }
        }

        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
    }
    
    @Override
    public void postHurtEnemy(ItemStack itemStack, LivingEntity mob, LivingEntity attacker) {
        super.postHurtEnemy(itemStack, mob, attacker);
        
        if (!(attacker.level() instanceof ServerLevel level)) return;
        if (!(mob.isDeadOrDying())) return;
        
        addXp(itemStack, mob.getExperienceReward(level, attacker));
    }
    
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (isDepleted(stack)) return super.use(level, player, hand);

        List<ToolUpgrade> upgrades = stack.get(ModDataComponents.TOOL_UPGRADES.get());
        if (upgrades != null) {
            for (ToolUpgrade upgrade : upgrades) {
                if (upgrade instanceof UseUpgrade useUpgrade && useUpgrade.isEnabled(stack)) {
                    InteractionResult result = useUpgrade.onUse(level, player, hand, stack);
                    if (result != InteractionResult.PASS) return result;
                }
            }
        }

        return super.use(level, player, hand);
    }

    @Override
    public void handleCycleKey(ServerPlayer player, ItemStack stack) {
        List<ToolUpgrade> upgrades = stack.get(ModDataComponents.TOOL_UPGRADES.get());
        if (upgrades == null) return;

        for (ToolUpgrade upgrade : upgrades) {
            if (upgrade instanceof UseUpgrade useUpgrade) {
                useUpgrade.toggle(stack);
                String state = useUpgrade.isEnabled(stack) ? "ON" : "OFF";
                player.sendOverlayMessage(Component.literal(upgrade.getDisplayName() + ": " + state));
                return;
            }
        }
    }
    
    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        if (enchantmentBlocked(enchantment)) return false;
        return super.supportsEnchantment(stack, enchantment);
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
}

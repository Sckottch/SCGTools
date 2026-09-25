package com.sckottch.scgtools.item.tools.upgrades;

import com.sckottch.scgtools.block.CompactedEarthBlock;
import com.sckottch.scgtools.block.ModBlocks;
import com.sckottch.scgtools.components.ModDataComponents;
import com.sckottch.scgtools.utility.enums.MiningMode;
import com.sckottch.scgtools.utility.enums.ToolTier;
import com.sckottch.scgtools.utility.tags.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BambooSaplingBlock;
import net.minecraft.world.level.block.BambooStalkBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class CompactorUpgrade extends ToolUpgrade implements BlockBreakingUpgrade {

    public CompactorUpgrade(ToolTier minTier, String displayName, TagKey<Item> applicableTo) {
        super(minTier, displayName, applicableTo);
    }

    @Override
    public void onApply(ItemStack stack) {
        stack.set(ModDataComponents.COMPACTOR_LEVEL.get(), getMaxLevel(stack));
    }

    public int getMaxLevel(ItemStack tool) {
        ToolTier tier = tool.get(ModDataComponents.TOOL_TIER.get());
        return tier == null ? 0 : tier.getId();
    }

    public int getSelectedLevel(ItemStack tool) {
        int max = getMaxLevel(tool);
        return Mth.clamp(tool.getOrDefault(ModDataComponents.COMPACTOR_LEVEL.get(), max), 1, max);
    }

    public static int bonusPercent(int level) {
        return 5 * (level + 1);
    }

    @Override
    public MiningMode getMiningMode() {
        return MiningMode.SPECIAL;
    }

    @Override
    public float getSpeedMultiplier(ItemStack tool) {
        return 1.0F;
    }

    @Override
    public String getModeDescriptor(ItemStack tool) {
        return "+" + bonusPercent(getSelectedLevel(tool)) + "%";
    }

    @Override
    public void cycleUpgradeMode(ItemStack tool) {
        int max = getMaxLevel(tool);
        if (max <= 1) return;

        tool.set(ModDataComponents.COMPACTOR_LEVEL.get(), getSelectedLevel(tool) % max + 1);
    }

    @Override
    public List<BlockPos> getBlocksToBreak(ItemStack tool, Level level, BlockPos origin, ServerPlayer player) {
        return List.of();
    }

    public InteractionResult tryCompact(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clicked = context.getClickedPos();
        if (context.getClickedFace() == Direction.DOWN) return InteractionResult.PASS;

        BlockPos target = isCompactable(level.getBlockState(clicked)) ? clicked
            : isCompactable(level.getBlockState(clicked.below())) ? clicked.below()
            : null;
        if (target == null || isPlant(level.getBlockState(target.above()))) return InteractionResult.PASS;

        ItemStack stack = context.getItemInHand();
        BlockState newState = ModBlocks.COMPACTED_EARTH.get().defaultBlockState()
            .setValue(CompactedEarthBlock.LEVEL, getSelectedLevel(stack));
        if (level.getBlockState(target) == newState) return InteractionResult.PASS;

        Player player = context.getPlayer();
        level.playSound(player, target, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (!level.isClientSide()) {
            level.setBlock(target, newState, 11);
            level.gameEvent(GameEvent.BLOCK_CHANGE, target, GameEvent.Context.of(player, newState));
            if (player != null) stack.hurtAndBreak(1, player, context.getHand().asEquipmentSlot());
        }

        return InteractionResult.SUCCESS;
    }

    private static boolean isCompactable(BlockState state) {
        return state.is(ModTags.Blocks.COMPACTABLE) || state.is(ModBlocks.COMPACTED_EARTH.get());
    }

    private static boolean isPlant(BlockState state) {
        return state.getBlock() instanceof VegetationBlock
            || state.getBlock() instanceof SugarCaneBlock
            || state.getBlock() instanceof BambooStalkBlock
            || state.getBlock() instanceof BambooSaplingBlock;
    }

    public static @Nullable CompactorUpgrade active(ItemStack stack) {
        if (stack.getOrDefault(ModDataComponents.MINING_MODE.get(), MiningMode.NORMAL) != MiningMode.SPECIAL) return null;

        List<ToolUpgrade> upgrades = stack.get(ModDataComponents.TOOL_UPGRADES.get());
        if (upgrades == null) return null;

        for (ToolUpgrade upgrade : upgrades) {
            if (upgrade instanceof CompactorUpgrade compactor) return compactor;
        }
        return null;
    }
}

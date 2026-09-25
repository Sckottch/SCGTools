package com.sckottch.scgtools.utility.events;

import com.sckottch.scgtools.SCGTools;
import com.sckottch.scgtools.block.FertilizedFarmlandBlock;
import com.sckottch.scgtools.block.ModBlocks;
import com.sckottch.scgtools.item.tools.MiningEvolvingTool;
import com.sckottch.scgtools.item.tools.upgrades.FertilizerUpgrade;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = SCGTools.MOD_ID)
public class ModFarmlandHandler {

    @SubscribeEvent
    public static void onToolModification(BlockEvent.BlockToolModificationEvent event) {
        if (event.getItemAbility() != ItemAbilities.HOE_TILL) return;

        ItemStack stack = event.getHeldItemStack();
        if (!(stack.getItem() instanceof MiningEvolvingTool tool) || tool.isDepleted(stack)) return;

        FertilizerUpgrade fertilizer = FertilizerUpgrade.active(stack);
        if (fertilizer == null) return;

        int newLevel = fertilizer.getLevel(stack);
        if (newLevel <= 0) return;

        BlockState original = event.getFinalState();
        BlockState fertilized = ModBlocks.FERTILIZED_FARMLAND.get().defaultBlockState()
            .setValue(FertilizedFarmlandBlock.LEVEL, newLevel)
            .setValue(FertilizedFarmlandBlock.FRESH, true);

        if (original.is(ModBlocks.FERTILIZED_FARMLAND.get())) {
            if (original.getValue(FertilizedFarmlandBlock.LEVEL) >= newLevel) return;
            event.setFinalState(original.setValue(FertilizedFarmlandBlock.LEVEL, newLevel).setValue(FertilizedFarmlandBlock.FRESH, true));
            return;
        }

        if (original.is(Blocks.FARMLAND)) {
            event.setFinalState(fertilized.setValue(FarmlandBlock.MOISTURE, original.getValue(FarmlandBlock.MOISTURE)));
            return;
        }

        BlockState vanillaResult = original.getBlock().getToolModifiedState(original, event.getContext(), ItemAbilities.HOE_TILL, true);
        if (vanillaResult != null && vanillaResult.is(Blocks.FARMLAND)) {
            event.setFinalState(fertilized);
        }
    }

    @SubscribeEvent
    public static void onTrample(BlockEvent.FarmlandTrampleEvent event) {
        // o estado do evento é o destino do pisoteio (terra), então o bloco pisoteado vem do mundo
        if (event.getLevel().getBlockState(event.getPos()).getBlock() instanceof FertilizedFarmlandBlock) {
            event.setCanceled(true);
        }
    }
}

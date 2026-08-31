package com.sckottch.scgtools.block.entities.forge;

import com.sckottch.scgtools.block.ModBlocks;
import com.sckottch.scgtools.item.tools.EvolvingTool;
import com.sckottch.scgtools.menu.ModMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class ForgeMenu extends AbstractContainerMenu {
    private final ForgeBlockEntity blockEntity;
    private final Level level;

    public ForgeMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(containerId, inventory, inventory.player.level().getBlockEntity(buf.readBlockPos()), new ItemStacksResourceHandler(ForgeBlockEntity.TOTAL_SLOTS));
    }

    public ForgeMenu(int containerId, Inventory inventory, BlockEntity entity, ItemStacksResourceHandler handler) {
        super(ModMenus.FORGE.get(), containerId);
        this.blockEntity = (ForgeBlockEntity) entity;
        this.level = inventory.player.level();
        
        addPlayerHotbar(inventory);
        addPlayerInventory(inventory);

        for (int row = 0; row < 3; row ++){
            for (int col = 0; col < 2; col++) {
                this.addSlot(new ResourceHandlerSlot(handler, handler::set, row * 2 + col, 30 + col * 18, 20 + row * 18));
            }
        }
        
        this.addSlot(new ResourceHandlerSlot(handler, handler::set, ForgeBlockEntity.MAIN_SLOT, 100, 29) {
            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        
        this.addSlot(new ResourceHandlerSlot(handler, handler::set, ForgeBlockEntity.OUTPUT_SLOT, 140, 29) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
    }
    
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        
        if (index < ForgeBlockEntity.TOTAL_SLOTS) {
            if (!moveItemStackTo(stack, ForgeBlockEntity.TOTAL_SLOTS, this.slots.size(), true)) return ItemStack.EMPTY;
        } else {
            Slot mainSlot = this.slots.get(ForgeBlockEntity.MAIN_SLOT);
            
            if (isMainSlotItem(stack) && !mainSlot.hasItem()) {
                ItemStack single = stack.split(1);
                mainSlot.set(single);
            } else {
                if (!moveItemStackTo(stack, 0, ForgeBlockEntity.RECIPE_SLOTS, false)) return ItemStack.EMPTY;
            }
        }
        
        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        
        return original;
    }
    
    private boolean isMainSlotItem(ItemStack stack) {
        return stack.getItem() instanceof EvolvingTool;
    }
    
    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
            player, ModBlocks.FORGE.get()
        );
    }
    
    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }
    
    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}

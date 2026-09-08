package com.sckottch.scgtools.forge;

import com.sckottch.scgtools.block.ModBlocks;
import com.sckottch.scgtools.menu.ModMenus;
import com.sckottch.scgtools.utility.enums.UpgradeValidationResult;
import com.sckottch.scgtools.utility.tags.ModTags;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot;

public class ForgeMenu extends AbstractContainerMenu {
    private final ForgeBlockEntity blockEntity;
    private final Level level;
    private final Player player;

    private final DataSlot xpCost = DataSlot.standalone();
    private final DataSlot affordable = DataSlot.standalone();
    private final DataSlot validation = DataSlot.standalone();

    public ForgeMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(containerId, inventory, inventory.player.level().getBlockEntity(buf.readBlockPos()), new ForgeBlockEntity.ForgeInventory());
    }

    public ForgeMenu(int containerId, Inventory inventory, BlockEntity entity, ItemStacksResourceHandler handler) {
        super(ModMenus.FORGE.get(), containerId);
        this.blockEntity = (ForgeBlockEntity) entity;
        this.level = inventory.player.level();
        this.player = inventory.player;

        for (int row = 0; row < 3; row ++){
            for (int col = 0; col < 2; col++) {
                this.addSlot(new ResourceHandlerSlot(handler, handler::set, row * 2 + col, 8 + col * 18, 12 + row * 18));
            }
        }

        this.addSlot(new ResourceHandlerSlot(handler, handler::set, ForgeBlockEntity.MAIN_SLOT, 72, 30));

        this.addSlot(new ResourceHandlerSlot(handler, handler::set, ForgeBlockEntity.OUTPUT_SLOT, 152, 30) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public boolean mayPickup(Player player) {
                return blockEntity.canCraft(player);
            }

            @Override
            public void onTake(Player player, ItemStack carried) {
                blockEntity.onCrafted(player);
                super.onTake(player, carried);
            }
        });

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);

        this.addDataSlot(xpCost);
        this.addDataSlot(affordable);
        this.addDataSlot(validation);
    }

    @Override
    public void broadcastChanges() {
        if (!level.isClientSide()) {
            ForgeBlockEntity.ForgeStatus status = blockEntity.getStatus();

            xpCost.set(status.xpCost());
            affordable.set(blockEntity.canAfford(player, status.xpCost()) ? 1 : 0);
            validation.set(status.validation().ordinal());
        }
        super.broadcastChanges();
    }

    public int getXpCost() {
        return xpCost.get();
    }

    public boolean canAffordXp() {
        return affordable.get() != 0;
    }

    public UpgradeValidationResult getValidation() {
        UpgradeValidationResult[] results = UpgradeValidationResult.values();
        int index = validation.get();

        return index >= 0 && index < results.length ? results[index] : UpgradeValidationResult.OK;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index == ForgeBlockEntity.OUTPUT_SLOT) {
            if (!slot.mayPickup(player)) return ItemStack.EMPTY;
            if (!moveItemStackTo(stack, ForgeBlockEntity.TOTAL_SLOTS, this.slots.size(), true)) return ItemStack.EMPTY;
            slot.onTake(player, stack);
        } else if (index < ForgeBlockEntity.TOTAL_SLOTS) {
            if (!moveItemStackTo(stack, ForgeBlockEntity.TOTAL_SLOTS, this.slots.size(), true)) return ItemStack.EMPTY;
        } else if (stack.is(ModTags.Items.FORGE_MAIN)) {
            Slot mainSlot = this.slots.get(ForgeBlockEntity.MAIN_SLOT);
            if (mainSlot.hasItem()) return ItemStack.EMPTY;
            mainSlot.set(stack.split(1));
        } else {
            if (!moveItemStackTo(stack, 0, ForgeBlockEntity.RECIPE_SLOTS, false)) return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        return original;
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

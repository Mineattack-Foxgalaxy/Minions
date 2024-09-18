package io.github.skippyall.minions.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public class ModuleInventoryScreenHandler extends ScreenHandler {
    private final int rows = 3;
    private final ModuleInventory inventory;
    public ModuleInventoryScreenHandler(int syncId, ModuleInventory inventory) {
        super(ScreenHandlerType.GENERIC_9X3, syncId);
        this.inventory = inventory;
    }

    public ModuleInventoryScreenHandler(int syncId, PlayerInventory playerInventory, ModuleInventory inventory) {
        super(ScreenHandlerType.GENERIC_9X3, syncId);
        int k;
        int j;
        GenericContainerScreenHandler.checkSize(inventory, 3 * 9);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);
        int i = (rows - 4) * 18;
        for (j = 0; j < rows; ++j) {
            for (k = 0; k < 9; ++k) {
                this.addSlot(new Slot(inventory, k + j * 9, 8 + k * 18, 18 + j * 18) {
                    @Override
                    public boolean canInsert(ItemStack stack) {
                        return super.canInsert(stack) && inventory.isValid(getIndex(), stack);
                    }
                });
            }
        }
        for (j = 0; j < 3; ++j) {
            for (k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));
            }
        }
        for (j = 0; j < 9; ++j) {
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 161 + i));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot < this.rows * 9 ? !this.insertItem(itemStack2, this.rows * 9, this.slots.size(), true) : !this.insertItem(itemStack2, 0, this.rows * 9, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
        }
        return itemStack;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }

    public ModuleInventory getInventory() {
        return inventory;
    }
}

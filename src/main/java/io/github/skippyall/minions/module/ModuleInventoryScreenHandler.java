package io.github.skippyall.minions.module;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ModuleInventoryScreenHandler extends AbstractContainerMenu {
    private final int rows = 3;
    private final ModuleInventory inventory;

    public ModuleInventoryScreenHandler(int syncId, Inventory playerInventory, ModuleInventory inventory) {
        super(MenuType.GENERIC_9x3, syncId);

        ChestMenu.checkContainerSize(inventory, 3 * 9);
        this.inventory = inventory;
        inventory.startOpen(playerInventory.player);

        for (int j = 0; j < rows; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(inventory, k + j * 9, 8 + k * 18, 18 + j * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return super.mayPlace(stack) && container.canPlaceItem(getContainerSlot(), stack);
                    }
                });
            }
        }

        addStandardInventorySlots(playerInventory, 8, 85);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2 != null && slot2.hasItem()) {
            ItemStack itemStack2 = slot2.getItem();
            itemStack = itemStack2.copy();
            if (slot < this.rows * 9 ? !this.moveItemStackTo(itemStack2, this.rows * 9, this.slots.size(), true) : !this.moveItemStackTo(itemStack2, 0, this.rows * 9, false)) {
                return ItemStack.EMPTY;
            }
            if (itemStack2.isEmpty()) {
                slot2.setByPlayer(ItemStack.EMPTY);
            } else {
                slot2.setChanged();
            }
        }
        return itemStack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.inventory.stopOpen(player);
    }

    public ModuleInventory getInventory() {
        return inventory;
    }
}

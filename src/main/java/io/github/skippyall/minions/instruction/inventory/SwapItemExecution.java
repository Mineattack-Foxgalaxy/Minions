package io.github.skippyall.minions.instruction.inventory;

import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.consumer.ValueConsumerList;
import io.github.skippyall.minions.program.instruction.InstructionExecution;
import io.github.skippyall.minions.program.supplier.ValueSupplierList;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

public class SwapItemExecution implements InstructionExecution<MinionRuntime> {
    private int fromSlot;
    private boolean fromScreen;
    private int toSlot;
    private boolean toScreen;

    @Override
    public void start(MinionRuntime runtime) {
        MinionFakePlayer minion = runtime.getMinion();

        if((fromScreen || toScreen) && minion.currentScreenHandler == null) {
            return;
        }
        if(!(checkBounds(minion, fromSlot, fromScreen) && checkBounds(minion, toSlot, toScreen))) {
            return;
        }

        ItemStack fromStack = getStack(minion, fromSlot, fromScreen);
        ItemStack toStack = getStack(minion, toSlot, toScreen);

        if(!(canExchange(minion, fromSlot, fromScreen, toStack) && canExchange(minion, toSlot, toScreen, fromStack))) {
            return;
        }


    }

    private boolean checkBounds(MinionFakePlayer minion, int slot, boolean screen) {
        if(screen) {
            return slot >= 0 && slot < minion.currentScreenHandler.slots.size();
        } else {
            return slot >= 0 && slot <= PlayerInventory.OFF_HAND_SLOT;
        }
    }

    private ItemStack getStack(MinionFakePlayer minion, int slot, boolean screen) {
        if(screen) {
            return minion.currentScreenHandler.getSlot(slot).getStack();
        } else {
            return minion.getInventory().getStack(slot);
        }
    }

    private boolean canExchange(MinionFakePlayer minion, int slotIndex, boolean screen, ItemStack newStack) {
        if(screen) {
            Slot slot = minion.currentScreenHandler.getSlot(slotIndex);
            if(!slot.getStack().isEmpty() && !slot.canTakeItems(minion)) {
                return false;
            }
            if(!newStack.isEmpty() && !slot.canInsert(newStack)) {
                return false;
            }
        } else {
            if(slotIndex >= PlayerInventory.MAIN_SIZE && slotIndex < PlayerInventory.OFF_HAND_SLOT) {
                if(!minion.canEquip(newStack, PlayerInventory.EQUIPMENT_SLOTS.get(slotIndex))) {
                    return false;
                }
                if(EnchantmentHelper.hasAnyEnchantmentsWith(minion.getInventory().getStack(slotIndex), EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void simulateClick(MinionFakePlayer minion, int slotIndex, boolean screen) {
        if(screen) {
            minion.currentScreenHandler.onSlotClick(slotIndex, 0, SlotActionType.SWAP, minion);
        } else {
        }
    }

    @Override
    public void tick(MinionRuntime runtime) {
        InstructionExecution.super.tick(runtime);
    }

    @Override
    public boolean isDone(MinionRuntime runtime) {
        return false;
    }

    @Override
    public void stop(MinionRuntime runtime, ValueConsumerList<MinionRuntime> valueConsumers) {
        InstructionExecution.super.stop(runtime, valueConsumers);
    }

    @Override
    public void readArguments(ValueSupplierList<MinionRuntime> arguments, MinionRuntime runtime) {

    }

    @Override
    public void save(WriteView view, MinionRuntime runtime) {

    }

    @Override
    public void load(ReadView view, MinionRuntime runtime) {

    }
}

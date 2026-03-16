package io.github.skippyall.minions.minion.program.instruction.inventory;

import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.consumer.ValueConsumerList;
import io.github.skippyall.minions.program.instruction.InstructionExecution;
import io.github.skippyall.minions.program.supplier.Parameter;
import io.github.skippyall.minions.program.supplier.ValueSupplierList;
import io.github.skippyall.minions.registration.ValueTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

public class SwapItemExecution implements InstructionExecution<MinionRuntime> {
    public static final Parameter<Long> FROM_SLOT = new Parameter<>("from_slot", ValueTypes.LONG);
    public static final Parameter<Boolean> FROM_SCREEN = new Parameter<>("from_screen", ValueTypes.BOOLEAN);
    public static final Parameter<Long> TO_SLOT = new Parameter<>("to_slot", ValueTypes.LONG);
    public static final Parameter<Boolean> TO_SCREEN = new Parameter<>("to_screen", ValueTypes.BOOLEAN);

    private int fromSlot;
    private boolean fromScreen;
    private int toSlot;
    private boolean toScreen;

    private ItemStack cursor = ItemStack.EMPTY;

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

        simulateClick(minion, fromSlot, fromScreen);
        simulateClick(minion, toSlot, toScreen);
        simulateClick(minion, fromSlot, fromScreen);
        minion.getInventory().offerOrDrop(cursor);
    }

    private ScreenHandler getScreen(MinionFakePlayer minion, boolean screen) {
        if(screen) {
            return minion.currentScreenHandler;
        } else  {
            return minion.playerScreenHandler;
        }
    }

    private boolean checkBounds(MinionFakePlayer minion, int slot, boolean screen) {
        return slot >= 0 && slot < getScreen(minion, screen).slots.size();
    }

    private ItemStack getStack(MinionFakePlayer minion, int slot, boolean screen) {
        return getScreen(minion, screen).getSlot(slot).getStack();
    }

    private boolean canExchange(MinionFakePlayer minion, int slotIndex, boolean screen, ItemStack newStack) {
        ScreenHandler screenHandler = getScreen(minion, screen);
        Slot slot = screenHandler.getSlot(slotIndex);
        if(!slot.getStack().isEmpty() && !slot.canTakeItems(minion)) {
            return false;
        }
        if(!newStack.isEmpty() && !slot.canInsert(newStack)) {
            return false;
        }
        /*else {
            if(slotIndex >= PlayerInventory.MAIN_SIZE && slotIndex < PlayerInventory.OFF_HAND_SLOT) {
                if(!minion.canEquip(newStack, PlayerInventory.EQUIPMENT_SLOTS.get(slotIndex))) {
                    return false;
                }
                if(EnchantmentHelper.hasAnyEnchantmentsWith(minion.getInventory().getStack(slotIndex), EnchantmentEffectComponentTypes.PREVENT_ARMOR_CHANGE)) {
                    return false;
                }
            }
        }*/
        return true;
    }

    private void simulateClick(MinionFakePlayer minion, int slotIndex, boolean screen) {
        ScreenHandler screenHandler = getScreen(minion, screen);
        ItemStack previousCursor = screenHandler.getCursorStack();
        screenHandler.setCursorStack(cursor);
        screenHandler.onSlotClick(slotIndex, 0, SlotActionType.SWAP, minion);
        cursor = screenHandler.getCursorStack();
        screenHandler.setCursorStack(previousCursor);
    }

    @Override
    public void tick(MinionRuntime runtime) {
        InstructionExecution.super.tick(runtime);
    }

    @Override
    public boolean isDone(MinionRuntime runtime) {
        return true;
    }

    @Override
    public void stop(MinionRuntime runtime, ValueConsumerList<MinionRuntime> valueConsumers) {
        InstructionExecution.super.stop(runtime, valueConsumers);
    }

    @Override
    public void readArguments(ValueSupplierList<MinionRuntime> arguments, MinionRuntime runtime) {
        fromSlot = Math.clamp(arguments.getValue(FROM_SLOT, runtime), 0, Integer.MAX_VALUE);
        fromScreen = arguments.getValue(FROM_SCREEN, runtime);
        toSlot = Math.clamp(arguments.getValue(TO_SLOT, runtime), 0, Integer.MAX_VALUE);
        toScreen = arguments.getValue(TO_SCREEN, runtime);
    }

    @Override
    public void save(WriteView view, MinionRuntime runtime) {

    }

    @Override
    public void load(ReadView view, MinionRuntime runtime) {

    }
}

package io.github.skippyall.minions.minion.program.instruction.inventory;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.ExecutionContext;
import io.github.skippyall.minions.program.instruction.InstructionExecution;
import io.github.skippyall.minions.program.supplier.Parameter;
import io.github.skippyall.minions.program.supplier.ParameterValueList;
import io.github.skippyall.minions.registration.ValueTypes;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SwapItemExecution implements InstructionExecution {
    public static final Codec<SwapItemExecution> CODEC = MapCodec.unitCodec(SwapItemExecution::new);

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
    public void start(ExecutionContext context) {
        MinionFakePlayer minion = context.getOrThrow(MinionRuntime.MINION_KEY);

        if((fromScreen || toScreen) && minion.containerMenu == null) {
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
        minion.getInventory().placeItemBackInInventory(cursor);
    }

    private AbstractContainerMenu getScreen(MinionFakePlayer minion, boolean screen) {
        if(screen) {
            return minion.containerMenu;
        } else  {
            return minion.inventoryMenu;
        }
    }

    private boolean checkBounds(MinionFakePlayer minion, int slot, boolean screen) {
        return slot >= 0 && slot < getScreen(minion, screen).slots.size();
    }

    private ItemStack getStack(MinionFakePlayer minion, int slot, boolean screen) {
        return getScreen(minion, screen).getSlot(slot).getItem();
    }

    private boolean canExchange(MinionFakePlayer minion, int slotIndex, boolean screen, ItemStack newStack) {
        AbstractContainerMenu screenHandler = getScreen(minion, screen);
        Slot slot = screenHandler.getSlot(slotIndex);
        if(!slot.getItem().isEmpty() && !slot.mayPickup(minion)) {
            return false;
        }
        if(!newStack.isEmpty() && !slot.mayPlace(newStack)) {
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
        AbstractContainerMenu screenHandler = getScreen(minion, screen);
        ItemStack previousCursor = screenHandler.getCarried();
        screenHandler.setCarried(cursor);
        screenHandler.clicked(slotIndex, 0, ContainerInput.SWAP, minion);
        cursor = screenHandler.getCarried();
        screenHandler.setCarried(previousCursor);
    }

    @Override
    public boolean isDone(ExecutionContext context) {
        return true;
    }

    @Override
    public void readArguments(ParameterValueList arguments, ExecutionContext context) {
        fromSlot = Math.clamp(arguments.getValue(FROM_SLOT), 0, Integer.MAX_VALUE);
        fromScreen = arguments.getValue(FROM_SCREEN);
        toSlot = Math.clamp(arguments.getValue(TO_SLOT), 0, Integer.MAX_VALUE);
        toScreen = arguments.getValue(TO_SCREEN);
    }
}

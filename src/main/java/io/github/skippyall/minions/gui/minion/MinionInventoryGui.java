package io.github.skippyall.minions.gui.minion;

import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.ArmorSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

public class MinionInventoryGui extends MinionsGui {
    protected final MinionGui parent;
    private final MinionFakePlayer minion;

    private SimpleGui gui;

    public MinionInventoryGui(MinionGui parent) {
        super(parent);
        this.parent = parent;
        this.minion = parent.getMinion();
        open();
    }

    @Override
    protected void open() {
        gui = new SimpleGui(ScreenHandlerType.GENERIC_9X6, viewer, false) {
            @Override
            public void onClose() {
                onBackingClosed();
            }
        };
        gui.setTitle(Text.translatable("minions.gui.inventory.title"));

        for(int i = 0; i < 18; i++) {
            gui.setSlot(i, new ItemStack(Items.BARRIER));
        }

        gui.setSlot(2, new ItemStack(Items.LEATHER_HELMET));
        gui.setSlot(3, new ItemStack(Items.LEATHER_CHESTPLATE));
        gui.setSlot(4, new ItemStack(Items.LEATHER_LEGGINGS));
        gui.setSlot(5, new ItemStack(Items.LEATHER_BOOTS));
        gui.setSlot(6, new ItemStack(Items.SHIELD));

        gui.setSlotRedirect(2 + 9, new ArmorSlot(minion.getInventory(), minion, EquipmentSlot.HEAD, EquipmentSlot.HEAD.getOffsetEntitySlotId(PlayerInventory.MAIN_SIZE), 0, 0, null));
        gui.setSlotRedirect(3 + 9, new ArmorSlot(minion.getInventory(), minion, EquipmentSlot.CHEST, EquipmentSlot.CHEST.getOffsetEntitySlotId(PlayerInventory.MAIN_SIZE), 0, 0, null));
        gui.setSlotRedirect(4 + 9, new ArmorSlot(minion.getInventory(), minion, EquipmentSlot.LEGS, EquipmentSlot.LEGS.getOffsetEntitySlotId(PlayerInventory.MAIN_SIZE), 0, 0, null));
        gui.setSlotRedirect(5 + 9, new ArmorSlot(minion.getInventory(), minion, EquipmentSlot.FEET, EquipmentSlot.FEET.getOffsetEntitySlotId(PlayerInventory.MAIN_SIZE), 0, 0, null));
        gui.setSlotRedirect(6 + 9, new Slot(minion.getInventory(), PlayerInventory.OFF_HAND_SLOT, 0, 0));

        for (int i = PlayerInventory.HOTBAR_SIZE; i < PlayerInventory.MAIN_SIZE; i++) {
            gui.setSlotRedirect(i + 9, new Slot(minion.getInventory(), i, 0, 0));
        }

        for (int i = 0; i < PlayerInventory.HOTBAR_SIZE; i++) {
            gui.setSlotRedirect(i + 45, new Slot(minion.getInventory(), i, 0, 0));
        }
        gui.open();
    }

    @Override
    protected void closeBacking() {
        gui.close();
    }
}

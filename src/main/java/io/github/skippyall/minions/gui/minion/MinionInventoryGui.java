package io.github.skippyall.minions.gui.minion;

import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ArmorSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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
        gui = new SimpleGui(MenuType.GENERIC_9x6, viewer, false) {
            @Override
            public void onPlayerClose(boolean success) {
                onBackingClosed();
            }
        };
        gui.setTitle(Component.translatable("minions.gui.inventory.title"));

        for(int i = 0; i < 18; i++) {
            gui.setSlot(i, new ItemStack(Items.BARRIER));
        }

        gui.setSlot(2, new ItemStack(Items.LEATHER_HELMET));
        gui.setSlot(3, new ItemStack(Items.LEATHER_CHESTPLATE));
        gui.setSlot(4, new ItemStack(Items.LEATHER_LEGGINGS));
        gui.setSlot(5, new ItemStack(Items.LEATHER_BOOTS));
        gui.setSlot(6, new ItemStack(Items.SHIELD));

        gui.setSlot(2 + 9, new ArmorSlot(minion.getInventory(), minion, EquipmentSlot.HEAD, EquipmentSlot.HEAD.getIndex(Inventory.INVENTORY_SIZE), 0, 0, null));
        gui.setSlot(3 + 9, new ArmorSlot(minion.getInventory(), minion, EquipmentSlot.CHEST, EquipmentSlot.CHEST.getIndex(Inventory.INVENTORY_SIZE), 0, 0, null));
        gui.setSlot(4 + 9, new ArmorSlot(minion.getInventory(), minion, EquipmentSlot.LEGS, EquipmentSlot.LEGS.getIndex(Inventory.INVENTORY_SIZE), 0, 0, null));
        gui.setSlot(5 + 9, new ArmorSlot(minion.getInventory(), minion, EquipmentSlot.FEET, EquipmentSlot.FEET.getIndex(Inventory.INVENTORY_SIZE), 0, 0, null));
        gui.setSlot(6 + 9, new Slot(minion.getInventory(), Inventory.SLOT_OFFHAND, 0, 0));

        for (int i = Inventory.SELECTION_SIZE; i < Inventory.INVENTORY_SIZE; i++) {
            gui.setSlot(i + 9, new Slot(minion.getInventory(), i, 0, 0));
        }

        for (int i = 0; i < Inventory.SELECTION_SIZE; i++) {
            gui.setSlot(i + 45, new Slot(minion.getInventory(), i, 0, 0));
        }
        gui.open();
    }

    @Override
    protected void closeBacking() {
        gui.close();
    }
}

package io.github.skippyall.minions.gui.minion;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.gui.instruction.InstructionGui;
import io.github.skippyall.minions.minion.MinionListener;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.module.ModuleInventory;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.ArmorSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class MinionGui extends MinionsGui implements MinionListener {
    private final MinionFakePlayer minion;
    private SimpleGui gui;

    public MinionGui(ServerPlayerEntity viewer, MinionFakePlayer minion) {
        super(viewer);
        this.minion = minion;
        minion.addMinionListener(this);
    }

    public MinionFakePlayer getMinion() {
        return minion;
    }

    @Override
    protected void open() {
        gui = new SimpleGui(ScreenHandlerType.GENERIC_3X3, viewer, false) {
            @Override
            public void onClose() {
                onBackingClosed();
            }
        };

        gui.setTitle(minion.getName());

        gui.setSlot(1, new GuiElementBuilder()
                .setItem(Items.COMMAND_BLOCK)
                .setName(Text.translatable("minions.gui.main.instructions"))
                .setCallback(() -> {
                    InstructionGui.openInstructionMainMenu(minion, viewer);
                })
        );
        gui.setSlot(3, new GuiElementBuilder()
                .setItem(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
                .setName(Text.translatable("minions.gui.main.modules"))
                .setCallback(() -> {
                    ModuleInventory.openModuleInventory(viewer, minion);
                })
        );
        gui.setSlot(5, new GuiElementBuilder()
                .setItem(Items.CHEST)
                .setName(Text.translatable("minions.gui.main.inventory"))
                .setCallback(() -> new MinionInventoryGui(this))
        );
        gui.setSlot(7, new GuiElementBuilder()
                .setItem(Items.BARRIER)
                .setName(Text.translatable("minions.gui.main.pickup"))
                .setCallback(() -> minion.kill(minion.getWorld()))
        );
        gui.open();
    }

    @Override
    protected void reopen() {
        gui.open();
    }

    @Override
    protected void onClose() {
        gui.close();
        minion.removeMinionListener(this);
    }

    @Override
    public void onMinionRemove(MinionFakePlayer minion) {
        close();
    }

    public static GuiElementBuilder backButton(Runnable onBack) {
        return new GuiElementBuilder(Items.COMPASS)
                .setItemName(Text.translatable("gui.back"))
                .setCallback(onBack);
    }
}

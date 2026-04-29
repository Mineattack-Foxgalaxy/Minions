package io.github.skippyall.minions.gui.minion;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.gui.instruction.InstructionGui;
import io.github.skippyall.minions.minion.MinionListener;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.module.ModuleInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;

public class MinionGui extends MinionsGui implements MinionListener {
    private final MinionFakePlayer minion;
    private SimpleGui gui;

    public MinionGui(ServerPlayer viewer, MinionFakePlayer minion) {
        super(viewer);
        this.minion = minion;
        minion.addMinionListener(this);
        open();
    }

    public MinionFakePlayer getMinion() {
        return minion;
    }

    @Override
    protected void open() {
        gui = new SimpleGui(MenuType.GENERIC_3x3, viewer, false) {
            @Override
            public void onClose() {
                onBackingClosed();
            }
        };

        gui.setTitle(minion.getName());

        gui.setSlot(1, new GuiElementBuilder()
                .setItem(Items.COMMAND_BLOCK)
                .setName(Component.translatable("minions.gui.main.instructions"))
                .setCallback(() -> {
                    InstructionGui.openInstructionMainMenu(this, GuiContext.Minion.create(GuiContext.create(viewer), minion));
                })
        );
        gui.setSlot(3, new GuiElementBuilder()
                .setItem(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
                .setName(Component.translatable("minions.gui.main.modules"))
                .setCallback(() -> {
                    ModuleInventory.openModuleInventory(viewer, minion);
                })
        );
        gui.setSlot(5, new GuiElementBuilder()
                .setItem(Items.CHEST)
                .setName(Component.translatable("minions.gui.main.inventory"))
                .setCallback(() -> new MinionInventoryGui(this))
        );
        gui.setSlot(7, new GuiElementBuilder()
                .setItem(Items.BARRIER)
                .setName(Component.translatable("minions.gui.main.pickup"))
                .setCallback(() -> minion.kill(minion.level()))
        );
        gui.open();
    }

    @Override
    protected void reopen() {
        gui.open();
    }

    @Override
    protected void closeBacking() {
        gui.close();
        minion.removeMinionListener(this);
    }

    @Override
    public void onMinionRemove(MinionFakePlayer minion) {
        close();
    }
}

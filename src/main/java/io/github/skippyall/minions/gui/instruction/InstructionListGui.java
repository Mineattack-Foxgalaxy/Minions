package io.github.skippyall.minions.gui.instruction;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.gui.minion.GuiContext;
import io.github.skippyall.minions.minion.MinionListener;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.registration.MinionRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;

/*public class InstructionListGui extends MinionsGui implements MinionListener {
    private final GuiContext.Minion context;
    private final MinionFakePlayer minion;
    private SimpleGui gui;

    public InstructionListGui(MinionsGui parent, GuiContext.Minion context) {
        super(parent);
        this.context = context;
        this.minion = context.getMinion();
        open();
    }

    @Override
    public void onInstructionsUpdate(MinionFakePlayer minion) {
        resetInstructionList();
    }

    @Override
    protected void open() {
        minion.addMinionListener(this);
        gui = new SimpleGui(MenuType.GENERIC_9x4, viewer, false) {
            @Override
            public void onPlayerClose(boolean success) {
                onBackingClosed();
            }
        };
        gui.setTitle(Component.translatable("minions.gui.instruction.title"));

        gui.setSlot(8, backButton());
        resetInstructionList();
        gui.open();
    }

    @Override
    protected void closeBacking() {
        minion.removeMinionListener(this);
        gui.close();
    }

    private void resetInstructionList() {
        int i = 9;
        for (String instructionName : minion.getRuntime().getInstructionNames()) {
            ConfiguredInstruction<MinionRuntime> instruction = minion.getRuntime().getInstruction(instructionName);
            gui.setSlot(i, new GuiElementBuilder(GuiDisplay.getGuiDisplayFor(MinionRegistries.INSTRUCTION_TYPES, instruction.getInstruction(), viewer.registryAccess()).createItemStack())
                    .setName(Component.literal(instructionName))
                    .setCallback(() -> new ConfigureInstructionGui(this, GuiContext.Instruction.create(context, instruction, instructionName)))
            );
            i++;
        }
    }
}*/

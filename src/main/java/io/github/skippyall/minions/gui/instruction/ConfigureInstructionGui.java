package io.github.skippyall.minions.gui.instruction;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.clipboard.ClipboardItem;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.gui.input.ChoiceInput;
import io.github.skippyall.minions.gui.minion.GuiContext;
import io.github.skippyall.minions.minion.MinionListener;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.program.instruction.ConfiguredInstructionListener;
import io.github.skippyall.minions.program.supplier.Parameter;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class ConfigureInstructionGui extends MinionsGui implements ConfiguredInstructionListener, MinionListener {
    private String name;
    private final ConfiguredInstruction<MinionRuntime> instruction;
    private final MinionFakePlayer minion;

    private final GuiContext.Instruction context;
    
    private SimpleGui gui;
    
    public ConfigureInstructionGui(MinionsGui parent, GuiContext.Instruction context) {
        super(parent);
        this.name = context.getName();
        this.instruction = context.getInstruction();
        this.minion = context.getMinion();
        this.context = context;
        minion.addMinionListener(this);
        instruction.addListener(this);
        open();
    }

    @Override
    protected void open() {
        gui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, viewer, false) {
            @Override
            public void onClose() {
                onBackingClosed();
            }
        };
        
        gui.setTitle(Text.literal(name));

        gui.setSlot(7, new GuiElementBuilder(Items.ANVIL)
                .setName(Text.translatable("minions.gui.instruction.configure.rename"))
                .setCallback(() -> InstructionGui.inputInstructionName(this, context, name).thenAccept(newName -> {
                    minion.getInstructionManager().setInstructionName(name, newName);
                    reopen();
                }))
        );

        gui.setSlot(8, new GuiElementBuilder(Items.LAVA_BUCKET)
                .setName(Text.translatable("minions.gui.instruction.configure.delete"))
                .setCallback(() -> ChoiceInput.confirm(this, Text.translatable("minions.gui.instruction.configure.delete.confirm", name))
                        .thenAccept(v -> {
                            minion.getInstructionManager().removeInstruction(name);
                            close();
                        }))
        );

        updateSuppliers();

        gui.setSlot(13, InstructionGui.createInstructionElement(instruction.getInstruction(), viewer.getRegistryManager()));

        gui.setSlot(25, new GuiElementBuilder(Items.FEATHER)
                .setName(Text.translatable("minions.gui.instruction.configure.copy"))
                .addLoreLine(Text.translatable("minions.gui.instruction.configure.copy.description"))
                .setCallback(() -> {
                    viewer.getInventory().offer(ClipboardItem.createInstructionReference(minion, name), true);
                    viewer.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(), SoundCategory.BLOCKS, 1, 1);
                })
        );

        updateRunSlot();
        gui.open();
    }

    @Override
    protected void closeBacking() {
        gui.close();
        minion.removeMinionListener(this);
        instruction.removeListener(this);
    }

    @Override
    public void onInstructionRename(MinionFakePlayer minion, ConfiguredInstruction<?> instruction, String oldName, String newName) {
        gui.setTitle(Text.literal(newName));
        name = newName;
        context.setName(newName);
    }

    @Override
    public void onRun(ConfiguredInstruction<?> instruction) {
        updateRunSlot();
    }

    @Override
    public void onStop(ConfiguredInstruction<?> instruction) {
        updateRunSlot();
    }

    @Override
    public void onSupplierChange(ConfiguredInstruction<?> instruction, Parameter<?> parameter) {
        updateSuppliers();
    }

    private void updateRunSlot() {
        if(!instruction.isRunning()) {
            gui.setSlot(26, new GuiElementBuilder(Items.ARROW)
                    .setName(Text.translatable("minions.gui.instruction.run"))
                    .setCallback(() -> instruction.run(minion.getInstructionManager()))
            );
        } else {
            gui.setSlot(26, new GuiElementBuilder(Items.BARRIER)
                    .setName(Text.translatable("minions.gui.instruction.stop"))
                    .setCallback(() -> instruction.stop(minion.getInstructionManager()))
            );
        }
    }

    private void updateSuppliers() {
        int slot = 12;
        for(Parameter<?> parameter : instruction.getInstruction().getParameters().reversed()) {
            gui.setSlot(slot, InstructionGui.createParameterElement(parameter, instruction.getArguments().getArgument(parameter), viewer.getRegistryManager())
                    .setCallback(() -> new ArgumentGui(this, GuiContext.ValueSupplier.create(context, parameter)))
            );
            slot--;
        }
    }

}

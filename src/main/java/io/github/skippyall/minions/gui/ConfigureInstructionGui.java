package io.github.skippyall.minions.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import io.github.skippyall.minions.gui.input.ChoiceInput;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.program.supplier.Parameter;
import io.github.skippyall.minions.reference.ReferenceItem;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

public class ConfigureInstructionGui extends InstructionBoundSimpleGui {
    private String name;

    private ConfigureInstructionGui(ScreenHandlerType<?> type, ServerPlayerEntity player, MinionFakePlayer minion, ConfiguredInstruction<MinionRuntime> configuredInstruction, String name) {
        super(type, player, minion, configuredInstruction);
        this.name = name;
        init();
    }

    public static void configureInstructionMenu(String name, ConfiguredInstruction<MinionRuntime> instruction, MinionFakePlayer minion, ServerPlayerEntity player) {
        if(!InstructionGui.checkInstructionExists(name, instruction, minion, player)) {
            return;
        }

        ConfigureInstructionGui gui = new ConfigureInstructionGui(ScreenHandlerType.GENERIC_9X3, player, minion, instruction, name);

        gui.open();
    }
    
    private void init() {
        setTitle(Text.literal(name));

        setSlot(7, new GuiElementBuilder(Items.ANVIL)
                .setName(Text.translatable("minions.gui.instruction.configure.rename"))
                .setCallback(() -> InstructionGui.inputInstructionName(minion, player, name).thenAccept(newName -> {
                    minion.getInstructionManager().setInstructionName(name, newName);
                    configureInstructionMenu(newName, instruction, minion, player);
                }))
        );

        setSlot(8, new GuiElementBuilder(Items.LAVA_BUCKET)
                .setName(Text.translatable("minions.gui.instruction.configure.delete"))
                .setCallback(() -> ChoiceInput.confirm(player, Text.translatable("minions.gui.instruction.configure.delete.confirm", name))
                        .thenAccept(v -> {
                            minion.getInstructionManager().removeInstruction(name);
                            InstructionGui.instructionList(minion, player);
                        }))
        );

        updateSuppliers();

        setSlot(13, InstructionGui.createInstructionElement(instruction.getInstruction(), player.getRegistryManager()));

        setSlot(25, new GuiElementBuilder(Items.FEATHER)
                .setName(Text.translatable("minions.gui.instruction.configure.copy"))
                .addLoreLine(Text.translatable("minions.gui.instruction.configure.copy.description"))
                .setCallback(() -> {
                    player.getInventory().offer(ReferenceItem.createInstructionReference(minion, name), true);
                    player.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(), SoundCategory.BLOCKS, 1, 1);
                })
        );

        updateRunSlot();
    }

    @Override
    public void onInstructionRename(MinionFakePlayer minion, ConfiguredInstruction<?> instruction, String newName) {
        this.setTitle(Text.literal(newName));
        name = newName;
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
            setSlot(26, new GuiElementBuilder(Items.ARROW)
                    .setName(Text.translatable("minions.gui.instruction.run"))
                    .setCallback(() -> instruction.run(minion.getInstructionManager()))
            );
        } else {
            setSlot(26, new GuiElementBuilder(Items.BARRIER)
                    .setName(Text.translatable("minions.gui.instruction.stop"))
                    .setCallback(() -> instruction.stop(minion.getInstructionManager()))
            );
        }
    }

    private void updateSuppliers() {
        int slot = 11;
        for(Parameter<?> parameter : instruction.getInstruction().getParameters().reversed()) {
            setSlot(slot, InstructionGui.createParameterElement(parameter, player.getRegistryManager())
                    .setCallback(() -> InstructionGui.configureArgumentMenu(name, instruction, parameter, minion, player))
            );
            slot--;
        }
    }

}

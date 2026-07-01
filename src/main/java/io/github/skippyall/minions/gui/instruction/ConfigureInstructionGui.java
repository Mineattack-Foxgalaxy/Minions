package io.github.skippyall.minions.gui.instruction;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.clipboard.ClipboardItem;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.gui.input.BooleanInput;
import io.github.skippyall.minions.gui.minion.GuiContext;
import io.github.skippyall.minions.minion.MinionListener;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.program.instruction.ConfiguredInstructionListener;
import io.github.skippyall.minions.program.supplier.Parameter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class ConfigureInstructionGui extends MinionsGui implements ConfiguredInstructionListener, MinionListener {
    private final ConfiguredInstruction instruction;
    private final @Nullable Runnable onDelete;
    
    private SimpleGui gui;
    
    public ConfigureInstructionGui(ServerPlayer player, @Nullable MinionsGui parent, ConfiguredInstruction instruction, @Nullable Runnable onDelete) {
        super(player, parent);
        this.instruction = instruction;
        this.onDelete = onDelete;
        instruction.addListener(this);
        open();
    }

    @Override
    protected void open() {
        gui = new SimpleGui(MenuType.GENERIC_9x3, viewer, false) {
            @Override
            public void onPlayerClose(boolean success) {
                onBackingClosed();
            }
        };

        /*gui.setSlot(6, new GuiElementBuilder(Items.ANVIL)
                .setName(Component.translatable("minions.gui.instruction.configure.rename"))
                .setCallback(() -> InstructionGui.inputInstructionName(this, context, name).thenAccept(newName -> {
                    if(newName != null) {
                        minion.getRuntime().setInstructionName(name, newName);
                    }
                    reopen();
                }))
        );*/

        if(onDelete != null) {
            gui.setSlot(7, new GuiElementBuilder(Items.LAVA_BUCKET)
                    .setName(Component.translatable("minions.gui.instruction.configure.delete"))
                    .setCallback(() -> BooleanInput.confirm(this, Component.translatable("minions.gui.instruction.configure.delete.confirm"))
                            .thenAccept((confirmed) -> {
                                if (confirmed) {
                                    onDelete.run();
                                    goBack();
                                }
                            })
                    )
            );
        }

        gui.setSlot(8, backButton());

        updateSuppliers();

        gui.setSlot(13, InstructionGui.createInstructionElement(instruction.getInstruction(), viewer.registryAccess()));

        /*gui.setSlot(25, new GuiElementBuilder(Items.FEATHER)
                .setName(Component.translatable("minions.gui.instruction.configure.copy"))
                .addLoreLine(Component.translatable("minions.gui.instruction.configure.copy.description"))
                .setCallback(() -> {
                    viewer.getInventory().placeItemBackInInventory(ClipboardItem.createInstructionReference(minion, name), true);
                    viewer.connection.send(new ClientboundSoundEntityPacket(SoundEvents.NOTE_BLOCK_CHIME, SoundSource.BLOCKS, viewer, 1, 1, 0));
                })
        );*/

        updateLastError();
        //updateRunSlot();
        gui.open();
    }

    @Override
    protected void closeBacking() {
        gui.close();
        instruction.removeListener(this);
    }

    /*@Override
    public void onRun(ConfiguredInstruction<?> instruction) {
        updateRunSlot();
        updateLastError();
    }*/

    @Override
    public void onStop(ConfiguredInstruction instruction) {
        //updateRunSlot();
    }

    @Override
    public void onSupplierChange(ConfiguredInstruction instruction, Parameter<?> parameter) {
        updateSuppliers();
    }

    /*private void updateRunSlot() {
        List<Component> errors = instruction.preCheck();
        if(errors.isEmpty()) {
            if (!instruction.isRunning()) {
                gui.setSlot(26, new GuiElementBuilder(Items.ARROW)
                        .setName(Component.translatable("minions.gui.instruction.run"))
                        .setCallback(() -> instruction.run(minion.getRuntime()))
                );
            } else {
                gui.setSlot(26, new GuiElementBuilder(Items.BARRIER)
                        .setName(Component.translatable("minions.gui.instruction.stop"))
                        .setCallback(() -> instruction.stop(minion.getRuntime()))
                );
            }
        } else {
            GuiElementBuilder builder = new GuiElementBuilder(Items.RED_WOOL)
                    .setName(Component.translatable("minions.gui.instruction.errors"));
            for(Component error : errors) {
                builder.addLoreLine(error);
            }
            gui.setSlot(26, builder);
        }
    }*/

    private void updateLastError() {
        /*List<Component> errors = instruction.getLastErrors();
        if(!errors.isEmpty()) {
            GuiElementBuilder builder = new GuiElementBuilder(Items.RED_WOOL)
                    .setName(Component.translatable("minions.gui.instruction.last_errors"));
            for(Component error : errors) {
                builder.addLoreLine(error);
            }
            gui.setSlot(17, builder);
        }*/
    }

    private void updateSuppliers() {
        int slot = 12;
        for(Parameter<?> parameter : instruction.getInstruction().getParameters().reversed()) {
            gui.setSlot(slot, InstructionGui.createParameterElement(parameter, instruction.getArguments().getArgument(parameter), viewer.registryAccess())
                    .setCallback(() -> new ArgumentGui(this, instruction, parameter))
            );
            slot--;
        }
    }

}

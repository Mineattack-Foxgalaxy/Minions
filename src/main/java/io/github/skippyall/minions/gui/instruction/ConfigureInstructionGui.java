package io.github.skippyall.minions.gui.instruction;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.gui.input.BooleanInput;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.handler.Parameter;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.program.instruction.ConfiguredInstructionListener;
import io.github.skippyall.minions.registration.ResolutionContext;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class ConfigureInstructionGui extends MinionsGui implements ConfiguredInstructionListener {
    private final ConfiguredInstruction instruction;
    private final @Nullable Runnable onDelete;
    private final Context resolutionContext;
    
    private SimpleGui gui;
    
    public ConfigureInstructionGui(ServerPlayer player, @Nullable MinionsGui parent, ConfiguredInstruction instruction, @Nullable Runnable onDelete, Context resolutionContext) {
        super(player, parent);
        this.instruction = instruction;
        this.onDelete = onDelete;
        this.resolutionContext = resolutionContext;
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

        if(onDelete != null) {
            gui.setSlot(7, new GuiElementBuilder(Items.LAVA_BUCKET)
                    .setName(Component.translatable("minions.gui.instruction.configure.delete"))
                    .setCallback(() -> BooleanInput.confirm(this, Component.translatable("minions.gui.instruction.configure.delete.confirm"))
                            .thenAccept((confirmed) -> {
                                if (confirmed) {
                                    onDelete.run();
                                    instruction.onInstructionRemove();
                                    goBack();
                                }
                            })
                    )
            );
        }

        gui.setSlot(8, backButton());

        updateSuppliers();

        gui.setSlot(13, InstructionGui.createInstructionElement(instruction.getInstruction(), viewer.registryAccess()));

        updateLastError();
        updateCompileErrorSlot();
        gui.open();
    }

    @Override
    protected void reopen() {
        gui.open();
    }

    @Override
    protected void closeBacking() {
        gui.close();
        instruction.removeListener(this);
    }

    @Override
    public void onSupplierChange(ConfiguredInstruction instruction, Parameter<?> parameter) {
        updateSuppliers();
    }

    @Override
    public void onConsumerChange(ConfiguredInstruction instruction, Parameter<?> parameter) {
        updateConsumers();
    }

    @Override
    public void onInstructionRemove(ConfiguredInstruction instruction) {
        goBack();
    }

    private void updateCompileErrorSlot() {
        List<Component> errors = instruction.preCheck();
        if(!errors.isEmpty()) {
            GuiElementBuilder builder = new GuiElementBuilder(Items.RED_WOOL)
                    .setName(Component.translatable("minions.gui.instruction.errors"));
            for(Component error : errors) {
                builder.addLoreLine(error);
            }
            gui.setSlot(26, builder);
        } else {
            gui.setSlot(26, ItemStack.EMPTY);
        }
    }

    private void updateLastError() {
        List<Component> errors = instruction.getLastErrors();
        if(!errors.isEmpty()) {
            GuiElementBuilder builder = new GuiElementBuilder(Items.RED_WOOL)
                    .setName(Component.translatable("minions.gui.instruction.last_errors"));
            for(Component error : errors) {
                builder.addLoreLine(error);
            }
            gui.setSlot(17, builder);
        }
    }

    private void updateSuppliers() {
        int slot = 12;
        for(Parameter<?> parameter : instruction.getInstruction().getParameters().reversed()) {
            gui.setSlot(slot, InstructionGui.createParameterElement(parameter, instruction.getArguments().getHandler(parameter), viewer.registryAccess())
                    .setCallback(() -> {
                        Context newContext = resolutionContext.toBuilder()
                                .put(ResolutionContext.PARAMETER_NAME, parameter.name())
                                .build();

                        new ValueSupplierGui(this, instruction, parameter, newContext);
                    })
            );
            slot--;
        }
    }

    private void updateConsumers() {
        int slot = 14;
        for(Parameter<?> parameter : instruction.getInstruction().getReturnParameters()) {
            gui.setSlot(slot, InstructionGui.createParameterElement(parameter, instruction.getArguments().getHandler(parameter), viewer.registryAccess())
                    .setCallback(() -> {
                        Context newContext = resolutionContext.toBuilder()
                                .put(ResolutionContext.PARAMETER_NAME, parameter.name())
                                .build();
                        new ValueConsumerGui(this, instruction, parameter, newContext);
                    })
            );
            slot++;
        }
    }
}

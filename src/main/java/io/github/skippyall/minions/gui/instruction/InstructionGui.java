package io.github.skippyall.minions.gui.instruction;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.gui.input.Result;
import io.github.skippyall.minions.gui.input.TextInput;
import io.github.skippyall.minions.gui.minion.GuiContext;
import io.github.skippyall.minions.gui.minion.SimpleMinionsGui;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.module.MinionModule;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.program.instruction.InstructionType;
import io.github.skippyall.minions.program.supplier.Parameter;
import io.github.skippyall.minions.program.supplier.ValueSupplier;
import io.github.skippyall.minions.registration.MinionComponentTypes;
import io.github.skippyall.minions.registration.MinionRegistries;
import io.github.skippyall.minions.util.TranslationUtil;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

public class InstructionGui {
    public static MinionsGui openInstructionMainMenu(MinionsGui parent, GuiContext.Minion context) {
        return new SimpleMinionsGui(parent, (onClose, me) -> {
            ServerPlayer player = parent.getViewer();

            SimpleGui gui = new SimpleGui(MenuType.GENERIC_3x3, player, false) {
                @Override
                public void onPlayerClose(boolean success) {
                    onClose.run();
                }
            };
            gui.setTitle(Component.translatable("minions.gui.instruction.title"));

            gui.setSlot(3, new GuiElementBuilder()
                    .setItem(Items.BOOK)
                    .setName(Component.translatable("minions.gui.instruction.list"))
                    .setCallback(() -> new InstructionListGui(me, context))
            );
            gui.setSlot(5, new GuiElementBuilder()
                    .setItem(Items.WRITABLE_BOOK)
                    .setName(Component.translatable("minions.gui.instruction.create"))
                    .setCallback(() -> createNewInstruction(me, context))
            );

            gui.open();
            return gui;
        });
    }

    public static void createNewInstruction(MinionsGui parent, GuiContext.Minion context) {
        MinionFakePlayer minion = context.getMinion();
        ServerPlayer viewer = parent.getViewer();
        selectInstructionModuleMenu(parent, context).thenAccept(instructionType ->
                inputInstructionName(parent, context, "Instruction").thenAccept(name -> {
                    if (!minion.isRemoved() && !minion.hasDisconnected()) {
                        ConfiguredInstruction<MinionRuntime> configuredInstruction = minion.getInstructionManager().createInstruction(name, instructionType);
                        new ConfigureInstructionGui(parent, GuiContext.Instruction.create(context, configuredInstruction, name));
                    }
                })
        );
    }

    public static CompletableFuture<String> inputInstructionName(MinionsGui parent, GuiContext.Minion context, String defaultValue) {
        return TextInput.inputSync(parent, Component.translatable("minions.gui.instruction.enter_name"), defaultValue, name -> {
            if (context.getMinion().getInstructionManager().hasInstruction(name)) {
                return new Result.Error<>(Component.translatable("minions.gui.instruction.name_already_used"));
            }
            return new Result.Success<>(name);
        });
    }

    public static boolean checkInstructionExists(String name, ConfiguredInstruction<?> instruction, MinionFakePlayer minion, ServerPlayer player) {
        boolean stillExists = !minion.isRemoved() && !minion.hasDisconnected() && minion.getInstructionManager().getInstruction(name) == instruction;
        if (!stillExists) {
            player.closeContainer();
            player.sendSystemMessage(Component.translatable("minions.gui.instruction.removed"));
        }
        return stillExists;
    }

    public static CompletableFuture<InstructionType<MinionRuntime>> selectInstructionModuleMenu(MinionsGui parent, GuiContext.Minion context) {
        MinionFakePlayer minion = context.getMinion();
        ServerPlayer viewer = parent.getViewer();

        if (minion.getModuleInventory().getModules().isEmpty()) {
            viewer.sendSystemMessage(Component.translatable("minions.gui.instruction.no_modules"));
            return CompletableFuture.failedFuture(new NoSuchElementException("No modules"));
        }

        CompletableFuture<InstructionType<MinionRuntime>> future = new CompletableFuture<>();

        new SimpleMinionsGui(parent, (closeHandler, me) -> {
            SimpleGui gui = new SimpleGui(MenuType.GENERIC_9x3, viewer, false) {
                @Override
                public void onPlayerClose(boolean success) {
                    if (!future.isDone()) {
                        future.cancel(false);
                    }
                    closeHandler.run();
                }
            };
            gui.setTitle(Component.translatable("minions.gui.instruction.select_instruction"));

            for (int i = 0; i < minion.getModuleInventory().getContainerSize(); i++) {
                ItemStack moduleItem = minion.getModuleInventory().getItem(i);
                MinionModule module = moduleItem.get(MinionComponentTypes.MODULE);
                if (module != null && !module.instructions().isEmpty()) {
                    gui.addSlot(new GuiElementBuilder(moduleItem)
                            .setCallback(() -> selectInstructionMenu(parent, context, module)
                                    .thenApply(future::complete)
                            )
                    );
                }
            }

            gui.open();
            return gui;
        });
        return future;
    }

    public static CompletableFuture<InstructionType<MinionRuntime>> selectInstructionMenu(MinionsGui parent, GuiContext.Minion context, MinionModule module) {
        CompletableFuture<InstructionType<MinionRuntime>> future = new CompletableFuture<>();

        new SimpleMinionsGui(parent, (closeHandler, me) -> {
            SimpleGui gui = new SimpleGui(MenuType.GENERIC_9x3, parent.getViewer(), false) {
                @Override
                public void onPlayerClose(boolean success) {
                    if (!future.isDone()) {
                        future.cancel(false);
                    }
                    closeHandler.run();
                }
            };
            gui.setTitle(Component.translatable("minions.gui.instruction.select_instruction"));

            for (InstructionType<MinionRuntime> instructionType : module.instructions()) {
                gui.addSlot(createInstructionElement(instructionType, parent.getViewer().registryAccess())
                        .setCallback(() -> future.complete(instructionType))
                );
            }

            gui.open();
            return gui;
        });
        return future;
    }

    public static GuiElementBuilder createInstructionElement(InstructionType<MinionRuntime> instructionType, RegistryAccess manager) {
        GuiElementBuilder instructionBuilder;
        if (instructionType != null) {
            instructionBuilder = new GuiElementBuilder(GuiDisplay.getDisplayStackWithName(MinionRegistries.INSTRUCTION_TYPES, instructionType, manager));
        } else {
            instructionBuilder = new GuiElementBuilder(Items.RED_WOOL)
                    .setName(Component.translatable("minions.gui.instruction.no_instruction_set"));
        }
        return instructionBuilder;
    }

    public static GuiElementBuilder createParameterElement(Parameter<?> parameter, @Nullable ValueSupplier<?,?> valueSupplier, RegistryAccess manager) {
        GuiElementBuilder builder = new GuiElementBuilder(GuiDisplay.getDisplayStack(MinionRegistries.VALUE_TYPES, parameter.type(), manager))
                .setName(Component.translatable("minions.gui.instruction.parameter", parameter.name(), Component.translatable(TranslationUtil.getTranslationKey(parameter.type(), MinionRegistries.VALUE_TYPES))));
        if(valueSupplier != null) {
                builder.addLoreLine(Component.translatable("minions.gui.instruction.argument", valueSupplier.getDisplayText()));
        }
        return builder;
    }
}
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
import org.jspecify.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

public class InstructionGui {
    /*public static void openInstructionMainMenu(MinionsGui parent, GuiContext.Minion context) {
        new SimpleMinionsGui(parent, (onClose, me) -> {
            ServerPlayer player = parent.viewer;

            SimpleGui gui = new SimpleGui(MenuType.GENERIC_3x3, player, false) {
                @Override
                public void onPlayerClose(boolean success) {
                    onClose.run();
                }
            };
            gui.setTitle(Component.translatable("minions.gui.instruction.title"));

            gui.setSlot(2, me.backButton());
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
        ServerPlayer viewer = parent.viewer;
        selectInstructionModuleMenu(parent, context).thenAccept(instructionType ->
                inputInstructionName(parent, context, "Instruction").thenAccept(name -> {
                    if (!minion.isRemoved() && !minion.hasDisconnected() && name != null) {
                        ConfiguredInstruction<MinionRuntime> configuredInstruction = minion.getRuntime().createInstruction(name, instructionType);
                        new ConfigureInstructionGui(parent, GuiContext.Instruction.create(context, configuredInstruction, name));
                    }
                })
        );
    }

    public static CompletableFuture<@Nullable String> inputInstructionName(MinionsGui parent, GuiContext.Minion context, String defaultValue) {
        return TextInput.input(parent, Component.translatable("minions.gui.instruction.enter_name"), defaultValue, (name, _) -> {
            if (context.getMinion().getRuntime().hasInstruction(name)) {
                return new Result.Error<>(Component.translatable("minions.gui.instruction.name_already_used"));
            }
            return new Result.Success<>(name);
        });
    }

    public static boolean checkInstructionExists(String name, ConfiguredInstruction<?> instruction, MinionFakePlayer minion, ServerPlayer player) {
        boolean stillExists = !minion.isRemoved() && !minion.hasDisconnected() && minion.getRuntime().getInstruction(name) == instruction;
        if (!stillExists) {
            player.closeContainer();
            player.sendSystemMessage(Component.translatable("minions.gui.instruction.removed"));
        }
        return stillExists;
    }*/

    public static CompletableFuture<InstructionType<MinionRuntime>> selectInstructionModuleMenu(ServerPlayer viewer, @Nullable MinionsGui parent, MinionFakePlayer minion) {
        if (minion.getModuleInventory().getModules().isEmpty()) {
            viewer.sendSystemMessage(Component.translatable("minions.gui.instruction.no_modules"));
            return CompletableFuture.failedFuture(new NoSuchElementException("No modules"));
        }

        CompletableFuture<InstructionType<MinionRuntime>> future = new CompletableFuture<>();

        new SimpleMinionsGui(viewer, parent, (closeHandler, me) -> {
            SimpleGui gui = new SimpleGui(MenuType.GENERIC_9x4, viewer, false) {
                @Override
                public void onPlayerClose(boolean success) {
                    if (!future.isDone()) {
                        future.cancel(false);
                    }
                    closeHandler.run();
                }
            };
            gui.setTitle(Component.translatable("minions.gui.instruction.select_instruction"));

            gui.setSlot(8, me.backButton());

            for (int i = 0; i < minion.getModuleInventory().getContainerSize(); i++) {
                ItemStack moduleItem = minion.getModuleInventory().getItem(i);
                MinionModule module = moduleItem.get(MinionComponentTypes.MODULE);
                if (module != null && !module.instructions().isEmpty()) {
                    gui.setSlot(i + 9, new GuiElementBuilder(moduleItem)
                            .setCallback(() -> selectInstructionMenu(viewer, parent, module)
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

    public static CompletableFuture<InstructionType<MinionRuntime>> selectInstructionMenu(ServerPlayer viewer, @Nullable MinionsGui parent, MinionModule module) {
        CompletableFuture<InstructionType<MinionRuntime>> future = new CompletableFuture<>();

        new SimpleMinionsGui(viewer, parent, (closeHandler, me) -> {
            SimpleGui gui = new SimpleGui(MenuType.GENERIC_9x4, viewer, false) {
                @Override
                public void onPlayerClose(boolean success) {
                    if (!future.isDone()) {
                        future.cancel(false);
                    }
                    closeHandler.run();
                }
            };
            gui.setTitle(Component.translatable("minions.gui.instruction.select_instruction"));

            gui.setSlot(8, me.backButton());
            int slot = 9;
            for (InstructionType<MinionRuntime> instructionType : module.instructions()) {
                gui.setSlot(slot, createInstructionElement(instructionType, viewer.registryAccess())
                        .setCallback(() -> {
                            future.complete(instructionType);
                            me.goBack();
                        })
                );
                slot++;
            }

            gui.open();
            return gui;
        });
        return future;
    }

    public static GuiElementBuilder createInstructionElement(@Nullable InstructionType<?> instructionType, RegistryAccess manager) {
        GuiElementBuilder instructionBuilder;
        if (instructionType != null) {
            instructionBuilder = new GuiElementBuilder(GuiDisplay.getDisplayStackWithName(MinionRegistries.INSTRUCTION_TYPES, instructionType, manager));
        } else {
            instructionBuilder = new GuiElementBuilder(Items.RED_WOOL)
                    .setName(Component.translatable("minions.gui.instruction.no_instruction_set"));
        }
        return instructionBuilder;
    }

    public static GuiElementBuilder createParameterElement(Parameter<?> parameter, @Nullable ValueSupplier<?> valueSupplier, RegistryAccess manager) {
        GuiElementBuilder builder = new GuiElementBuilder(GuiDisplay.getDisplayStack(MinionRegistries.VALUE_TYPES, parameter.type(), manager))
                .setName(Component.translatable("minions.gui.instruction.parameter", parameter.name(), Component.translatable(TranslationUtil.getTranslationKey(parameter.type(), MinionRegistries.VALUE_TYPES))));
        if(valueSupplier != null) {
                builder.addLoreLine(Component.translatable("minions.gui.instruction.argument", valueSupplier.getDisplayText()));
        }
        return builder;
    }
}
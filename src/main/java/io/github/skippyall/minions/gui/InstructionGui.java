package io.github.skippyall.minions.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.MinionRegistries;
import io.github.skippyall.minions.input.TextInput;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.module.MinionModule;
import io.github.skippyall.minions.program.argument.Argument;
import io.github.skippyall.minions.program.argument.GenericArgumentType;
import io.github.skippyall.minions.program.argument.SpecificArgumentType;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.program.instruction.InstructionType;
import io.github.skippyall.minions.program.argument.Parameter;
import io.github.skippyall.minions.util.TranslationUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

public class InstructionGui {
    public static void openInstructionMainMenu(MinionFakePlayer minion, ServerPlayerEntity player) {
        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_3X3, player, false);
        gui.setSlot(3, new GuiElementBuilder()
                .setItem(Items.BOOK)
                .setName(Text.literal("Instruction List"))
                .setCallback(() -> instructionList(minion, player))
        );
        gui.setSlot(5, new GuiElementBuilder()
                .setItem(Items.WRITABLE_BOOK)
                .setName(Text.literal("New Instruction"))
                .setCallback(() -> createNewInstruction(minion, player))
        );

        gui.open();
    }

    public static void instructionList(MinionFakePlayer minion, ServerPlayerEntity player) {
        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false);
        for(String instructionName : minion.getInstructionManager().getInstructionNames()) {
            ConfiguredInstruction<?> instruction = minion.getInstructionManager().getInstruction(instructionName);
            gui.addSlot(instruction.getInstruction().getDisplay().createElement()
                    .setName(Text.literal(instructionName))
                    .setLore(List.of())
                    .setCallback(() -> configureInstructionMenu(instructionName, instruction, minion, player))
            );
        }
        gui.open();
    }

    public static void createNewInstruction(MinionFakePlayer minion, ServerPlayerEntity player) {
        selectInstructionModuleMenu(minion, player).thenAccept(instructionType ->
                TextInput.inputString(player, Text.translatable("minions.gui.instruction.enter_name"), "Instruction").thenAccept(name -> {
                    ConfiguredInstruction<?> configuredInstruction = minion.getInstructionManager().createInstruction(name, instructionType);
                    configureInstructionMenu(name, configuredInstruction, minion, player);
                })
        );
    }

    public static boolean checkInstructionExists(String name, ConfiguredInstruction<?> instruction, MinionFakePlayer minion, ServerPlayerEntity player) {
        boolean stillExists = minion.getInstructionManager().getInstruction(name) == instruction;
        if(!stillExists) {
            player.closeHandledScreen();
            player.sendMessage(Text.translatable("minions.gui.instruction.removed"));
        }
        return stillExists;
    }

    public static void configureInstructionMenu(String name, ConfiguredInstruction<?> instruction, MinionFakePlayer minion, ServerPlayerEntity player) {
        if(!checkInstructionExists(name, instruction, minion, player)) {
            return;
        }

        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false);

        gui.setSlot(12, createInstructionElement(instruction.getInstruction()));

        int slot = 13;
        for(Parameter<?> parameter : instruction.getInstruction().getParameters()) {
            gui.setSlot(slot, createArgumentElement(instruction.getArguments().getArgument(parameter))
                    .setCallback(() -> configureArgumentMenu(name, instruction, parameter, minion, player))
            );
            slot++;
        }
        updateRunSlot(instruction, minion, gui);

        gui.open();
    }

    private static void updateRunSlot(ConfiguredInstruction<?> instruction, MinionFakePlayer minion, SimpleGui gui) {
        if(!instruction.isRunning()) {
            gui.setSlot(26, new GuiElementBuilder(Items.ARROW)
                    .setName(Text.literal("Run"))
                    .setCallback(() -> {
                        instruction.run(minion);
                        updateRunSlot(instruction, minion, gui);
                    })
            );
        } else {
            gui.setSlot(26, new GuiElementBuilder(Items.BARRIER)
                    .setName(Text.literal("Stop"))
                    .setCallback(() -> {
                        instruction.stop(minion);
                        updateRunSlot(instruction, minion, gui);
                    })
            );
        }
    }

    public static <T, A extends Argument<T, ? extends SpecificArgumentType<T, A>>> void configureArgumentMenu(String name, ConfiguredInstruction<?> instruction, Parameter<T> parameter, MinionFakePlayer minion, ServerPlayerEntity player) {
        if(!checkInstructionExists(name, instruction, minion, player)) {
            return;
        }

        A argument = instruction.getArguments().getArgument(parameter);

        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_3X3, player, false);
        gui.setSlot(3, new GuiElementBuilder(Items.STICK)
                .setName(Text.literal("Type: " + (argument == null ? "Unset" : MinionRegistries.GENERIC_ARGUMENT_TYPE_REGISTRY.getId(argument.getType().getGenericArgumentType()).getPath())))
                .setCallback(() -> selectArgumentType(player)
                        .thenApply(type -> type.<T>createTypeSpecific(parameter.type()).openArgumentDialog(player, null)
                                .thenAccept(newArgument -> {
                                    instruction.getArguments().setArgument(parameter, newArgument);
                                    configureArgumentMenu(name, instruction, parameter, minion, player);
                                })
                        )
                )
        );

        if(argument != null) {
            gui.setSlot(5, new GuiElementBuilder(Items.STRUCTURE_VOID)
                    .setName(Text.literal("Configure"))
                    .setCallback(() -> argument.getType().openArgumentDialog(player, argument)
                            .thenAccept(newArgument -> instruction.getArguments().setArgument(parameter, newArgument))
                    )
            );
        }
        gui.open();
    }

    public static CompletableFuture<GenericArgumentType> selectArgumentType(ServerPlayerEntity player) {
        CompletableFuture<GenericArgumentType> future = new CompletableFuture<>();
        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false);
        for(GenericArgumentType type : MinionRegistries.GENERIC_ARGUMENT_TYPE_REGISTRY) {
            gui.addSlot(new GuiElementBuilder()
                    .setName(Text.translatable(TranslationUtil.getTranslationKey(type, MinionRegistries.GENERIC_ARGUMENT_TYPE_REGISTRY)))
                    .setCallback(() -> future.complete(type))
            );
        }
        gui.open();
        return future;
    }

    public static CompletableFuture<InstructionType<?>> selectInstructionModuleMenu(MinionFakePlayer minion, ServerPlayerEntity player) {
        if(minion.getModuleInventory().getModules().isEmpty()) {
            player.sendMessage(Text.literal("This minion has no modules"));
            return CompletableFuture.failedFuture(new NoSuchElementException("No modules"));
        }

        CompletableFuture<InstructionType<?>> future = new CompletableFuture<>();

        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false) {
            @Override
            public void onClose() {
                if(!future.isDone()) {
                    future.cancel(false);
                }
            }
        };

        for(int i = 0; i < minion.getModuleInventory().size(); i++) {
            ItemStack module = minion.getModuleInventory().getStack(i);
            if(module.contains(MinionModule.COMPONENT_TYPE)) {
                gui.addSlot(new GuiElementBuilder(module)
                        .setCallback(() -> selectInstructionMenu(module.get(MinionModule.COMPONENT_TYPE), minion, player)
                                .thenApply(future::complete)
                        )
                );
            }
        }

        gui.open();
        return future;
    }

    public static CompletableFuture<InstructionType<?>> selectInstructionMenu(MinionModule module, MinionFakePlayer minion, ServerPlayerEntity player) {
        CompletableFuture<InstructionType<?>> future = new CompletableFuture<>();

        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false);
        for(InstructionType<?> instructionType : module.instructions()) {
            gui.addSlot(createInstructionElement(instructionType)
                    .setCallback(() -> future.complete(instructionType))
            );
        }

        gui.open();
        return future;
    }

    public static GuiElementBuilder createInstructionElement(InstructionType<?> instructionType) {
        GuiElementBuilder instructionBuilder;
        if(instructionType != null) {
            instructionBuilder = instructionType.getDisplay().createElement();
        } else {
            instructionBuilder = new GuiElementBuilder(Items.RED_WOOL)
                    .setName(Text.translatable("minions.gui.instruction.no_instruction_set"));
        }
        return instructionBuilder;
    }

    public static GuiElementBuilder createArgumentElement(Argument<?,?> argument) {
        GuiElementBuilder argumentBuilder;
        if(argument != null) {
            argumentBuilder = argument.getDisplay().createElement();
        } else {
            argumentBuilder = new GuiElementBuilder(Items.RED_WOOL)
                    .setName(Text.translatable("minions.gui.instruction.no_argument_set"));
        }
        return argumentBuilder;
    }
}

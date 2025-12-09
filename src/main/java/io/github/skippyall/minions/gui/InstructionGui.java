package io.github.skippyall.minions.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.MinionRegistries;
import io.github.skippyall.minions.PlayerClipboardAttachment;
import io.github.skippyall.minions.gui.input.ChoiceInput;
import io.github.skippyall.minions.gui.input.Result;
import io.github.skippyall.minions.gui.input.TextInput;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.module.MinionModule;
import io.github.skippyall.minions.program.supplier.ValueSupplier;
import io.github.skippyall.minions.program.supplier.ValueSupplierType;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.program.instruction.InstructionType;
import io.github.skippyall.minions.program.supplier.Parameter;
import io.github.skippyall.minions.util.TranslationUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

public class InstructionGui {
    public static void openInstructionMainMenu(MinionFakePlayer minion, ServerPlayerEntity player) {
        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_3X3, player, false);
        gui.setSlot(3, new GuiElementBuilder()
                .setItem(Items.BOOK)
                .setName(Text.translatable("minions.gui.instruction.list"))
                .setCallback(() -> instructionList(minion, player))
        );
        gui.setSlot(5, new GuiElementBuilder()
                .setItem(Items.WRITABLE_BOOK)
                .setName(Text.translatable("minions.gui.instruction.create"))
                .setCallback(() -> createNewInstruction(minion, player))
        );

        gui.open();
    }

    public static void instructionList(MinionFakePlayer minion, ServerPlayerEntity player) {
        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false);
        for(String instructionName : minion.getInstructionManager().getInstructionNames()) {
            ConfiguredInstruction<MinionRuntime> instruction = minion.getInstructionManager().getInstruction(instructionName);
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
                inputInstructionName(minion, player, "Instruction").thenAccept(name -> {
                    ConfiguredInstruction<MinionRuntime> configuredInstruction = minion.getInstructionManager().createInstruction(name, instructionType);
                    configureInstructionMenu(name, configuredInstruction, minion, player);
                })
        );
    }

    public static CompletableFuture<String> inputInstructionName(MinionFakePlayer minion, ServerPlayerEntity player, String defaultValue) {
        return TextInput.inputSync(player, Text.translatable("minions.gui.instruction.enter_name"), defaultValue, name -> {
            if(minion.getInstructionManager().hasInstruction(name)) {
                return new Result.Error<>(Text.translatable("minions.gui.instruction.name_already_used"));
            }
            return new Result.Success<>(name);
        });
    }

    public static boolean checkInstructionExists(String name, ConfiguredInstruction<?> instruction, MinionFakePlayer minion, ServerPlayerEntity player) {
        boolean stillExists = minion.getInstructionManager().getInstruction(name) == instruction;
        if(!stillExists) {
            player.closeHandledScreen();
            player.sendMessage(Text.translatable("minions.gui.instruction.removed"));
        }
        return stillExists;
    }

    public static void configureInstructionMenu(String name, ConfiguredInstruction<MinionRuntime> instruction, MinionFakePlayer minion, ServerPlayerEntity player) {
        if(!checkInstructionExists(name, instruction, minion, player)) {
            return;
        }

        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false);
        gui.setTitle(Text.literal(name));

        gui.setSlot(7, new GuiElementBuilder(Items.ANVIL)
                .setName(Text.translatable("minions.gui.instruction.configure.rename"))
                .setCallback(() -> inputInstructionName(minion, player, name).thenAccept(newName -> {
                    minion.getInstructionManager().setInstructionName(name, newName);
                    configureInstructionMenu(newName, instruction, minion, player);
                }))
        );

        gui.setSlot(8, new GuiElementBuilder(Items.LAVA_BUCKET)
                .setName(Text.translatable("minions.gui.instruction.configure.delete"))
                .setCallback(() -> ChoiceInput.confirm(player, Text.translatable("minions.gui.instruction.configure.delete.confirm", name))
                        .thenAccept(v -> {
                            minion.getInstructionManager().removeInstruction(name);
                            instructionList(minion, player);
                        }))
        );

        gui.setSlot(13, createInstructionElement(instruction.getInstruction()));

        int slot = 14;
        for(Parameter<?> parameter : instruction.getInstruction().getParameters()) {
            gui.setSlot(slot, createArgumentElement(instruction.getArguments().getArgument(parameter))
                    .setCallback(() -> configureArgumentMenu(name, instruction, parameter, minion, player))
            );
            slot++;
        }

        gui.setSlot(25, new GuiElementBuilder(Items.FEATHER)
                .setName(Text.translatable("minions.gui.instruction.configure.copy"))
                .addLoreLine(Text.translatable("minions.gui.instruction.configure.copy.description"))
                .setCallback(() -> {
                    player.setAttached(PlayerClipboardAttachment.TYPE, new PlayerClipboardAttachment(minion.getUuid(), name));
                    player.playSoundToPlayer(SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(), SoundCategory.BLOCKS, 1, 1);
                })
        );

        updateRunSlot(instruction, minion, gui);

        gui.open();
    }

    private static void updateRunSlot(ConfiguredInstruction<MinionRuntime> instruction, MinionFakePlayer minion, SimpleGui gui) {
        if(!instruction.isRunning()) {
            gui.setSlot(26, new GuiElementBuilder(Items.ARROW)
                    .setName(Text.translatable("minions.gui.instruction.run"))
                    .setCallback(() -> {
                        instruction.run(minion.getInstructionManager());
                        updateRunSlot(instruction, minion, gui);
                    })
            );
        } else {
            gui.setSlot(26, new GuiElementBuilder(Items.BARRIER)
                    .setName(Text.translatable("minions.gui.instruction.stop"))
                    .setCallback(() -> {
                        instruction.stop(minion.getInstructionManager());
                        updateRunSlot(instruction, minion, gui);
                    })
            );
        }
    }

    public static <T, A extends ValueSupplier<T, MinionRuntime>> void configureArgumentMenu(String name, ConfiguredInstruction<MinionRuntime> instruction, Parameter<T> parameter, MinionFakePlayer minion, ServerPlayerEntity player) {
        if(!checkInstructionExists(name, instruction, minion, player)) {
            return;
        }

        @Nullable A argument = instruction.getArguments().getArgument(parameter);

        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_3X3, player, false);
        gui.setSlot(3, new GuiElementBuilder(Items.STICK)
                .setName(Text.translatable("minions.gui.instruction.argument.configure.type", Text.translatable(TranslationUtil.getTranslationKey(argument != null ? argument.getType() : null, MinionRegistries.ARGUMENT_TYPES, "minions.gui.instruction.argument.configure.type.unset"))))
                .setCallback(() -> selectArgumentType(player)
                        .thenApply(type -> type.openConfiguration(player, parameter.type(), null)
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
                    .setCallback(() -> argument.getType().openConfiguration(player, argument.getValueType(), argument)
                            .thenAccept(newArgument -> instruction.getArguments().setArgument(parameter, newArgument))
                    )
            );
        }
        gui.open();
    }

    public static CompletableFuture<ValueSupplierType<MinionRuntime>> selectArgumentType(ServerPlayerEntity player) {
        CompletableFuture<ValueSupplierType<MinionRuntime>> future = new CompletableFuture<>();
        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false);
        for(ValueSupplierType<MinionRuntime> type : MinionRegistries.ARGUMENT_TYPES) {
            gui.addSlot(new GuiElementBuilder()
                    .setName(Text.translatable(TranslationUtil.getTranslationKey(type, MinionRegistries.ARGUMENT_TYPES)))
                    .setCallback(() -> future.complete(type))
            );
        }
        gui.open();
        return future;
    }

    public static CompletableFuture<InstructionType<MinionRuntime>> selectInstructionModuleMenu(MinionFakePlayer minion, ServerPlayerEntity player) {
        if(minion.getModuleInventory().getModules().isEmpty()) {
            player.sendMessage(Text.literal("This minion has no modules"));
            return CompletableFuture.failedFuture(new NoSuchElementException("No modules"));
        }

        CompletableFuture<InstructionType<MinionRuntime>> future = new CompletableFuture<>();

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

    public static CompletableFuture<InstructionType<MinionRuntime>> selectInstructionMenu(MinionModule module, MinionFakePlayer minion, ServerPlayerEntity player) {
        CompletableFuture<InstructionType<MinionRuntime>> future = new CompletableFuture<>();

        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false);
        for(InstructionType<MinionRuntime> instructionType : module.instructions()) {
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

    public static GuiElementBuilder createArgumentElement(ValueSupplier<?,?> valueSupplier) {
        GuiElementBuilder argumentBuilder;
        if(valueSupplier != null) {
            argumentBuilder = valueSupplier.getDisplay().createElement();
        } else {
            argumentBuilder = new GuiElementBuilder(Items.RED_WOOL)
                    .setName(Text.translatable("minions.gui.instruction.no_argument_set"));
        }
        return argumentBuilder;
    }
}

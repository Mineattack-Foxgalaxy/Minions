package io.github.skippyall.minions.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.registration.MinionComponentTypes;
import io.github.skippyall.minions.registration.MinionRegistries;
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
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

public class InstructionGui {
    public static void openInstructionMainMenu(MinionFakePlayer minion, ServerPlayerEntity player) {
        SimpleGui gui = new MinionBoundSimpleGui(ScreenHandlerType.GENERIC_3X3, player, minion);
        gui.setTitle(Text.translatable("minions.gui.instruction.title"));

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
        SimpleGui gui = new MinionBoundSimpleGui(ScreenHandlerType.GENERIC_9X3, player, minion) {
            @Override
            public void onInstructionsUpdate(MinionFakePlayer minion) {
                resetInstructionList(this, minion, player);
            }
        };
        gui.setTitle(Text.translatable("minions.gui.instruction.title"));
        resetInstructionList(gui, minion, player);

        gui.open();
    }

    private static void resetInstructionList(SimpleGui gui, MinionFakePlayer minion, ServerPlayerEntity player) {
        int i = 0;
        for (String instructionName : minion.getInstructionManager().getInstructionNames()) {
            ConfiguredInstruction<MinionRuntime> instruction = minion.getInstructionManager().getInstruction(instructionName);
            gui.setSlot(i, new GuiElementBuilder(GuiDisplay.getGuiDisplayFor(MinionRegistries.INSTRUCTION_TYPES, instruction.getInstruction(), player.getRegistryManager()).createItemStack())
                    .setName(Text.literal(instructionName))
                    .setCallback(() -> ConfigureInstructionGui.configureInstructionMenu(instructionName, instruction, minion, player))
            );
            i++;
        }
    }

    public static void createNewInstruction(MinionFakePlayer minion, ServerPlayerEntity player) {
        selectInstructionModuleMenu(minion, player).thenAccept(instructionType ->
                inputInstructionName(minion, player, "Instruction").thenAccept(name -> {
                    if (!minion.isRemoved() && !minion.isDisconnected()) {
                        ConfiguredInstruction<MinionRuntime> configuredInstruction = minion.getInstructionManager().createInstruction(name, instructionType);
                        ConfigureInstructionGui.configureInstructionMenu(name, configuredInstruction, minion, player);
                    }
                })
        );
    }

    public static CompletableFuture<String> inputInstructionName(MinionFakePlayer minion, ServerPlayerEntity player, String defaultValue) {
        return TextInput.inputSync(player, Text.translatable("minions.gui.instruction.enter_name"), defaultValue, name -> {
            if (minion.getInstructionManager().hasInstruction(name)) {
                return new Result.Error<>(Text.translatable("minions.gui.instruction.name_already_used"));
            }
            return new Result.Success<>(name);
        });
    }

    public static boolean checkInstructionExists(String name, ConfiguredInstruction<?> instruction, MinionFakePlayer minion, ServerPlayerEntity player) {
        boolean stillExists = !minion.isRemoved() && !minion.isDisconnected() && minion.getInstructionManager().getInstruction(name) == instruction;
        if (!stillExists) {
            player.closeHandledScreen();
            player.sendMessage(Text.translatable("minions.gui.instruction.removed"));
        }
        return stillExists;
    }


    public static <T, A extends ValueSupplier<T, MinionRuntime>> void configureArgumentMenu(String instructionName, ConfiguredInstruction<MinionRuntime> instruction, Parameter<?> parameter, MinionFakePlayer minion, ServerPlayerEntity player) {
        if (!checkInstructionExists(instructionName, instruction, minion, player)) {
            return;
        }

        @Nullable ValueSupplier<?, MinionRuntime> argument = instruction.getArguments().getArgument(parameter);

        if(argument == null) {
            configureTypeAndValue(instructionName, instruction, parameter, minion, player);
            return;
        }

        configureArgumentHelper(instructionName, instruction, parameter, argument, minion, player);
    }

    private static <T> void configureArgumentHelper(String instructionName, ConfiguredInstruction<MinionRuntime> instruction, Parameter<?> parameter, ValueSupplier<T, MinionRuntime> argument, MinionFakePlayer minion, ServerPlayerEntity player) {
        SimpleGui gui = new InstructionBoundSimpleGui(ScreenHandlerType.GENERIC_3X3, player, minion, instruction);

        ItemStack displayStack = GuiDisplay.getDisplayStack(MinionRegistries.VALUE_SUPPLIER_TYPES, argument.getType(), player.getRegistryManager());

        gui.setSlot(3, new GuiElementBuilder(displayStack)
                .setName(Text.translatable("minions.gui.instruction.argument.configure.type", Text.translatable(TranslationUtil.getTranslationKey(argument.getType(), MinionRegistries.VALUE_SUPPLIER_TYPES, "minions.gui.instruction.argument.configure.type.unset"))))
                .setCallback(() -> configureTypeAndValue(instructionName, instruction, parameter, minion, player))
        );
        gui.setSlot(5, new GuiElementBuilder(Items.STRUCTURE_VOID)
                .setName(Text.literal("Configure"))
                .setCallback(() -> argument.getType().openConfiguration(player, argument.getValueType(), argument)
                        .thenAccept(newArgument -> instruction.getArguments().setArgument(parameter, newArgument))
                )
        );
        gui.open();
    }

    public static CompletableFuture<ValueSupplierType<MinionRuntime>> selectArgumentType(ServerPlayerEntity player, MinionFakePlayer minion, ConfiguredInstruction<MinionRuntime> instruction) {
        CompletableFuture<ValueSupplierType<MinionRuntime>> future = new CompletableFuture<>();
        SimpleGui gui = new InstructionBoundSimpleGui(ScreenHandlerType.GENERIC_9X3, player, minion, instruction);
        for (ValueSupplierType<MinionRuntime> type : MinionRegistries.VALUE_SUPPLIER_TYPES) {
            gui.addSlot(new GuiElementBuilder(GuiDisplay.getDisplayStackWithName(MinionRegistries.VALUE_SUPPLIER_TYPES, type, player.getRegistryManager()))
                    .setCallback(() -> future.complete(type))
            );
        }
        gui.open();
        return future;
    }

    public static <T> void configureTypeAndValue(String name, ConfiguredInstruction<MinionRuntime> instruction, Parameter<T> parameter, MinionFakePlayer minion, ServerPlayerEntity player) {
        selectArgumentType(player, minion, instruction)
                .thenApply(type -> type.openConfiguration(player, parameter.type(), null)
                        .thenAccept(newArgument -> {
                            instruction.getArguments().setArgument(parameter, newArgument);
                            configureArgumentMenu(name, instruction, parameter, minion, player);
                        })
                );
    }

    public static CompletableFuture<InstructionType<MinionRuntime>> selectInstructionModuleMenu(MinionFakePlayer minion, ServerPlayerEntity player) {
        if (minion.getModuleInventory().getModules().isEmpty()) {
            player.sendMessage(Text.translatable("minions.gui.instruction.no_modules"));
            return CompletableFuture.failedFuture(new NoSuchElementException("No modules"));
        }

        CompletableFuture<InstructionType<MinionRuntime>> future = new CompletableFuture<>();

        SimpleGui gui = new MinionBoundSimpleGui(ScreenHandlerType.GENERIC_9X3, player, minion) {
            @Override
            public void onClose() {
                if (!future.isDone()) {
                    future.cancel(false);
                }
                super.onClose();
            }
        };
        gui.setTitle(Text.translatable("minions.gui.instruction.select_instruction"));

        for (int i = 0; i < minion.getModuleInventory().size(); i++) {
            ItemStack moduleItem = minion.getModuleInventory().getStack(i);
            MinionModule module = moduleItem.get(MinionComponentTypes.MODULE);
            if (module != null && !module.instructions().isEmpty()) {
                gui.addSlot(new GuiElementBuilder(moduleItem)
                        .setCallback(() -> selectInstructionMenu(module, minion, player)
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

        SimpleGui gui = new MinionBoundSimpleGui(ScreenHandlerType.GENERIC_9X3, player, minion) {
            @Override
            public void onClose() {
                if (!future.isDone()) {
                    future.cancel(false);
                }
                super.onClose();
            }
        };
        gui.setTitle(Text.translatable("minions.gui.instruction.select_instruction"));

        for (InstructionType<MinionRuntime> instructionType : module.instructions()) {
            gui.addSlot(createInstructionElement(instructionType, player.getRegistryManager())
                    .setCallback(() -> future.complete(instructionType))
            );
        }

        gui.open();
        return future;
    }

    public static GuiElementBuilder createInstructionElement(InstructionType<MinionRuntime> instructionType, DynamicRegistryManager manager) {
        GuiElementBuilder instructionBuilder;
        if (instructionType != null) {
            instructionBuilder = new GuiElementBuilder(GuiDisplay.getDisplayStackWithName(MinionRegistries.INSTRUCTION_TYPES, instructionType, manager));
        } else {
            instructionBuilder = new GuiElementBuilder(Items.RED_WOOL)
                    .setName(Text.translatable("minions.gui.instruction.no_instruction_set"));
        }
        return instructionBuilder;
    }

    public static GuiElementBuilder createParameterElement(Parameter<?> parameter, @Nullable ValueSupplier<?,?> valueSupplier, DynamicRegistryManager manager) {
        GuiElementBuilder builder = new GuiElementBuilder(GuiDisplay.getDisplayStack(MinionRegistries.VALUE_TYPES, parameter.type(), manager))
                .setName(Text.translatable("minions.gui.instruction.parameter", parameter.name(), Text.translatable(TranslationUtil.getTranslationKey(parameter.type(), MinionRegistries.VALUE_TYPES))));
        if(valueSupplier != null) {
                builder.addLoreLine(Text.translatable("minions.gui.instruction.argument", valueSupplier.getDisplayText()));
        }
        return builder;
    }
}
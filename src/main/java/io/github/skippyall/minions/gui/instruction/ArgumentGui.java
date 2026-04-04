package io.github.skippyall.minions.gui.instruction;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.program.supplier.Parameter;
import io.github.skippyall.minions.program.supplier.ValueSupplier;
import io.github.skippyall.minions.program.supplier.ValueSupplierType;
import io.github.skippyall.minions.registration.MinionRegistries;
import io.github.skippyall.minions.util.TranslationUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ArgumentGui {
    public static <T, A extends ValueSupplier<T, MinionRuntime>> void configureArgumentMenu(String instructionName, ConfiguredInstruction<MinionRuntime> instruction, Parameter<?> parameter, MinionFakePlayer minion, ServerPlayerEntity player) {
        if (!InstructionGui.checkInstructionExists(instructionName, instruction, minion, player)) {
            return;
        }

        @Nullable ValueSupplier<?, MinionRuntime> argument = instruction.getArguments().getArgument(parameter);

        if(argument == null) {
            configureTypeAndValue(instructionName, instruction, parameter, minion, player);
            return;
        }

        configureArgumentHelper(instructionName, instruction, parameter, argument, minion, player);
    }

    private static <F, T> void configureArgumentHelper(String instructionName, ConfiguredInstruction<MinionRuntime> instruction, Parameter<T> parameter, ValueSupplier<F, MinionRuntime> argument, MinionFakePlayer minion, ServerPlayerEntity player) {
        SimpleGui gui = new InstructionBoundSimpleGui(ScreenHandlerType.GENERIC_3X3, player, minion, instruction);

        ItemStack displayStack = GuiDisplay.getDisplayStack(MinionRegistries.VALUE_SUPPLIER_TYPES, argument.getType(), player.getRegistryManager());

        gui.setSlot(3, new GuiElementBuilder(displayStack)
                .setName(Text.translatable("minions.gui.instruction.argument.configure.type", Text.translatable(TranslationUtil.getTranslationKey(argument.getType(), MinionRegistries.VALUE_SUPPLIER_TYPES, "minions.gui.instruction.argument.configure.type.unset"))))
                .setCallback(() -> configureTypeAndValue(instructionName, instruction, parameter, minion, player))
        );
        gui.setSlot(5, new GuiElementBuilder(Items.STRUCTURE_VOID)
                .setName(Text.literal("Configure"))
                .setCallback(() -> argument.getType().openConfiguration(player, argument.getValueType(), argument)
                        .thenAccept(newArgument -> {
                            instruction.getArguments().setArgument(parameter, newArgument);
                            configureArgumentMenu(instructionName, instruction, parameter, minion, player);
                        })
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
                        .thenAccept(v -> {
                            instruction.getArguments().setArgument(parameter, v);
                            configureArgumentMenu(name, instruction, parameter, minion, player);
                        })
                );
    }
}

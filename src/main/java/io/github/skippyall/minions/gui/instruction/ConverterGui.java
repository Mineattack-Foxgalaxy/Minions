package io.github.skippyall.minions.gui.instruction;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.gui.PaginatedList;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.conversion.ValueConverter;
import io.github.skippyall.minions.program.conversion.ValueConverterType;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.program.supplier.Parameter;
import io.github.skippyall.minions.program.supplier.ValueSupplierList;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.MinionRegistries;
import net.minecraft.item.Items;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public class ConverterGui {
    public static void configureConvertersMenu(ServerPlayerEntity player, MinionFakePlayer minion, ConfiguredInstruction<MinionRuntime> instruction, Parameter<?> parameter, Runnable back) {
        InstructionBoundSimpleGui gui = new InstructionBoundSimpleGui(ScreenHandlerType.GENERIC_9X4, player, minion, instruction);

        ValueSupplierList.ValueSupplierEntry<?,?,MinionRuntime> entry = instruction.getArguments().getEntry(parameter);
        PaginatedList.createList(
                gui,
                entry.getConverters(),
                converter -> createConverterElement(converter, player.getRegistryManager())
                    .setCallback(() -> configureConverter(player, minion, instruction, parameter, entry, converter)),
                back
        );
        gui.open();
    }

    public static GuiElementBuilder createConverterElement(ValueConverter<?,?> converter, DynamicRegistryManager manager) {
        GuiElementBuilder builder = new GuiElementBuilder(GuiDisplay.getDisplayStack(MinionRegistries.VALUE_CONVERTER_TYPES, converter.getType(), manager));
        builder.addLoreLine(converter.getDisplayText());
        return builder;
    }

    public static void configureConverter(ServerPlayerEntity player, MinionFakePlayer minion, ConfiguredInstruction<MinionRuntime> instruction, Parameter<?> parameter, ValueSupplierList.ValueSupplierEntry<?,?,MinionRuntime> entry, @Nullable ValueConverter<?,?> converter) {
        InstructionBoundSimpleGui gui = new InstructionBoundSimpleGui(ScreenHandlerType.GENERIC_3X3, player, minion, instruction);
        gui.setSlot(3, new GuiElementBuilder(GuiDisplay.getDisplayStackWithName(MinionRegistries.VALUE_CONVERTER_TYPES, converter.getType(), player.getRegistryManager()))
                .setCallback(() -> {
                    InstructionBoundSimpleGui chooseTypeGui = new InstructionBoundSimpleGui(ScreenHandlerType.GENERIC_9X4, player, minion, instruction);
                    PaginatedList.createList(
                            chooseTypeGui,
                            MinionRegistries.VALUE_CONVERTER_TYPES,
                            type -> new GuiElementBuilder(GuiDisplay.getDisplayStackWithName(MinionRegistries.VALUE_CONVERTER_TYPES, type, player.getRegistryManager()))
                                    .setCallback(() -> {
                                        entry.set
                                    }),
                            () -> configureConverter(player, minion, instruction, parameter, entry, converter)
                    );
                }));

        gui.setSlot(5, new GuiElementBuilder(Items.STRUCTURE_VOID));

        gui.open();
    }

    public static <F,T> void selectConverterMenu(ValueType<F> from, ValueType<T> to, ServerPlayerEntity player, MinionFakePlayer minion, ConfiguredInstruction<MinionRuntime> instruction) {
        InstructionBoundSimpleGui gui = new InstructionBoundSimpleGui(ScreenHandlerType.GENERIC_9X3, player, minion, instruction);
        for(ValueConverterType<?> valueConverterType : MinionRegistries.VALUE_CONVERTER_TYPES) {
            gui.addSlot(new GuiElementBuilder(GuiDisplay.getDisplayStackWithName(MinionRegistries.VALUE_CONVERTER_TYPES, valueConverterType, player.getRegistryManager()))
                    .setCallback(() -> valueConverterType.configure(player, from, to, null).thenAccept(c -> )));
        }
        gui.open();
    }
}

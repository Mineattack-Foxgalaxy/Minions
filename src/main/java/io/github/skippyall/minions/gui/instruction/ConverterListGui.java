package io.github.skippyall.minions.gui.instruction;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.program.conversion.ConverterList;
import io.github.skippyall.minions.program.conversion.ValueConverter;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.MinionRegistries;
import io.github.skippyall.minions.util.TranslationUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

public class ConverterListGui extends MinionsGui {
    private ConverterList converters;

    private SimpleGui gui;
    private int page = 0;

    private ValueType<?> inputType;
    private ValueType<?> outputType;

    public ConverterListGui(MinionsGui parent, ConverterList converters, ValueType<?> inputType, ValueType<?> outputType) {
        super(parent);
        this.converters = converters;
        this.inputType = inputType;
        this.outputType = outputType;
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

        gui.setTitle(Component.translatable("minions.gui.instruction.converters"));

        updateConverters();

        gui.setSlot(21, new GuiElementBuilder(Items.SPECTRAL_ARROW)
                .setCallback(() -> {
                    if(page > 0) {
                        page--;
                        updateConverters();
                    }
                })
        );

        gui.setSlot(23, new GuiElementBuilder(Items.ARROW)
                .setCallback(() -> {
                    if(page * 4 + 4 < converters.getConverters().size() + 1) {
                        page++;
                        updateConverters();
                    }
                })
        );

        gui.setSlot(26, backButton());

        gui.open();
    }

    private ValueType<?> getInputTypeOfConverter(int converterIndex) {
        if(converterIndex < converters.getConverters().size() && converterIndex >= 0) {
            return converters.getConverters().get(converterIndex).getFrom();
        } else if(converterIndex == converters.getConverters().size()) {
            return outputType;
        } else {
            throw new IndexOutOfBoundsException("Can't get input type of converter " + converterIndex + ", list size " + converters.getConverters().size());
        }
    }

    private ValueType<?> getOutputTypeOfConverter(int converterIndex) {
        if(converterIndex < converters.getConverters().size() && converterIndex >= 0) {
            return converters.getConverters().get(converterIndex).getTo();
        } else if(converterIndex == -1) {
            return inputType;
        } else {
            throw new IndexOutOfBoundsException("Can't get output type of converter " + converterIndex + ", list size " + converters.getConverters().size());
        }
    }

    public void updateConverters() {
        for(int slot = 0; slot < 18; slot++) {
            gui.clearSlot(slot);
        }

        int lastConverter = Math.min(5, converters.getConverters().size() + 2 - page * 4);
        for(int i = 0; i < lastConverter; i++) {
            //Each page has 5 converters, but the last is displayed on the next page as well
            //The input element index is -1, the output element index is the list size
            int converterIndex = page * 4 + i - 1;
            int slot = 9 + 2 * i;

            if(converterIndex == converters.getConverters().size()) {
                gui.setSlot(slot, new GuiElementBuilder(Items.HOPPER));
            } else {
                ValueType<?> thisConverterOutput = getOutputTypeOfConverter(converterIndex);
                ValueType<?> nextConverterInput = getInputTypeOfConverter(converterIndex + 1);

                //What should be shown at the converter's slot
                if (converterIndex == -1) {
                    gui.setSlot(slot, new GuiElementBuilder(Items.DROPPER));
                } else {
                    ValueType<?> previousConverterOutput = getOutputTypeOfConverter(converterIndex - 1);

                    ValueConverter<?, ?> converter = converters.getConverters().get(converterIndex);
                    gui.setSlot(slot, createConverterDisplay(converter, previousConverterOutput, nextConverterInput, converterIndex));

                    GuiElementBuilder warning = createConverterWarning(converter);
                    if (warning != null) {
                        gui.setSlot(slot - 8, warning);
                    }
                }

                //show cast in the next slot
                if (i != 4 && converterIndex != converters.getConverters().size()) {
                    gui.setSlot(slot + 1, createCastDisplay(thisConverterOutput, nextConverterInput, converterIndex + 1));

                    GuiElementBuilder warning = createCastWarning(thisConverterOutput, nextConverterInput);
                    if (warning != null) {
                        gui.setSlot(slot - 8, warning);
                    }
                }
            }
        }
    }

    private GuiElementBuilder createConverterDisplay(ValueConverter<?, ?> converter, ValueType<?> fromType, ValueType<?> toType, int converterIndex) {
        return new GuiElementBuilder(GuiDisplay.getDisplayStackWithName(MinionRegistries.VALUE_CONVERTER_TYPES, converter.getType(), viewer.registryAccess()))
                .addLoreLine(converter.getDisplayText())
                .setCallback(() -> new ConverterGui(this, converter, fromType, toType, converters, false, converterIndex));
    }

    private GuiElementBuilder createCastDisplay(ValueType<?> fromType, ValueType<?> toType, int newConverterIndex) {
        return new GuiElementBuilder(Items.MAGENTA_GLAZED_TERRACOTTA)
                .setName(Component.translatable(
                        "minions.gui.instruction.converters.cast",
                        Component.translatable(TranslationUtil.getTranslationKey(fromType, MinionRegistries.VALUE_TYPES)),
                        Component.translatable(TranslationUtil.getTranslationKey(toType, MinionRegistries.VALUE_TYPES))
                ))
                .setCallback(() -> new ConverterGui(this, null, fromType, toType, converters, true, newConverterIndex));
    }

    private @Nullable GuiElementBuilder createCastWarning(ValueType<?> fromType, ValueType<?> toType) {
        Component warning = ConverterList.createCastWarning(fromType, toType);

        if(warning != null) {
            return new GuiElementBuilder(Items.RED_BANNER)
                    .setName(Component.translatable("minions.generic.error"))
                    .addLoreLine(warning);
        } else {
            return null;
        }
    }

    private @Nullable GuiElementBuilder createConverterWarning(ValueConverter<?,?> converter) {
        Component warning = ConverterList.createConverterWarning(converter);

        if(warning != null) {
            return new GuiElementBuilder(Items.RED_BANNER)
                    .setName(Component.translatable("minions.generic.error"))
                    .addLoreLine(warning);
        } else {
            return null;
        }
    }

    @Override
    protected void closeBacking() {
        gui.close();
    }
}

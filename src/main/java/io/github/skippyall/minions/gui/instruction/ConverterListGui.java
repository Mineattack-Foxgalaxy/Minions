package io.github.skippyall.minions.gui.instruction;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.program.conversion.ConverterList;
import io.github.skippyall.minions.program.conversion.ValueConverter;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.MinionRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ConverterListGui extends MinionsGui {
    private ConverterList converters;

    private List<ValueType<?>> outputTypeCache = new ArrayList<>();

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

        updateOutputTypeCache();
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
                    if(page * 4 + 4 < converters.size() + 1) {
                        page++;
                        updateConverters();
                    }
                })
        );

        gui.setSlot(26, backButton());

        gui.open();
    }

    @Override
    protected void reopen() {
        updateOutputTypeCache();
        updateConverters();
        gui.open();
    }

    private void updateOutputTypeCache() {
        outputTypeCache.clear();
        ValueType<?> current = inputType;
        for(ValueConverter converter : converters) {
            current = converter.getOutputType(current);
            outputTypeCache.add(current);
        }
    }

    private ValueType<?> getOutputTypeOfConverter(int converterIndex) {
        if(converterIndex < converters.size() && converterIndex >= 0) {
            return outputTypeCache.get(converterIndex);
        } else if(converterIndex == -1) {
            return inputType;
        } else {
            throw new IndexOutOfBoundsException("Can't get output type of converter " + converterIndex + ", list size " + converters.size());
        }
    }

    public void updateConverters() {
        for(int slot = 0; slot < 18; slot++) {
            gui.clearSlot(slot);
        }

        int lastConverter = Math.min(5, converters.size() + 2 - page * 4);
        for(int i = 0; i < lastConverter; i++) {
            //Each page has 5 converters, but the last is displayed on the next page as well
            //The io element index is -1, the output element index is the list size
            int converterIndex = page * 4 + i - 1;
            int slot = 9 + 2 * i;

            if(converterIndex == converters.size()) {
                gui.setSlot(slot, new GuiElementBuilder(Items.HOPPER));
            } else {
                //What should be shown at the converter's slot
                if (converterIndex == -1) {
                    gui.setSlot(slot, new GuiElementBuilder(Items.DROPPER));
                } else {
                    ValueType<?> previousConverterOutput = getOutputTypeOfConverter(converterIndex - 1);

                    ValueConverter converter = converters.get(converterIndex);
                    gui.setSlot(slot, createConverterDisplay(converter, previousConverterOutput, converterIndex));

                    GuiElementBuilder warning = createConverterWarning(converter);
                    if (warning != null) {
                        gui.setSlot(slot - 8, warning);
                    }
                }

                //show cast in the next slot
                if (i != 4 && converterIndex != converters.size()) {
                    ValueType<?> thisConverterOutput = getOutputTypeOfConverter(converterIndex);
                    gui.setSlot(slot + 1, createCastDisplay(thisConverterOutput, converterIndex + 1));
                }
            }
        }
    }

    private GuiElementBuilder createConverterDisplay(ValueConverter converter, ValueType<?> from, int converterIndex) {
        return new GuiElementBuilder(GuiDisplay.getDisplayStackWithName(MinionRegistries.VALUE_CONVERTER_TYPES, converter.getType(), viewer.registryAccess()))
                .addLoreLine(converter.getDisplayText())
                .setCallback(() -> new ConverterGui(this, from, converter, converters, false, converterIndex));
    }

    private GuiElementBuilder createCastDisplay(ValueType<?> from, int newConverterIndex) {
        return new GuiElementBuilder(Items.MAGENTA_GLAZED_TERRACOTTA)
                .setCallback(() -> new ConverterGui(this, from, null, converters, true, newConverterIndex));
    }

    private @Nullable GuiElementBuilder createConverterWarning(ValueConverter converter) {
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

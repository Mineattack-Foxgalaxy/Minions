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
            public void onClose() {
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
                    if(page * 4 + 4 < converters.getConverters().size()) {
                        page++;
                        updateConverters();
                    }
                })
        );

        gui.open();
    }

    public void updateConverters() {
        int lastConverter = Math.min(5, converters.getConverters().size() + 2 - page * 4);
        for(int i = 0; i < lastConverter; i++) {
            //Each page has 5 converters, but the last is displayed on the next page as well
            int converterIndex = page * 4 + i;
            //without input element
            int actualConverterIndex = converterIndex - 1;
            int slot = 9 + 2 * i;
            if(converterIndex == 0) {
                gui.setSlot(slot, new GuiElementBuilder(Items.DROPPER));
            } else if(converterIndex == converters.getConverters().size() + 1) {
                gui.setSlot(slot, new GuiElementBuilder(Items.HOPPER));
            } else {
                ValueConverter<?, ?> converter = converters.getConverters().get(actualConverterIndex);
                ValueType<?> fromType = actualConverterIndex >= 1 ? converters.getConverters().get(actualConverterIndex - 1).getTo() : inputType;
                ValueType<?> toType = actualConverterIndex < converters.getConverters().size() - 1 ? converters.getConverters().get(actualConverterIndex + 1).getFrom() : outputType;

                gui.setSlot(slot, new GuiElementBuilder(GuiDisplay.getDisplayStackWithName(MinionRegistries.VALUE_CONVERTER_TYPES, converter.getType(), viewer.registryAccess()))
                        .addLoreLine(converter.getDisplayText())
                        .setCallback(() -> new ConverterGui(this, converter, fromType, toType, converters, false, actualConverterIndex))
                );
            }
            if(i != 4 && actualConverterIndex != converters.getConverters().size()) {
                ValueType<?> fromType = actualConverterIndex >= 0 ? converters.getConverters().get(actualConverterIndex).getTo() : inputType;
                ValueType<?> toType = actualConverterIndex < converters.getConverters().size() - 1 ? converters.getConverters().get(actualConverterIndex + 1).getFrom() : outputType;

                gui.setSlot(slot + 1, new GuiElementBuilder(Items.MAGENTA_GLAZED_TERRACOTTA)
                        .setName(Component.translatable(
                                "minions.gui.instruction.converters.cast",
                                Component.translatable(TranslationUtil.getTranslationKey(fromType, MinionRegistries.VALUE_TYPES)),
                                Component.translatable(TranslationUtil.getTranslationKey(toType, MinionRegistries.VALUE_TYPES))
                        ))
                        .setCallback(() -> new ConverterGui(this, null, fromType, toType, converters, true, actualConverterIndex + 1))
                );
            }
        }
    }

    @Override
    protected void closeBacking() {
        gui.close();
    }
}

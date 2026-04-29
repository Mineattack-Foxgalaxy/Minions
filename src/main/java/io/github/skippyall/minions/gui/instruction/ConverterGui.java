package io.github.skippyall.minions.gui.instruction;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.gui.PaginatedList;
import io.github.skippyall.minions.program.conversion.ConverterList;
import io.github.skippyall.minions.program.conversion.ValueConverter;
import io.github.skippyall.minions.program.conversion.ValueConverterType;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.MinionRegistries;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class ConverterGui extends MinionsGui {
    private @Nullable ValueConverterType<?> valueConverterType;
    private @Nullable ValueConverter<?,?> converter;
    private ConverterList list;
    private ValueType<?> from, to;

    private SimpleGui gui;
    private boolean isNew;
    private int index;

    public ConverterGui(MinionsGui parent, @Nullable ValueConverter<?,?> converter, ValueType<?> from, ValueType<?> to, ConverterList list, boolean isNew, int index) {
        super(parent);
        open();
        this.converter = converter;
        if(converter != null) {
            this.valueConverterType = converter.getType();
        }
        this.from = from;
        this.to = to;
        this.list = list;
        this.isNew = isNew;
        this.index = index;
    }

    @Override
    protected void open() {
        gui = new SimpleGui(MenuType.GENERIC_3x3, viewer, false) {
            @Override
            public void onClose() {
                onBackingClosed();
            }
        };

        gui.setTitle(Component.translatable("minions.gui.instruction.converter.title"));

        updateTypeDisplay();
        updateConverterDisplay();

        gui.open();
    }

    @Override
    protected void reopen() {
        gui.open();
    }

    private void updateTypeDisplay() {
        gui.setSlot(3, new GuiElementBuilder(GuiDisplay.getDisplayStackWithName(MinionRegistries.VALUE_CONVERTER_TYPES, valueConverterType, viewer.registryAccess()))
                .setCallback(this::configureType)
        );
    }

    private void updateConverterDisplay() {
        gui.setSlot(5, new GuiElementBuilder(Items.STRUCTURE_VOID)
                .setName(Component.translatable("minions.gui.instruction.converter.title"))
                .addLoreLine(converter == null ? Component.translatable("minions.gui.not_set") : converter.getDisplayText())
                .setCallback(this::configureData)
        );
    }

    @Override
    protected void closeBacking() {
        gui.close();
    }

    private void setType(ValueConverterType<?> valueConverterType) {
        this.valueConverterType = valueConverterType;
        updateTypeDisplay();
    }

    public void setConverter(ValueConverter<?,?> converter) {
        this.converter = converter;
        if(isNew) {
            list.getConverters().add(index, converter);
            isNew = false;
        } else {
            list.getConverters().set(index, converter);
        }
        updateConverterDisplay();
    }

    private void configureType() {
        PaginatedList.createList(
                this,
                Component.translatable("minions.gui.instruction.converter.type.title"),
                MinionRegistries.VALUE_CONVERTER_TYPES,
                (type, me) -> new GuiElementBuilder(
                        GuiDisplay.getDisplayStackWithName(MinionRegistries.VALUE_CONVERTER_TYPES, type, viewer.registryAccess())
                ).setCallback(() -> {
                    setType(type);
                    me.close();
                })
        );
    }

    private void configureData() {
        if(valueConverterType != null) {
            valueConverterType.configure(this, from, to, converter)
                    .thenAccept(newConverter -> {
                        setConverter(newConverter);
                        if(child != null) {
                            child.close();
                        }
                    });
        }
    }

    public static GuiElementBuilder createConverterElement(ValueConverter<?,?> converter, RegistryAccess manager) {
        GuiElementBuilder builder = new GuiElementBuilder(GuiDisplay.getDisplayStack(MinionRegistries.VALUE_CONVERTER_TYPES, converter.getType(), manager));
        builder.addLoreLine(converter.getDisplayText());
        return builder;
    }
}

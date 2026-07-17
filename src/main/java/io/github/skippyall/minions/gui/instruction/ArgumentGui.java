package io.github.skippyall.minions.gui.instruction;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.gui.PaginatedList;
import io.github.skippyall.minions.gui.input.Result;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.program.supplier.ConfiguredValueSupplier;
import io.github.skippyall.minions.program.handler.Parameter;
import io.github.skippyall.minions.program.supplier.ValueSupplier;
import io.github.skippyall.minions.program.supplier.ValueSupplierType;
import io.github.skippyall.minions.program.value.TypedValue;
import io.github.skippyall.minions.registration.MinionRegistries;
import io.github.skippyall.minions.util.TranslationUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

public class ArgumentGui extends MinionsGui {
    private final ConfiguredInstruction instruction;
    private final Parameter<?> parameter;
    private final Context resolutionContext;

    private SimpleGui gui;

    private @Nullable ValueSupplierType argumentType;
    private @Nullable ConfiguredValueSupplier<?> entry;

    public ArgumentGui(MinionsGui parent, ConfiguredInstruction instruction, Parameter<?> parameter, Context resolutionContext) {
        super(parent);
        this.instruction = instruction;
        this.parameter = parameter;
        this.resolutionContext = resolutionContext;

        this.entry = instruction.getArguments().getEntry(parameter);
        if(entry != null) {
            this.argumentType = entry.getSupplier().getType();
        }
        open();
    }

    public @Nullable ValueSupplier getArgument() {
        if(entry != null) {
            return entry.getSupplier();
        }
        return null;
    }

    @Override
    protected void open() {
        gui = new SimpleGui(MenuType.GENERIC_3x3, viewer, false) {
            @Override
            public void onPlayerClose(boolean success) {
                onBackingClosed();
            }
        };
        gui.setTitle(Component.translatable(
                "minions.gui.instruction.argument.title",
                parameter.name(),
                Component.translatable(TranslationUtil.getTranslationKey(parameter.type(), MinionRegistries.VALUE_TYPES))
        ));

        gui.setSlot(2, backButton());

        updateTypeConfiguration();
        updateArgumentConfiguration();
        updateConverterConfiguration();
        gui.open();
    }

    private void updateTypeConfiguration() {
        ItemStack displayStack;
        if(argumentType != null) {
            displayStack = GuiDisplay.getDisplayStack(MinionRegistries.VALUE_SUPPLIER_TYPES, argumentType, viewer.registryAccess());
        } else {
            displayStack = new ItemStack(Items.BARRIER);
        }

        gui.setSlot(3, new GuiElementBuilder(displayStack)
                .setName(Component.translatable("minions.gui.instruction.argument.configure.type"))
                .addLoreLine(Component.translatable(TranslationUtil.getTranslationKey(
                        argumentType,
                        MinionRegistries.VALUE_SUPPLIER_TYPES,
                        "minions.gui.not_set"
                )))
                .setCallback(this::selectArgumentType)
        );
    }

    private void updateArgumentConfiguration() {
        if(argumentType != null) {
            gui.setSlot(4, new GuiElementBuilder(Items.STRUCTURE_VOID)
                    .setName(Component.translatable("minions.gui.instruction.argument.configure.data"))
                    .addLoreLine(getArgument() != null ? getArgument().getDisplayText() : Component.translatable("minions.gui.not_set"))
                    .setCallback(() -> argumentType.openConfiguration(this, parameter.type(), getArgument())
                            .thenAccept(this::setArgument)
                    )
            );
        }
    }

    private void updateConverterConfiguration() {
        if(entry != null) {
            gui.setSlot(5, new GuiElementBuilder(Items.CRAFTER)
                    .setName(Component.translatable("minions.gui.instruction.converters"))
                    .setCallback(this::configureConvertersMenu)
            );
        }
    }

    @Override
    protected void closeBacking() {
        gui.close();
    }

    public void setArgumentType(ValueSupplierType type) {
        this.argumentType = type;
        if(entry != null && getArgument().getType() != argumentType) {
            instruction.getArguments().removeEntry(parameter);
            entry = null;
        }
        updateTypeConfiguration();
    }

    public void setArgument(ValueSupplier argument) {
        if(entry != null) {
            entry.setSupplier(argument);
        } else {
            entry = instruction.getArguments().createEntry(parameter, argument);
        }
    }

    public void selectArgumentType() {
        PaginatedList.createList(this, Component.translatable("minions.gui.instruction.argument.configure.type.title"), MinionRegistries.VALUE_SUPPLIER_TYPES, (type, me) ->
                new GuiElementBuilder(GuiDisplay.getDisplayStackWithName(MinionRegistries.VALUE_SUPPLIER_TYPES, type, viewer.registryAccess()))
                        .setCallback(() -> {
                            setArgumentType(type);
                            me.goBack();
                        })
        );
    }

    public void configureConvertersMenu() {
        if(entry != null) {
            Result<TypedValue<?>, Component> result = entry.getSupplier().resolve(resolutionContext);
            if(result instanceof Result.Success<TypedValue<?>, Component> success) {
                new ConverterListGui(this, entry.getConverters(), success.result().type(), entry.getParameter().type());
            }
        }
    }
}

package io.github.skippyall.minions.gui.instruction;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.gui.PaginatedList;
import io.github.skippyall.minions.gui.minion.GuiContext;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.program.supplier.Parameter;
import io.github.skippyall.minions.program.supplier.ValueSupplier;
import io.github.skippyall.minions.program.supplier.ValueSupplierList;
import io.github.skippyall.minions.program.supplier.ValueSupplierType;
import io.github.skippyall.minions.registration.MinionRegistries;
import io.github.skippyall.minions.util.TranslationUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class ArgumentGui extends MinionsGui {
    private final GuiContext.ValueSupplier context;
    private final MinionFakePlayer minion;
    private final ConfiguredInstruction<MinionRuntime> instruction;
    private final Parameter<?> parameter;

    private SimpleGui gui;

    private @Nullable ValueSupplierType<MinionRuntime> argumentType;
    private @Nullable ValueSupplierList.ValueSupplierEntry<?, MinionRuntime> entry;

    public ArgumentGui(MinionsGui parent, GuiContext.ValueSupplier context) {
        super(parent);
        minion = context.getMinion();
        instruction = context.getInstruction();
        this.parameter = context.getParameter();
        this.context = context;

        this.entry = instruction.getArguments().getEntry(parameter);
        if(entry != null) {
            this.argumentType = entry.getSupplier().getType();
        }
        open();
    }

    public String getInstructionName() {
        return context.getName();
    }

    public @Nullable ValueSupplier<?, MinionRuntime> getArgument() {
        if(entry != null) {
            return entry.getSupplier();
        }
        return null;
    }

    @Override
    protected void open() {
        gui = new SimpleGui(ScreenHandlerType.GENERIC_3X3, viewer, false) {
            @Override
            public void onClose() {
                onBackingClosed();
            }
        };
        gui.setTitle(Text.translatable(
                "minions.gui.instruction.argument.title",
                parameter.name(),
                Text.translatable(TranslationUtil.getTranslationKey(parameter.type(), MinionRegistries.VALUE_TYPES))
        ));

        updateTypeConfiguration();
        updateArgumentConfiguration();
        updateConverterConfiguration();
        gui.open();
    }

    private void updateTypeConfiguration() {
        ItemStack displayStack;
        if(argumentType != null) {
            displayStack = GuiDisplay.getDisplayStack(MinionRegistries.VALUE_SUPPLIER_TYPES, argumentType, viewer.getRegistryManager());
        } else {
            displayStack = new ItemStack(Items.BARRIER);
        }

        gui.setSlot(3, new GuiElementBuilder(displayStack)
                .setName(Text.translatable("minions.gui.instruction.argument.configure.type"))
                .addLoreLine(Text.translatable(TranslationUtil.getTranslationKey(
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
                    .setName(Text.translatable("minions.gui.instruction.argument.configure.data"))
                    .addLoreLine(getArgument() != null ? getArgument().getDisplayText() : Text.translatable("minions.gui.not_set"))
                    .setCallback(() -> argumentType.openConfiguration(this, parameter.type(), getArgument())
                            .thenAccept(newArgument -> {
                                setArgument(newArgument);
                                if(child != null) {
                                    child.close();
                                }
                            })
                    )
            );
        }
    }

    private void updateConverterConfiguration() {
        if(entry != null) {
            gui.setSlot(5, new GuiElementBuilder(Items.CRAFTER)
                    .setName(Text.translatable("minions.gui.instruction.converters"))
                    .setCallback(this::configureConvertersMenu)
            );
        }
    }

    @Override
    protected void closeBacking() {
        gui.close();
    }

    public void setArgumentType(ValueSupplierType<MinionRuntime> type) {
        this.argumentType = type;
        if(entry != null && getArgument().getType() != argumentType) {
            instruction.getArguments().removeEntry(parameter);
            entry = null;
        }
        updateTypeConfiguration();
    }

    public void setArgument(ValueSupplier<?, MinionRuntime> argument) {
        if(entry != null) {
            entry.setSupplier(argument);
        } else {
            entry = instruction.getArguments().createEntry(parameter, argument);
        }
    }

    public void selectArgumentType() {
        PaginatedList.createList(this, Text.translatable("minions.gui.instruction.argument.configure.type.title"), MinionRegistries.VALUE_SUPPLIER_TYPES, (type, me) ->
                new GuiElementBuilder(GuiDisplay.getDisplayStackWithName(MinionRegistries.VALUE_SUPPLIER_TYPES, type, viewer.getRegistryManager()))
                        .setCallback(() -> {
                            setArgumentType(type);
                            me.close();
                        })
        );
    }

    public void configureConvertersMenu() {
        if(entry != null) {
            new ConverterListGui(this, entry.getConverters(), entry.getSupplier().getValueType(), entry.getParameter().type());
        }
    }
}

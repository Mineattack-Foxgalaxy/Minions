package io.github.skippyall.minions.gui.instruction;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.gui.PaginatedList;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.handler.ConfiguredValueHandler;
import io.github.skippyall.minions.program.handler.Parameter;
import io.github.skippyall.minions.program.handler.ValueHandler;
import io.github.skippyall.minions.program.handler.ValueHandlerList;
import io.github.skippyall.minions.program.handler.ValueHandlerType;
import io.github.skippyall.minions.program.handler.supplier.ValueSupplierType;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.registration.MinionRegistries;
import io.github.skippyall.minions.util.TranslationUtil;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

public abstract class ValueHandlerGui<H extends ValueHandler<H>> extends MinionsGui {
    protected final ConfiguredInstruction instruction;
    protected final Parameter<?> parameter;
    protected final Context resolutionContext;

    protected SimpleGui gui;

    protected @Nullable ValueHandlerType<H> argumentType;
    protected @Nullable ConfiguredValueHandler<?, H> entry;

    public ValueHandlerGui(MinionsGui parent, ConfiguredInstruction instruction, Parameter<?> parameter, Context resolutionContext) {
        super(parent);
        this.instruction = instruction;
        this.parameter = parameter;
        this.resolutionContext = resolutionContext;

        this.entry = getHandlerList().getEntry(parameter);
        if(entry != null) {
            this.argumentType = entry.getHandler().getType();
        }
        open();
    }

    protected abstract ValueHandlerList<H, ? extends ConfiguredValueHandler<?, H>> getHandlerList();

    protected abstract Registry<ValueHandlerType<H>> getTypeRegistry();

    protected abstract void configureConvertersMenu();

    public @Nullable H getArgument() {
        if(entry != null) {
            return entry.getHandler();
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
            displayStack = argumentType.getDisplayStack(viewer.registryAccess());
        } else {
            displayStack = new ItemStack(Items.BARRIER);
        }

        gui.setSlot(3, new GuiElementBuilder(displayStack)
                .setName(Component.translatable("minions.gui.instruction.argument.configure.type"))
                .addLoreLine(argumentType.getTranslation())
                .setCallback(this::selectArgumentType)
        );
    }

    private void updateArgumentConfiguration() {
        if(argumentType != null && !(argumentType instanceof ValueSupplierType.Singleton)) {
            gui.setSlot(4, new GuiElementBuilder(Items.STRUCTURE_VOID)
                    .setName(Component.translatable("minions.gui.instruction.argument.configure.data"))
                    .addLoreLine(getArgument() != null ? getArgument().getDisplayText() : Component.translatable("minions.gui.not_set"))
                    .setCallback(() -> {
                        if (argumentType != null) {
                            argumentType.openConfiguration(this, parameter.type(), getArgument())
                                    .thenAccept(this::setArgument);
                        }
                    })
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

    public void setArgumentType(ValueHandlerType<H> type) {
        this.argumentType = type;
        ValueHandler<H> handler = getArgument();
        if(handler != null && handler.getType() != argumentType) {
            getHandlerList().removeEntry(parameter);
            entry = null;
        }
        updateTypeConfiguration();

        if(argumentType instanceof ValueHandlerType.Singleton<?> singleton) {
            //noinspection unchecked
            setArgument((H) singleton.getHandler());
        }
    }

    public void setArgument(H argument) {
        if(entry != null) {
            entry.setHandler(argument);
        } else {
            entry = getHandlerList().createEntry(parameter, argument);
        }
        updateArgumentConfiguration();
    }

    public void selectArgumentType() {
        PaginatedList.createList(this, Component.translatable("minions.gui.instruction.argument.configure.type.title"), getTypeRegistry(), (type, me) ->
                new GuiElementBuilder(type.getDisplayStack(viewer.registryAccess()))
                        .setCallback(() -> {
                            setArgumentType(type);
                            me.goBack();
                        })
        );
    }
}

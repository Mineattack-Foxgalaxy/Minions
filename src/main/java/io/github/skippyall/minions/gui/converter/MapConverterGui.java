package io.github.skippyall.minions.gui.converter;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.gui.instruction.InstructionGui;
import io.github.skippyall.minions.program.conversion.MapConverter;
import io.github.skippyall.minions.program.value.TypedValue;
import io.github.skippyall.minions.program.value.ValueType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MapConverterGui<F, T> extends MinionsGui {
    private final ValueType<F> from;
    private final ValueType<T> to;

    private final List<Map.Entry<F, T>> entries;
    private T defaultValue;
    private CompletableFuture<MapConverter<F, T>> future;

    private SimpleGui gui;

    public MapConverterGui(ServerPlayer viewer, @Nullable MinionsGui parent, ValueType<F> from, ValueType<T> to, List<Map.Entry<F, T>> entries, @Nullable T defaultValue, CompletableFuture<MapConverter<F, T>> future) {
        this.from = from;
        this.to = to;
        this.entries = entries;
        if(defaultValue != null) {
            this.defaultValue = defaultValue;
        } else {
            this.defaultValue = to.defaultValue();
        }
        this.future = future;
        super(viewer, parent);
        open();
    }

    @Override
    protected void open() {
        gui = new SimpleGui(MenuType.GENERIC_9x6, viewer, false) {
            @Override
            public void onRemoved() {
                onBackingClosed();
            }
        };
        gui.setSlot(8, backButton());

        refresh();

        gui.open();
    }

    private void confirm() {
        synchronized (entries) {
            Map<F, T> map = new LinkedHashMap<>();
            for(Map.Entry<F, T> entry : entries) {
                map.put(entry.getKey(), entry.getValue());
            }
            future.complete(new MapConverter<>(from, to, map, defaultValue));
        }
    }

    private void refresh() {
        synchronized (entries) {
            addAssociations(3 + 9, 5, 0);
            addConfirmButton();
        }
    }

    private void addAssociations(int startSlot, int maxLines, int startIndex) {
        for(int line = 0; line < maxLines; line++) {
            int index = line + startIndex;
            int slot = startSlot + 9 * line;

            if(index < entries.size()) {
                Map.Entry<F, T> entry = entries.get(index);

                gui.setSlot(slot, InstructionGui.createValueElement(new TypedValue<>(entry.getKey(), from), viewer.registryAccess())
                        .setCallback(() -> from.openValueDialog(this, entry.getKey()).thenAccept(value -> {
                            if(value != null) {
                                synchronized (entries) {
                                    entries.set(index, Map.<F, T>entry(value, entry.getValue()));
                                }
                                refresh();
                            }
                        }))
                );
                gui.setSlot(slot + 1, new GuiElementBuilder(Items.MAGENTA_GLAZED_TERRACOTTA));
                gui.setSlot(slot + 2, InstructionGui.createValueElement(new TypedValue<>(entry.getValue(), to), viewer.registryAccess())
                        .setCallback(() -> to.openValueDialog(this, entry.getValue()).thenAccept(value -> {
                            if(value != null) {
                                synchronized (entries) {
                                    entries.set(index, Map.<F, T>entry(entry.getKey(), value));
                                }
                                refresh();
                            }
                        }))
                );
            } else if(index == entries.size()) {
                gui.setSlot(slot + 1, new GuiElementBuilder(Items.ITEM_FRAME)
                        .setName(Component.translatable("value_converter.minions.map.input.add_new"))
                        .setCallback(() -> {
                            synchronized (entries) {
                                entries.add(Map.entry(from.defaultValue(), to.defaultValue()));
                            }
                            refresh();
                        })
                );
            } else if(index == entries.size() + 1) {
                gui.setSlot(slot + 1, InstructionGui.createValueElement(new TypedValue<>(defaultValue, to), viewer.registryAccess(), "value_converter.minions.map.display.default")
                        .setCallback(() -> to.openValueDialog(this, defaultValue).thenAccept(value -> {
                            if(value != null) {
                                defaultValue = value;
                                refresh();
                            }
                        }))
                );
            }
        }
    }

    private void addConfirmButton() {
        HashSet<F> keys = new HashSet<>();
        F duplicate = null;

        for(Map.Entry<F, T> entry : entries) {
            if(!keys.add(entry.getKey())) {
                duplicate = entry.getKey();
                break;
            }
        }

        if(duplicate != null) {
            gui.setSlot(7, new GuiElementBuilder(Items.REDSTONE_BLOCK).setName(Component.translatable("value_converter.minions.map.input.duplicate")));
        } else {
            gui.setSlot(7, new GuiElementBuilder(Items.EMERALD_BLOCK)
                    .setName(Component.translatable("minions.gui.confirm"))
                    .setCallback(this::confirm)
            );
        }
    }

    @Override
    protected void closeBacking() {
        gui.close();
    }
}

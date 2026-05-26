package io.github.skippyall.minions.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.core.IdMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PaginatedList extends MinionsGui {
    private int page = 0;
    private SimpleGui gui;
    private final Component title;
    private final int size;
    private final BiFunction<Integer, PaginatedList, GuiElementBuilder> display;
    private @Nullable Runnable onClose = null;

    public PaginatedList(MinionsGui parent, Component title, int size, BiFunction<Integer, PaginatedList, GuiElementBuilder> display) {
        super(parent);
        this.title = title;
        this.size = size;
        this.display = display;
        open();
    }

    public PaginatedList(MinionsGui parent, Component title, int size, BiFunction<Integer, PaginatedList, GuiElementBuilder> display, Runnable onClose) {
        this(parent, title, size, display);
        this.onClose = onClose;
    }

    @Override
    protected void open() {
        gui = new SimpleGui(MenuType.GENERIC_9x6, viewer, false) {
            @Override
            public void onPlayerClose(boolean success) {
                onBackingClosed();
                if(onClose != null) {
                    onClose.run();
                }
            }
        };
        gui.setTitle(title);

        gui.setSlot(8, backButton());
        addItems();
        gui.open();
    }

    @Override
    protected void closeBacking() {
        gui.close();
    }

    public static <T> void createList(MinionsGui parent, Component title, List<T> list, BiFunction<T, PaginatedList, GuiElementBuilder> display) {
        new PaginatedList(parent, title, list.size(), (i, gui) -> display.apply(list.get(i), gui));
    }

    public static <T> void createList(MinionsGui parent, Component title, IdMap<T> list, BiFunction<T, PaginatedList, GuiElementBuilder> display) {
        new PaginatedList(parent, title, list.size(), (i, gui) -> display.apply(list.byId(i), gui));
    }

    public static <T> CompletableFuture<T> createListFuture(MinionsGui parent, Component title, List<T> list, Function<T, GuiElementBuilder> display) {
        CompletableFuture<T> future = new CompletableFuture<>();
        new PaginatedList(parent, title, list.size(), (i, me) -> display.apply(list.get(i))
                .setCallback(() -> {
                    future.complete(list.get(i));
                    me.goBack();
                })
        );
        return future;
    }

    private void addItems() {
        int slot = 9;
        for(int i = 36 * page; i < Math.min(36 * (page + 1), size); i++) {
            gui.setSlot(slot, display.apply(i, this));
            slot++;
        }

        if(page > 0) {
            gui.setSlot(48, new GuiElementBuilder(Items.SPECTRAL_ARROW)
                    .setItemName(Component.translatable("book.page_button.previous"))
                    .setCallback(() -> {
                        page--;
                        addItems();
                    })
            );
        } else {
            gui.clearSlot(48);
        }

        if(27 * (page + 1) < size) {
            gui.setSlot(50, new GuiElementBuilder(Items.ARROW)
                    .setItemName(Component.translatable("book.page_button.next"))
                    .setCallback(() -> {
                        page++;
                        addItems();
                    })
            );
        } else {
            gui.clearSlot(50);
        }
    }
}

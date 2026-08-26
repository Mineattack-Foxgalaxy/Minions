package io.github.skippyall.minions.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.core.IdMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

public class PaginatedList extends MinionsGui {
    private static final int ITEMS_PER_PAGE = 45;

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
        addItemsAndNavigation();
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
        new PaginatedList(parent, title, list.size(), (i, gui) -> display.apply(Objects.requireNonNull(list.byId(i)), gui));
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

    public static <T> CompletableFuture<T> createListFuture(MinionsGui parent, Component title, IdMap<T> list, Function<T, GuiElementBuilder> display) {
        CompletableFuture<T> future = new CompletableFuture<>();
        new PaginatedList(parent, title, list.size(), (i, me) -> display.apply(Objects.requireNonNull(list.byId(i)))
                .setCallback(() -> {
                    future.complete(Objects.requireNonNull(list.byId(i)));
                    me.goBack();
                })
        );
        return future;
    }

    private void addItemsAndNavigation() {
        int slot = 9;
        for(int registryIndex = firstItemOf(page); registryIndex < Math.min(lastItemOf(page), size); registryIndex++) {
            gui.setSlot(slot, display.apply(registryIndex, this));
            slot++;
        }

        if(page > 0) {
            gui.setSlot(3, new GuiElementBuilder(Items.SPECTRAL_ARROW)
                    .setItemName(Component.translatable("book.page_button.previous"))
                    .setCallback(() -> {
                        page--;
                        addItemsAndNavigation();
                    })
            );
        } else {
            gui.clearSlot(3);
        }

        if(lastItemOf(page) < size && size > ITEMS_PER_PAGE) {
            gui.setSlot(5, new GuiElementBuilder(Items.ARROW)
                    .setItemName(Component.translatable("book.page_button.next"))
                    .setCallback(() -> {
                        page++;
                        addItemsAndNavigation();
                    })
            );
        } else {
            gui.clearSlot(5);
        }
    }

    private static int firstItemOf(int page) {
        return ITEMS_PER_PAGE * page;
    }

    private static int lastItemOf(int page) {
        return firstItemOf(page + 1) - 1;
    }
}

package io.github.skippyall.minions.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import net.minecraft.util.collection.IndexedIterable;

import java.util.List;
import java.util.function.BiFunction;

public class PaginatedList extends MinionsGui {
    private int page = 0;
    private SimpleGui gui;
    private final Text title;
    private final int size;
    private final BiFunction<Integer, PaginatedList, GuiElementBuilder> display;

    public PaginatedList(MinionsGui parent, Text title, int size, BiFunction<Integer, PaginatedList, GuiElementBuilder> display) {
        super(parent);
        this.title = title;
        this.size = size;
        this.display = display;
        open();
    }

    @Override
    protected void open() {
        gui = new SimpleGui(ScreenHandlerType.GENERIC_9X4, viewer, false) {
            @Override
            public void onClose() {
                onBackingClosed();
            }
        };
        gui.setTitle(title);
        addItems();
        gui.open();
    }

    @Override
    protected void closeBacking() {
        gui.close();
    }

    public static <T> void createList(MinionsGui parent, Text title, List<T> list, BiFunction<T, PaginatedList, GuiElementBuilder> display) {
        new PaginatedList(parent, title, list.size(), (i, gui) -> display.apply(list.get(i), gui));
    }

    public static <T> void createList(MinionsGui parent, Text title, IndexedIterable<T> list, BiFunction<T, PaginatedList, GuiElementBuilder> display) {
        new PaginatedList(parent, title, list.size(), (i, gui) -> display.apply(list.get(i), gui));
    }

    private void addItems() {
        for(int i = 27 * page; i < Math.min(27 * (page + 1), size); i++) {
            gui.addSlot(display.apply(i, this));
        }

        if(page > 0) {
            gui.setSlot(30, new GuiElementBuilder(Items.SPECTRAL_ARROW)
                    .setItemName(Text.translatable("book.page_button.previous"))
                    .setCallback(() -> {
                        page--;
                        addItems();
                    })
            );
        } else {
            gui.clearSlot(30);
        }

        if(27 * (page + 1) < size) {
            gui.setSlot(32, new GuiElementBuilder(Items.ARROW)
                    .setItemName(Text.translatable("book.page_button.next"))
                    .setCallback(() -> {
                        page++;
                        addItems();
                    })
            );
        } else {
            gui.clearSlot(32);
        }
    }
}

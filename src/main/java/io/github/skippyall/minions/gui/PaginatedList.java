package io.github.skippyall.minions.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SlotGuiInterface;
import io.github.skippyall.minions.gui.minion.MinionGui;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.collection.IndexedIterable;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class PaginatedList {
    public static void createList(SlotGuiInterface gui, int size, Function<Integer, GuiElementBuilder> display, Runnable onBack) {
        AtomicInteger page = new AtomicInteger(0);
        addItems(page, gui, size, display);

        gui.setSlot(27, MinionGui.backButton(onBack));
    }

    public static <T> void createList(SlotGuiInterface gui, List<T> list, Function<T, GuiElementBuilder> display, Runnable onBack) {
        createList(gui, list.size(), i -> display.apply(list.get(i)), onBack);
    }

    public static <T> void createList(SlotGuiInterface gui, IndexedIterable<T> list, Function<T, GuiElementBuilder> display, Runnable onBack) {
        createList(gui, list.size(), i -> display.apply(list.get(i)), onBack);
    }

    private static void addItems(AtomicInteger page, SlotGuiInterface gui, int size, Function<Integer, GuiElementBuilder> display) {
        for(int i = 27 * page.get(); i < Math.min(27 * (page.get() + 1), size); i++) {
            gui.addSlot(display.apply(i));
        }

        if(page.get() > 0) {
            gui.setSlot(30, new GuiElementBuilder(Items.SPECTRAL_ARROW)
                    .setItemName(Text.translatable("book.page_button.previous"))
                    .setCallback(() -> {
                        page.decrementAndGet();
                        addItems(page, gui, size, display);
                    })
            );
        } else {
            gui.clearSlot(30);
        }

        if(27 * (page.get() + 1) < size) {
            gui.setSlot(32, new GuiElementBuilder(Items.ARROW)
                    .setItemName(Text.translatable("book.page_button.next"))
                    .setCallback(() -> {
                        page.incrementAndGet();
                        addItems(page, gui, size, display);
                    })
            );
        } else {
            gui.clearSlot(32);
        }
    }
}

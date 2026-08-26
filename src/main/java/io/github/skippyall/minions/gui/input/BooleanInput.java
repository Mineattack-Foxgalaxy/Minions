package io.github.skippyall.minions.gui.input;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.gui.minion.SimpleMinionsGui;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class BooleanInput {
    public static CompletableFuture<Boolean> confirm(
            MinionsGui parent,
            Component title,
            Component falseText,
            Component trueText
    ) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        new SimpleMinionsGui(parent, (onClose, me) -> {
            SimpleGui gui = new SimpleGui(MenuType.GENERIC_3x3, parent.viewer, false) {
                @Override
                public void onPlayerClose(boolean success) {
                    future.complete(false);
                    onClose.run();
                }
            };
            gui.setTitle(title);

            gui.setSlot(3, new GuiElementBuilder(Items.REDSTONE_BLOCK)
                    .setName(falseText)
                    .setCallback(() -> {
                        future.complete(false);
                       me.goBack();
                    })
            );

            gui.setSlot(5, new GuiElementBuilder(Items.EMERALD_BLOCK)
                    .setName(trueText)
                    .setCallback(() -> {
                        future.complete(true);
                        me.goBack();
                    })
            );

            gui.open();
            return gui;
        });
        return future;
    }

    public static CompletableFuture<Boolean> confirm(
            MinionsGui parent,
            Component title
    ) {
        return confirm(parent, title, Component.translatable("minions.gui.abort"), Component.translatable("minions.gui.confirm"));
    }
}

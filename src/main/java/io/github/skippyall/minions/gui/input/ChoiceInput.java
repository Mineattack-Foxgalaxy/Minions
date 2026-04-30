package io.github.skippyall.minions.gui.input;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.gui.Displayable;
import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.gui.minion.SimpleMinionsGui;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ChoiceInput {
    public static <T> BiFunction<ServerPlayer, T, CompletableFuture<T>> createDialogOpener(MenuType<?> screen, Component title, Function<T, GuiDisplay> displayFunction, T[] values, @Nullable T fallback) {
        return (player, object) -> {
            CompletableFuture<T> future = new CompletableFuture<>();

            SimpleGui gui = new SimpleGui(screen, player, false) {
                @Override
                public void onPlayerClose(boolean success) {
                    if(fallback == null) {
                        future.cancel(false);
                    } else {
                        future.complete(fallback);
                    }
                }
            };

            gui.setTitle(title);

            for(T value : values) {
                gui.addSlot(new GuiElementBuilder(displayFunction.apply(value).createItemStack())
                        .setCallback(() -> future.complete(value))
                );
            }
            gui.open();
            return future;
        };
    }

    public static <T extends Displayable> BiFunction<ServerPlayer, T, CompletableFuture<T>> createDialogOpener(T[] values) {
        return createDialogOpener(MenuType.GENERIC_9x3, Component.empty(), t -> t != null ? t.getDisplay() : null, values, null);
    }

    public static CompletableFuture<Void> confirm(ServerPlayer player, Component title) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        SimpleGui gui = new SimpleGui(MenuType.GENERIC_3x3, player, false) {
            @Override
            public void onPlayerClose(boolean success) {
                future.cancel(false);
            }
        };

        gui.setTitle(title);

        gui.setSlot(3, new GuiElementBuilder(Items.REDSTONE_BLOCK)
                .setName(Component.translatable("minions.gui.abort"))
                .setCallback(() -> future.cancel(false))
        );

        gui.setSlot(5, new GuiElementBuilder(Items.EMERALD_BLOCK)
                .setName(Component.translatable("minions.gui.confirm"))
                .setCallback(() -> future.complete(null))
        );

        gui.open();
        return future;
    }

    public static CompletableFuture<Boolean> confirm(MinionsGui parent, Component title) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        new SimpleMinionsGui(parent, (onClose, me) -> {
            SimpleGui gui = new SimpleGui(MenuType.GENERIC_3x3, parent.getViewer(), false) {
                @Override
                public void onPlayerClose(boolean success) {
                    future.complete(false);
                    onClose.run();
                }
            };

            gui.setTitle(title);

            gui.setSlot(3, new GuiElementBuilder(Items.REDSTONE_BLOCK)
                    .setName(Component.translatable("minions.gui.abort"))
                    .setCallback(() -> {
                        future.complete(false);
                        me.goBack();
                    })
            );

            gui.setSlot(5, new GuiElementBuilder(Items.EMERALD_BLOCK)
                    .setName(Component.translatable("minions.gui.confirm"))
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

    public static BiFunction<ServerPlayer, Boolean, CompletableFuture<Boolean>> inputBoolean(Component title) {
        return createDialogOpener(MenuType.GENERIC_3x3, title, value -> {
            if(value) {
                return new GuiDisplay.ItemBased(Items.EMERALD_BLOCK);
            } else {
                return new GuiDisplay.ItemBased(Items.REDSTONE_BLOCK);
            }
        }, new Boolean[]{false, true}, false);
    }
}

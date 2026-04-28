package io.github.skippyall.minions.gui.input;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.gui.Displayable;
import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.gui.minion.SimpleMinionsGui;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ChoiceInput {
    public static <T> BiFunction<ServerPlayerEntity, T, CompletableFuture<T>> createDialogOpener(ScreenHandlerType<?> screen, Text title, Function<T, GuiDisplay> displayFunction, T[] values, @Nullable T fallback) {
        return (player, object) -> {
            CompletableFuture<T> future = new CompletableFuture<>();

            SimpleGui gui = new SimpleGui(screen, player, false) {
                @Override
                public void onClose() {
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

    public static <T extends Displayable> BiFunction<ServerPlayerEntity, T, CompletableFuture<T>> createDialogOpener(T[] values) {
        return createDialogOpener(ScreenHandlerType.GENERIC_9X3, Text.empty(), t -> t != null ? t.getDisplay() : null, values, null);
    }

    public static CompletableFuture<Void> confirm(ServerPlayerEntity player, Text title) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_3X3, player, false) {
            @Override
            public void onClose() {
                future.cancel(false);
            }
        };

        gui.setTitle(title);

        gui.setSlot(3, new GuiElementBuilder(Items.REDSTONE_BLOCK)
                .setName(Text.translatable("minions.gui.abort"))
                .setCallback(() -> future.cancel(false))
        );

        gui.setSlot(5, new GuiElementBuilder(Items.EMERALD_BLOCK)
                .setName(Text.translatable("minions.gui.confirm"))
                .setCallback(() -> future.complete(null))
        );

        gui.open();
        return future;
    }

    public static CompletableFuture<Void> confirm(MinionsGui parent, Text title) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        new SimpleMinionsGui(parent, (onClose, me) -> {
            SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_3X3, parent.getViewer(), false) {
                @Override
                public void onClose() {
                    future.cancel(false);
                    onClose.run();
                }
            };

            gui.setTitle(title);

            gui.setSlot(3, new GuiElementBuilder(Items.REDSTONE_BLOCK)
                    .setName(Text.translatable("minions.gui.abort"))
                    .setCallback(() -> future.cancel(false))
            );

            gui.setSlot(5, new GuiElementBuilder(Items.EMERALD_BLOCK)
                    .setName(Text.translatable("minions.gui.confirm"))
                    .setCallback(() -> future.complete(null))
            );

            gui.open();
            return gui;
        });
        return future;
    }

    public static BiFunction<ServerPlayerEntity, Boolean, CompletableFuture<Boolean>> inputBoolean(Text title) {
        return createDialogOpener(ScreenHandlerType.GENERIC_3X3, title, value -> {
            if(value) {
                return new GuiDisplay.ItemBased(Items.EMERALD_BLOCK);
            } else {
                return new GuiDisplay.ItemBased(Items.REDSTONE_BLOCK);
            }
        }, new Boolean[]{false, true}, false);
    }
}

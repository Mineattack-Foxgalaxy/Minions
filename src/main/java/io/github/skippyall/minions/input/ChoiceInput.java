package io.github.skippyall.minions.input;

import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.gui.Displayable;
import io.github.skippyall.minions.gui.GuiDisplay;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ChoiceInput {
    public static <T> BiFunction<ServerPlayerEntity, T, CompletableFuture<T>> createDialogOpener(Function<T, GuiDisplay> displayFunction, T[] values) {
        return (player, object) -> {
            CompletableFuture<T> future = new CompletableFuture<>();

            SimpleGui gui = new SimpleGui(ScreenHandlerType.GENERIC_9X3, player, false) {
                @Override
                public void onClose() {
                    future.cancel(false);
                }
            };
            for(T value : values) {
                gui.addSlot(displayFunction.apply(value).createElement()
                        .setCallback(() -> future.complete(value))
                );
            }
            gui.open();
            return future;
        };
    }

    public static <T extends Displayable> BiFunction<ServerPlayerEntity, T, CompletableFuture<T>> createDialogOpener(T[] values) {
        return createDialogOpener(Displayable::getDisplay, values);
    }
}

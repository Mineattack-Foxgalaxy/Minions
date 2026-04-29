package io.github.skippyall.minions.gui.input;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.gui.minion.SimpleMinionsGui;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class TextInput<T> extends AnvilInputGui {
    private final GuiElementBuilder valid = new GuiElementBuilder()
            .setItem(Items.EMERALD_BLOCK)
            .setName(Component.literal("OK"))
            .setCallback(this::onConfirm);

    private final GuiElementBuilder invalid = new GuiElementBuilder()
            .setItem(Items.REDSTONE_BLOCK);
    private final Function<String, CompletableFuture<Result<T, Component>>> parser;
    private final CompletableFuture<T> future;
    private Result<T, Component> result;
    private boolean isConfirm;

    public TextInput(ServerPlayer player, Component title, String defaultValue, Function<String, CompletableFuture<Result<T, Component>>> parser, CompletableFuture<T> future) {
        super(player, false);
        setTitle(title);
        setDefaultInputValue(defaultValue);
        this.parser = parser;
        this.future = future;

        updateConfirmButton(defaultValue);
    }

    public static <T> CompletableFuture<T> inputSync(ServerPlayer player, Component title, String defaultValue, Function<String, Result<T, Component>> parser) {
        return input(player, title, defaultValue, (String string) -> CompletableFuture.completedFuture(parser.apply(string)));
    }

    public static <T> CompletableFuture<T> inputSync(MinionsGui gui, Component title, String defaultValue, Function<String, Result<T, Component>> parser) {
        return input(gui, title, defaultValue, (String string) -> CompletableFuture.completedFuture(parser.apply(string)));
    }

    public static <T> CompletableFuture<T> input(ServerPlayer player, Component title, String defaultValue, Function<String, CompletableFuture<Result<T, Component>>> parser) {
        CompletableFuture<T> future = new CompletableFuture<>();
        new TextInput<>(player, title, defaultValue, parser, future).open();
        return future;
    }

    public static <T> CompletableFuture<T> input(MinionsGui gui, Component title, String defaultValue, Function<String, CompletableFuture<Result<T, Component>>> parser) {
        CompletableFuture<T> future = new CompletableFuture<>();
        new SimpleMinionsGui(gui, (onClose, me) -> {
            TextInput<T> input = new TextInput<>(gui.getViewer(), title, defaultValue, parser, future);
            future.handle((v, e) -> {
                onClose.run();
                return null;
            });
            input.open();
            return input;
        });
        return future;
    }

    public static CompletableFuture<String> inputString(ServerPlayer player, Component title, String defaultValue) {
        return inputSync(player, title, defaultValue, Result.Success::new);
    }

    public static CompletableFuture<String> inputString(MinionsGui gui, Component title, String defaultValue) {
        return inputSync(gui, title, defaultValue, Result.Success::new);
    }

    public static CompletableFuture<Long> inputLong(ServerPlayer player, Component title, String defaultValue) {
        return inputSync(player, title, defaultValue, string -> Result.wrapCustomError(() -> Long.valueOf(string), Component.translatable("minions.command.input.int.fail")));
    }

    public static CompletableFuture<Long> inputLong(MinionsGui gui, Component title, String defaultValue) {
        return inputSync(gui, title, defaultValue, string -> Result.wrapCustomError(() -> Long.valueOf(string), Component.translatable("minions.command.input.int.fail")));
    }

    public static CompletableFuture<Double> inputDouble(ServerPlayer player, Component title, String defaultValue) {
        return inputSync(player, title, defaultValue, string -> Result.wrapCustomError(() -> Double.valueOf(string), Component.translatable("minions.command.input.float.fail")));
    }

    public static CompletableFuture<Double> inputDouble(MinionsGui gui, Component title, String defaultValue) {
        return inputSync(gui, title, defaultValue, string -> Result.wrapCustomError(() -> Double.valueOf(string), Component.translatable("minions.command.input.float.fail")));
    }

    @Override
    public void onInput(String input) {
        updateConfirmButton(input);
    }

    public void updateConfirmButton(String input) {
        parser.apply(input).thenAccept(result -> {
            this.result = result;
            if(result.isSuccess()) {
                setSlot(AnvilMenu.RESULT_SLOT, valid);
            } else {
                Component text = result.getErrorOrThrow();
                setSlot(AnvilMenu.RESULT_SLOT, invalid.setName(text));
            }
        });
    }

    @Override
    public void onPlayerClose(boolean success) {
        if(!future.isDone() && !isConfirm) {
            future.cancel(false);
        }
    }

    public void onConfirm() {
        if(result != null) {
            result.ifSuccess(success -> {
                isConfirm = true;
                close();
                future.complete(success);
            });
        }
    }
}

package io.github.skippyall.minions.gui.input;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import io.github.skippyall.minions.gui.MinionsGui;
import io.github.skippyall.minions.gui.minion.SimpleMinionsGui;
import net.minecraft.item.Items;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class TextInput<T> extends AnvilInputGui {
    private final GuiElementBuilder valid = new GuiElementBuilder()
            .setItem(Items.EMERALD_BLOCK)
            .setName(Text.literal("OK"))
            .setCallback(this::onConfirm);

    private final GuiElementBuilder invalid = new GuiElementBuilder()
            .setItem(Items.REDSTONE_BLOCK);
    private final Function<String, CompletableFuture<Result<T, Text>>> parser;
    private final CompletableFuture<T> future;
    private Result<T, Text> result;
    private boolean isConfirm;

    public TextInput(ServerPlayerEntity player, Text title, String defaultValue, Function<String, CompletableFuture<Result<T, Text>>> parser, CompletableFuture<T> future) {
        super(player, false);
        setTitle(title);
        setDefaultInputValue(defaultValue);
        this.parser = parser;
        this.future = future;

        updateConfirmButton(defaultValue);
    }

    public static <T> CompletableFuture<T> inputSync(ServerPlayerEntity player, Text title, String defaultValue, Function<String, Result<T, Text>> parser) {
        return input(player, title, defaultValue, (String string) -> CompletableFuture.completedFuture(parser.apply(string)));
    }

    public static <T> CompletableFuture<T> inputSync(MinionsGui gui, Text title, String defaultValue, Function<String, Result<T, Text>> parser) {
        return input(gui, title, defaultValue, (String string) -> CompletableFuture.completedFuture(parser.apply(string)));
    }

    public static <T> CompletableFuture<T> input(ServerPlayerEntity player, Text title, String defaultValue, Function<String, CompletableFuture<Result<T, Text>>> parser) {
        CompletableFuture<T> future = new CompletableFuture<>();
        new TextInput<>(player, title, defaultValue, parser, future).open();
        return future;
    }

    public static <T> CompletableFuture<T> input(MinionsGui gui, Text title, String defaultValue, Function<String, CompletableFuture<Result<T, Text>>> parser) {
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

    public static CompletableFuture<String> inputString(ServerPlayerEntity player, Text title, String defaultValue) {
        return inputSync(player, title, defaultValue, Result.Success::new);
    }

    public static CompletableFuture<String> inputString(MinionsGui gui, Text title, String defaultValue) {
        return inputSync(gui, title, defaultValue, Result.Success::new);
    }

    public static CompletableFuture<Long> inputLong(ServerPlayerEntity player, Text title, String defaultValue) {
        return inputSync(player, title, defaultValue, string -> Result.wrapCustomError(() -> Long.valueOf(string), Text.translatable("minions.command.input.int.fail")));
    }

    public static CompletableFuture<Long> inputLong(MinionsGui gui, Text title, String defaultValue) {
        return inputSync(gui, title, defaultValue, string -> Result.wrapCustomError(() -> Long.valueOf(string), Text.translatable("minions.command.input.int.fail")));
    }

    public static CompletableFuture<Double> inputDouble(ServerPlayerEntity player, Text title, String defaultValue) {
        return inputSync(player, title, defaultValue, string -> Result.wrapCustomError(() -> Double.valueOf(string), Text.translatable("minions.command.input.float.fail")));
    }

    public static CompletableFuture<Double> inputDouble(MinionsGui gui, Text title, String defaultValue) {
        return inputSync(gui, title, defaultValue, string -> Result.wrapCustomError(() -> Double.valueOf(string), Text.translatable("minions.command.input.float.fail")));
    }

    @Override
    public void onInput(String input) {
        updateConfirmButton(input);
    }

    public void updateConfirmButton(String input) {
        parser.apply(input).thenAccept(result -> {
            this.result = result;
            if(result.isSuccess()) {
                setSlot(AnvilScreenHandler.OUTPUT_ID, valid);
            } else {
                Text text = result.getErrorOrThrow();
                setSlot(AnvilScreenHandler.OUTPUT_ID, invalid.setName(text));
            }
        });
    }

    @Override
    public void onClose() {
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

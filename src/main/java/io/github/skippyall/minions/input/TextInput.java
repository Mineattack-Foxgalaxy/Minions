package io.github.skippyall.minions.input;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.AnvilInputGui;
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
    private final Function<String, Result<T, Text>> parser;
    private final CompletableFuture<T> future;
    private Result<T, Text> result;

    public TextInput(ServerPlayerEntity player, Text title, String defaultValue, Function<String, Result<T, Text>> parser, CompletableFuture<T> future) {
        super(player, false);
        setTitle(title);
        setDefaultInputValue(defaultValue);
        this.parser = parser;
        this.future = future;

        updateConfirmButton(defaultValue);
    }

    public static <T> CompletableFuture<T> input(ServerPlayerEntity player, Text title, String defaultValue, Function<String, Result<T, Text>> parser) {
        CompletableFuture<T> future = new CompletableFuture<>();
        new TextInput<>(player, title, defaultValue, parser, future).open();
        return future;
    }

    public static CompletableFuture<Integer> inputInt(ServerPlayerEntity player, Text title, String defaultValue) {
        return input(player, title, defaultValue, string -> Result.wrapCustomError(() -> Integer.valueOf(string), Text.translatable("minions.command.input.int.fail")));
    }

    public static CompletableFuture<Float> inputFloat(ServerPlayerEntity player, Text title, String defaultValue) {
        return input(player, title, defaultValue, string -> Result.wrapCustomError(() -> Float.valueOf(string), Text.translatable("minions.command.input.float.fail")));
    }

    @Override
    public void onInput(String input) {
        updateConfirmButton(input);
    }

    public void updateConfirmButton(String input) {
        result = parser.apply(input);
        if(result.isSuccess()) {
            setSlot(AnvilScreenHandler.OUTPUT_ID, valid);
        } else {
            Text text = result.getErrorOrThrow();
            setSlot(AnvilScreenHandler.OUTPUT_ID, invalid.setName(text));
        }
    }

    @Override
    public void onClose() {
        if(!future.isDone()) {
            future.cancel(false);
        }
    }

    public void onConfirm() {
        result.ifSuccess(success -> {
            future.complete(success);
            close();
        });
    }
}

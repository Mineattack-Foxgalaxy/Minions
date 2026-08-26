package io.github.skippyall.minions.gui.input;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import io.github.skippyall.minions.gui.MinionsGui;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class TextInput<T> extends MinionsGui {
    private final GuiElementBuilder valid = new GuiElementBuilder()
        .setItem(Items.EMERALD_BLOCK)
        .setName(Component.literal("OK"))
        .setCallback(this::onConfirm);

    private final GuiElementBuilder invalid = new GuiElementBuilder()
        .setItem(Items.REDSTONE_BLOCK);

    private final Component title;
    private final String defaultValue;
    private final Function<String, CompletableFuture<Result<T, Component>>> parser;

    private AnvilInputGui gui;

    private @Nullable Result<T, Component> result = null;
    private CompletableFuture<@Nullable T> future = new CompletableFuture<>();

    public TextInput(
            MinionsGui parent,
            Component title,
            String defaultValue,
            Function<String, CompletableFuture<Result<T, Component>>> parser
    ) {
        this.title = title;
        this.defaultValue = defaultValue;
        this.parser = parser;

        super(parent);
        open();
    }

    @Override
    public void open() {
        gui = new AnvilInputGui(viewer, false) {
            @Override
            public void onInput(String input) {
                updateConfirmButton(input);
            }

            @Override
            public void onPlayerClose(boolean success) {
                onBackingClosed();
                if (!future.isDone()) {
                    future.complete(null);
                }
            }
        };

        gui.setTitle(title);
        gui.setDefaultInputValue(defaultValue);
        updateConfirmButton(defaultValue);
        gui.open();
    }

    @Override
    public void closeBacking() {
        gui.close();
    }

    public void updateConfirmButton(String input) {
        parser.apply(input).thenAccept(result -> {
            this.result = result;
            if (result.isSuccess()) {
                gui.setSlot(AnvilMenu.RESULT_SLOT, valid);
            } else {
                Component text = result.getErrorOrThrow();
                gui.setSlot(AnvilMenu.RESULT_SLOT, invalid.setName(text));
            }
        });
    }

    public void onConfirm() {
        if(result != null && result instanceof Result.Success<T, Component> success) {
            future.complete(success.result());
            goBack();
        }
    }

    public static <T> CompletableFuture<@Nullable T> input(
            MinionsGui gui,
            Component title,
            String defaultValue,
            Function<String, CompletableFuture<Result<T, Component>>> parser
    ) {
        TextInput<T> input = new TextInput<>(
                gui,
                title,
                defaultValue,
                parser
        );

        return input.future;
    }

    public static CompletableFuture<@Nullable String> inputString(
            MinionsGui gui,
            Component title,
            String defaultValue
    ) {
        return input(
                gui,
                title,
                defaultValue,
                result -> CompletableFuture.completedFuture(new Result.Success<>(result))
        );
    }

    public static CompletableFuture<@Nullable Long> inputLong(
            MinionsGui gui,
            Component title,
            long defaultValue
    ) {
        return input(
                gui,
                title,
                Long.toString(defaultValue),
                string -> CompletableFuture.completedFuture(Result.wrapCustomError(
                        () -> Long.parseLong(string),
                        Component.translatable("value_type.minions.long.not_long")
                ))
        );
    }

    public static CompletableFuture<@Nullable Double> inputDouble(
            MinionsGui gui,
            Component title,
            double defaultValue
    ) {
        return input(
                gui,
                title,
                Double.toString(defaultValue),
                string -> CompletableFuture.completedFuture(Result.wrapCustomError(
                        () -> Double.parseDouble(string),
                        Component.translatable("value_type.minions.double.not_double")
                ))
        );
    }
}

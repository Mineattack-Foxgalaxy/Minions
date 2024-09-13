package io.github.skippyall.minions.input;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import eu.pb4.sgui.api.gui.AnvilInputGui;
import net.minecraft.item.Items;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;

public class TextInput {
    public static CompletableFuture<String> inputText(ServerPlayerEntity player, Text title, String defaultText) {
        CompletableFuture<String> future = new CompletableFuture<>();

        AnvilInputGui gui = new AnvilInputGui(player, false);
        gui.setSlot(AnvilScreenHandler.OUTPUT_ID, new GuiElementBuilder()
                .setItem(Items.EMERALD_BLOCK)
                .setName(Text.literal("OK"))
                .setCallback(() -> future.complete(gui.getInput()))
        );
        gui.setTitle(title);
        gui.setDefaultInputValue(defaultText);
        gui.open();
        return future;
    }
}

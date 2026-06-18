package io.github.skippyall.minions.gui.minion;

import eu.pb4.sgui.api.gui.GuiLike;
import io.github.skippyall.minions.gui.MinionsGui;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;

public class SimpleMinionsGui extends MinionsGui {
    private GuiLike gui;
    private final BiFunction<Runnable, SimpleMinionsGui, GuiLike> guiFactory;

    public SimpleMinionsGui(MinionsGui parent, BiFunction<Runnable, SimpleMinionsGui, GuiLike> guiFactory) {
        this(parent.viewer, parent, guiFactory);
    }

    public SimpleMinionsGui(ServerPlayer player, @Nullable MinionsGui parent, BiFunction<Runnable, SimpleMinionsGui, GuiLike> guiFactory) {
        super(player, parent);
        this.guiFactory = guiFactory;
        open();
    }

    @Override
    protected void open() {
        gui = guiFactory.apply(this::onBackingClosed, this);
    }

    @Override
    protected void closeBacking() {
        gui.close();
    }
}

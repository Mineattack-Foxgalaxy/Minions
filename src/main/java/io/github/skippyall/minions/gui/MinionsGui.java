package io.github.skippyall.minions.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import org.jspecify.annotations.Nullable;

public abstract class MinionsGui {
    protected final @Nullable MinionsGui parent;
    public final ServerPlayer viewer;
    protected @Nullable MinionsGui child;
    private boolean open = true;

    public MinionsGui(ServerPlayer viewer, @Nullable MinionsGui parent) {
        this.viewer = viewer;
        this.parent = parent;
        if(parent != null) {
            parent.child = this;
        }
    }

    public MinionsGui(MinionsGui parent) {
        this(parent.viewer, parent);
    }

    public MinionsGui(ServerPlayer viewer) {
        this(viewer, null);
    }

    protected abstract void open();

    protected void reopen() {
        open();
    }

    public void onBackingClosed() {
        if (child != null && child.open) {
            return;
        }

        close(true);
    }

    public void close(boolean alreadyClosed) {
        if (open) {
            open = false;
            if (child != null) {
                child.close(alreadyClosed);
            } else if (!alreadyClosed) {
                closeBacking();
            }
        }
    }

    public void close() {
        close(false);
    }

    public void goBack() {
        if (parent != null) {
            open = false;
            parent.child = null;
            parent.reopen();
            close(true);
        } else {
            close(false);
        }
    }

    public GuiElementBuilder backButton() {
        return new GuiElementBuilder(Items.MANGROVE_DOOR)
                .setName(Component.translatable("gui.back"))
                .setCallback(this::goBack);
    }

    protected abstract void closeBacking();
}

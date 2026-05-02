package io.github.skippyall.minions.gui;

import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public abstract class MinionsGui {
    protected final @Nullable MinionsGui parent;
    protected final ServerPlayer viewer;
    protected @Nullable MinionsGui child = null;
    private boolean open = true;

    public MinionsGui(MinionsGui parent) {
        this.viewer = parent.viewer;
        this.parent = parent;
        parent.child = this;
    }

    public MinionsGui(ServerPlayer viewer) {
        this.viewer = viewer;
        this.parent = null;
    }

    public ServerPlayer getViewer() {
        return viewer;
    }

    protected abstract void open();

    protected void reopen() {
        open();
    }

    public void onBackingClosed() {
        if(child != null && child.open) {
            return;
        }

        close(true);
    }

    public void close() {
        close(false);
    }

    public void close(boolean alreadyClosed) {
        if(open) {
            open = false;
            if(child != null) {
                child.close(alreadyClosed);
            } else if(!alreadyClosed) {
                closeBacking();
            }
        }
    }

    public void goBack() {
        if(parent != null) {
            open = false;
            parent.child = null;
            parent.reopen();
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

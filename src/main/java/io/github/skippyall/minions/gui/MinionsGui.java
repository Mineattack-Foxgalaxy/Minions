package io.github.skippyall.minions.gui;

import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public abstract class MinionsGui {
    protected final @Nullable MinionsGui parent;
    protected final ServerPlayerEntity viewer;
    private @Nullable MinionsGui child = null;
    private boolean open = true;

    public MinionsGui(MinionsGui parent) {
        this.viewer = parent.viewer;
        this.parent = parent;
        parent.child = this;
        open();
    }

    public MinionsGui(ServerPlayerEntity viewer) {
        this.viewer = viewer;
        this.parent = null;
        open();
    }

    protected void open() {}

    protected void reopen() {
        open();
    }

    public void onBackingClosed() {
        if(child != null && child.open) {
            return;
        }

        close();
    }

    public void close() {
        if(open) {
            if(child != null) {
                child.close();
            }
            if(parent != null) {
                parent.child = null;
                parent.reopen();
            }
            onClose();
            open = false;
        }
    }

    protected void onClose() {}
}

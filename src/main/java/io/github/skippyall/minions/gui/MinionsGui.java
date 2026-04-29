package io.github.skippyall.minions.gui;

import net.minecraft.server.level.ServerPlayer;
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

        close();
    }

    public void close() {
        if(open) {
            if(parent != null) {
                parent.child = null;
                parent.reopen();
            }
            closeNoOpen(parent == null);
        }
    }

    private void closeNoOpen(boolean closeBacking) {
        if(child != null) {
            child.closeNoOpen(closeBacking);
        }
        if(parent != null) {
            parent.child = null;
        }
        if(closeBacking) {
            closeBacking();
        }
        open = false;
    }

    protected abstract void closeBacking();
}

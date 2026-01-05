package io.github.skippyall.minions.gui;

import eu.pb4.sgui.api.gui.SimpleGui;
import io.github.skippyall.minions.minion.MinionListener;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;

public class MinionBoundSimpleGui extends SimpleGui implements MinionListener {
    protected final MinionFakePlayer minion;

    public MinionBoundSimpleGui(ScreenHandlerType<?> type, ServerPlayerEntity player, MinionFakePlayer minion) {
        super(type, player, false);
        this.minion = minion;
        minion.addMinionListener(this);
    }

    public MinionFakePlayer getMinion() {
        return minion;
    }

    @Override
    public void onMinionRemove(MinionFakePlayer minion) {
        close();
    }

    @Override
    public void onClose() {
        minion.removeMinionListener(this);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MinionBoundSimpleGui that)) return false;
        return minion == that.minion && player == that.player;
    }
}

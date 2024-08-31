package io.github.skippyall.minions.program.runtime;

import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;

public class MinionRuntime extends ProgramRuntime {
    private final MinionFakePlayer minion;

    public MinionRuntime(MinionFakePlayer minion) {
        this.minion = minion;
    }

    public MinionFakePlayer getMinion() {
        return minion;
    }
}

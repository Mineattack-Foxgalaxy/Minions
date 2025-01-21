package io.github.skippyall.minions.program.runtime;

import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;

public class MinionRuntime extends ProgramRuntime {
    private final MinionFakePlayer minion;

    public MinionRuntime(MinionFakePlayer minion) {
        this.minion = minion;
    }

    public MinionFakePlayer getMinion() {
        return minion;
    }
}

package io.github.skippyall.minions.registration;

import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.Context;

public class ExecutionContext {
    public static final Context.Key<MinionFakePlayer> MINION_KEY = new Context.Key<>(Minions.id("minion"));
}

package io.github.skippyall.minions.module;

import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;

public interface SpecialAbility {
    default void onAdd(MinionFakePlayer minion) {}

    default void onRemove(MinionFakePlayer minion) {}

    default void tick(MinionFakePlayer minion) {}
}

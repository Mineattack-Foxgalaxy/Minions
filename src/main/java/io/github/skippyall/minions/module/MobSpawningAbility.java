package io.github.skippyall.minions.module;

import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;

public class MobSpawningAbility implements SpecialAbility {
    @Override
    public void onAdd(MinionFakePlayer minion) {
        SpecialAbility.super.onAdd(minion);
    }

    @Override
    public void onRemove(MinionFakePlayer minion) {
        SpecialAbility.super.onRemove(minion);
    }
}

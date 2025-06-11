package io.github.skippyall.minions.new_program.argument;

import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.new_program.value.ValueType;

public interface Argument<T> {
    T resolve(MinionFakePlayer minion);
    ValueType<T> getType();
}

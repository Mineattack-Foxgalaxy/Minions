package io.github.skippyall.minions.program.value;

import java.util.Collection;

public record TypeRestriction<T>(ValueType<T> type, Collection<RestrictionRule<T>> rules) {
}

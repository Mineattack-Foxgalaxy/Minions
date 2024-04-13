package io.github.skippyall.minions.program.statement;

import io.github.skippyall.minions.program.variables.Type;

public interface Arg {
    Object getValue();
    Type getType();
}

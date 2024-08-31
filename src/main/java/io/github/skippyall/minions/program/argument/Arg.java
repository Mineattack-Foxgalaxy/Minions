package io.github.skippyall.minions.program.argument;

import io.github.skippyall.minions.program.runtime.ProgramRuntime;
import io.github.skippyall.minions.program.variables.Type;

public interface Arg<T> {
    T resolve(ProgramRuntime runtime);
    Type<T> getType();
}

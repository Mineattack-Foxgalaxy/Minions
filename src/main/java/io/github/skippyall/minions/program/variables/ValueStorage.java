package io.github.skippyall.minions.program.variables;

import io.github.skippyall.minions.program.runtime.ProgramRuntime;

public interface ValueStorage<T> {
    void storeValue(T value, ProgramRuntime runtime);
}

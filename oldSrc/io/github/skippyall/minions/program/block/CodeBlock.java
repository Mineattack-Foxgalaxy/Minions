package io.github.skippyall.minions.program.block;

import io.github.skippyall.minions.program.variables.Type;

import java.util.List;

public abstract class CodeBlock {
    public CodeBlock(String name, List<Type> arguments) {
    }

    public abstract Object execute(Object... args);
}

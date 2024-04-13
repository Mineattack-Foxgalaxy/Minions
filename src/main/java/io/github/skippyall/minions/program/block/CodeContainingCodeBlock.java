package io.github.skippyall.minions.program.block;

import io.github.skippyall.minions.program.variables.Type;

import java.util.List;

public abstract class CodeContainingCodeBlock extends CodeBlock{
    public CodeContainingCodeBlock(String name, List<Type> arguments, Type returnType) {
        super(name, arguments, returnType);
    }

    protected void executeBlocks() {

    }
}

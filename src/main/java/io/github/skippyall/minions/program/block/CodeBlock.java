package io.github.skippyall.minions.program.block;

import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.statement.Arg;
import io.github.skippyall.minions.program.variables.Type;

import java.util.List;

public abstract class CodeBlock {
    private String name;
    private final List<Type> arguments;
    private final Type returnType;
    public CodeBlock(String name, List<Type> arguments, Type returnType) {
        this.arguments = arguments;
        this.returnType = returnType;
    }

    public abstract Object execute(MinionFakePlayer minion, Object... args);

    public boolean fits(Arg arg, int slot) {
        return arguments.get(slot) == arg.getType();
    }
}

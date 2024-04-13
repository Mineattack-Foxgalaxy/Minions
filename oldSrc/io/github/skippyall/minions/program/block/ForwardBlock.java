package io.github.skippyall.minions.program.block;

import io.github.skippyall.minions.program.variables.IntegerType;
import io.github.skippyall.minions.program.variables.Type;
import io.github.skippyall.minions.program.variables.Types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ForwardBlock extends CodeBlock{
    public ForwardBlock() {
        super("forward", Arrays.asList(Types.INTEGER));
    }

    public Object execute(Object... args) {
        return null;
    }
}

package io.github.skippyall.minions.program.block;

import io.github.skippyall.minions.program.argument.Arg;
import io.github.skippyall.minions.program.runtime.ProgramRuntime;
import io.github.skippyall.minions.program.statement.Statement;
import io.github.skippyall.minions.program.tuple.Tuple;
import io.github.skippyall.minions.program.variables.Type;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

public abstract class CodeBlock<R, A extends Tuple> {
    private String name;
    private final List<Type<?>> arguments;
    private final Type<?> returnType;

    public CodeBlock(String name, List<Type<?>> arguments, Type<?> returnType) {
        this.arguments = arguments;
        this.returnType = returnType;
    }

    protected abstract R execute(ProgramRuntime runtime, A args, Statement<R,A>.Run run);

    public boolean fits(Arg<?> arg, int slot) {
        return arguments.get(slot) == arg.getType();
    }

    public void start(ProgramRuntime runtime, A args, Statement<R,A>.Run run) {
        ForkJoinPool.commonPool().execute(() -> {
            R result = execute(runtime, args, run);
            run.afterRun(result);
        });
    }
}

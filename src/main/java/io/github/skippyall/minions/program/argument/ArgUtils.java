package io.github.skippyall.minions.program.argument;

import io.github.skippyall.minions.program.runtime.ProgramRuntime;
import io.github.skippyall.minions.program.tuple.Tuple;
import io.github.skippyall.minions.program.tuple.Tuple0;

public class ArgUtils {
    public static Tuple resolveArgs(Tuple args, ProgramRuntime runtime) {
        Tuple tuple = new Tuple0();
        for(Object object : args) {
            Arg<?> arg =(Arg<?>) object;
            tuple = tuple.add(arg.getType().cast(arg.resolve(runtime)));
        }
        return tuple;
    }
}

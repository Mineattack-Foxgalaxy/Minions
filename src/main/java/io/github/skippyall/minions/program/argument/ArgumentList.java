package io.github.skippyall.minions.program.argument;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.program.InstructionRuntime;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ArgumentList<R extends InstructionRuntime<R>> {
    private final Map<String, Argument<?, R>> arguments;

    public ArgumentList() {
        arguments = new HashMap<>();
    }

    public ArgumentList(Map<String, Argument<?,R>> arguments) {
        this.arguments = new HashMap<>(arguments);
    }

    public <T> T getValue(Parameter<T> parameter, R runtime) {
        Argument<T,R> argument = getArgument(parameter);
        return argument != null ? argument.resolve(runtime) : null;
    }

    public <T, A extends Argument<T,R>> A getArgument(Parameter<T> parameter) {
        Argument<?,R> argument = arguments.get(parameter.name());
        return argument == null ? null : argument.cast(parameter.type());
    }

    public <T> void setArgument(Parameter<T> parameter, Argument<T,R> argument) {
        arguments.put(parameter.name(), argument);
    }

    public boolean hasArgumentFor(Parameter<?> parameter) {
        return getArgument(parameter) != null;
    }

    public boolean hasArgumentForAll(Collection<Parameter<?>> checkParameters) {
        for(Parameter<?> parameter : checkParameters) {
            if(!hasArgumentFor(parameter)) {
                return false;
            }
        }
        return true;
    }

    public static <R extends InstructionRuntime<R>> Codec<ArgumentList<R>> getCodec(Codec<ArgumentType<R>> genericCodec) {
        return Codec.unboundedMap(Codec.STRING, Arguments.createArgumentCodec(genericCodec))
                .xmap(ArgumentList::new, list -> list.arguments);
    }
}

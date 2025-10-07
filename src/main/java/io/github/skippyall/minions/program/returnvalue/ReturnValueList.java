package io.github.skippyall.minions.program.returnvalue;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.argument.Argument;
import io.github.skippyall.minions.program.argument.Arguments;
import io.github.skippyall.minions.program.argument.GenericArgumentType;
import io.github.skippyall.minions.program.argument.Parameter;
import io.github.skippyall.minions.program.argument.SpecificArgumentType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ReturnValueList<R extends InstructionRuntime<R>> {
    private final Map<String, ValueConsumer<?, ?, R>> arguments;

    public ReturnValueList() {
        arguments = new HashMap<>();
    }

    public ReturnValueList(Map<String, ValueConsumer<?,?, R>> arguments) {
        this.arguments = new HashMap<>(arguments);
    }

    public <T> T getValue(Parameter<T> parameter, R runtime) {
        Argument<T,?,R> argument = getArgument(parameter);
        return argument != null ? argument.resolve(runtime) : null;
    }

    public <T, A extends Argument<T, ? extends SpecificArgumentType<T,A,R>,R>> A getArgument(Parameter<T> parameter) {
        Argument<?, ?,R> argument = arguments.get(parameter.name());
        return argument == null ? null : argument.cast(parameter.type());
    }

    public <T> void setArgument(Parameter<T> parameter, Argument<T,?,R> argument) {
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

    public static <R extends InstructionRuntime<R>> Codec<ReturnValueList<R>> getCodec(Codec<GenericArgumentType<R>> genericCodec) {
        return Codec.unboundedMap(Codec.STRING, Arguments.createArgumentCodec(genericCodec))
                .xmap(ReturnValueList::new, list -> list.arguments);
    }
}

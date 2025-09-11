package io.github.skippyall.minions.new_program.argument;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ArgumentList {
    public static final Codec<ArgumentList> CODEC = Codec.unboundedMap(Codec.STRING, Arguments.ARGUMENT_CODEC)
            .xmap(ArgumentList::new, list -> list.arguments);

    private final Map<String, Argument<?, ?>> arguments;

    public ArgumentList() {
        arguments = new HashMap<>();
    }

    public ArgumentList(Map<String, Argument<?,?>> arguments) {
        this.arguments = new HashMap<>(arguments);
    }

    public <T> T getValue(Parameter<T> parameter, MinionFakePlayer minion) {
        Argument<T,?> argument = getArgument(parameter);
        return argument != null ? argument.resolve(minion) : null;
    }

    public <T, A extends Argument<T, ? extends SpecificArgumentType<T,A>>> A getArgument(Parameter<T> parameter) {
        Argument<?, ?> argument = arguments.get(parameter.name());
        return argument == null ? null : argument.cast(parameter.type());
    }

    public <T> void setArgument(Parameter<T> parameter, Argument<T,?> argument) {
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
}

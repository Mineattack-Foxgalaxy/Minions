package io.github.skippyall.minions.program.handler;

import io.github.skippyall.minions.program.instruction.InstructionType;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public abstract class ValueHandlerList<H, E extends ConfiguredValueHandler<?, H>> {
    protected final Map<Parameter<?>, E> arguments = new HashMap<>();
    protected final List<Consumer<Parameter<?>>> changeListeners = new ArrayList<>();

    public ValueHandlerList() {

    }

    private ValueHandlerList(List<E> arguments) {
        for(E argument : arguments) {
            this.arguments.put(argument.getParameter(), argument);
        }
    }

    private List<E> toCodecList() {
        return List.copyOf(arguments.values());
    }

    public @Nullable H getHandler(Parameter<?> parameter) {
        if(arguments.containsKey(parameter)) {
            return arguments.get(parameter).getHandler();
        } else {
            return null;
        }
    }

    @Nullable
    public <P> ConfiguredValueHandler<P, H> getEntry(Parameter<P> parameter) {
        //noinspection unchecked
        return (ConfiguredValueHandler<P, H>) arguments.get(parameter);
    }

    public abstract <P> ConfiguredValueHandler<P, H> createEntry(Parameter<P> parameter, H handler);

    public void removeEntry(Parameter<?> parameter) {
        arguments.remove(parameter);
    }

    public boolean hasArgumentFor(Parameter<?> parameter) {
        return arguments.containsKey(parameter);
    }

    public void checkHasArguments(Collection<Parameter<?>> checkParameters, Consumer<Component> errorConsumer) {
        for(Parameter<?> parameter : checkParameters) {
            if(!hasArgumentFor(parameter)) {
                errorConsumer.accept(Component.translatable("minions.gui.instruction.check.argument_not_set", parameter.name()));
            }
        }
    }

    public void checkRun(InstructionType instructionType, Consumer<Component> errorConsumer) {
        checkHasArguments(instructionType.getParameters(), errorConsumer);

        for(E entry : arguments.values()) {
            entry.check(errorConsumer);
        }
    }

    private void onChange(Parameter<?> parameter) {
        for (Consumer<Parameter<?>> listener : changeListeners) {
            listener.accept(parameter);
        }
    }

    public void addListener(Consumer<Parameter<?>> listener) {
        changeListeners.add(listener);
    }

    public void removeListener(Consumer<Parameter<?>> listener) {
        changeListeners.remove(listener);
    }
}

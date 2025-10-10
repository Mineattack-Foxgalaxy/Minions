package io.github.skippyall.minions.program.instruction;

import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.argument.Parameter;
import io.github.skippyall.minions.program.argument.ArgumentList;
import io.github.skippyall.minions.program.value.ValueType;
import net.minecraft.storage.ReadView;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class InstructionType<R extends InstructionRuntime<R>> {
    private final GuiDisplay display;
    private final Collection<Parameter<?>> parameters;
    private final Supplier<InstructionExecution<R>> executionFactory;

    private InstructionType(GuiDisplay display, Supplier<InstructionExecution<R>> executionFactory, Collection<Parameter<?>> parameters) {
        this.display = display;
        this.parameters = parameters;
        this.executionFactory = executionFactory;
    }

    public static <R extends InstructionRuntime<R>> InstructionType<R> create(GuiDisplay display, Supplier<InstructionExecution<R>> executionFactory, Collection<Parameter<?>> parameters) {
        return new InstructionType<>(display, executionFactory, List.copyOf(parameters));
    }

    public static <Return,R extends InstructionRuntime<R>> InstructionType<R> create(GuiDisplay display, Supplier<InstructionExecution<R>> executionFactory, ValueType<Return> returnType, Parameter<?>... parameters) {
        return new InstructionType<>(display, executionFactory, List.of(parameters));
    }

    public Collection<Parameter<?>> getParameters() {
        return parameters;
    }

    public GuiDisplay getDisplay() {
        return display;
    }

    public InstructionExecution<R> createExecution(ArgumentList<R> parameters, R minion) {
        InstructionExecution<R> execution = executionFactory.get();
        execution.readArguments(parameters, minion);
        return execution;
    }

    public InstructionExecution<R> loadExecution(ReadView view, R minion) {
        InstructionExecution<R> execution = executionFactory.get();
        execution.load(view, minion);
        return execution;
    }
}

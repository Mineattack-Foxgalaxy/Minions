package io.github.skippyall.minions.new_program.instruction;

import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.new_program.argument.Parameter;
import io.github.skippyall.minions.new_program.argument.ArgumentList;
import io.github.skippyall.minions.new_program.value.ValueType;
import net.minecraft.storage.ReadView;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public final class InstructionType<R> {
    private final GuiDisplay display;
    private final Collection<Parameter<?>> parameters;
    private final ValueType<R> returnType;
    private final Supplier<InstructionExecution<R>> executionFactory;

    private InstructionType(GuiDisplay display, Supplier<InstructionExecution<R>> executionFactory, ValueType<R> returnType, Collection<Parameter<?>> parameters) {
        this.display = display;
        this.parameters = parameters;
        this.returnType = returnType;
        this.executionFactory = executionFactory;
    }

    public static <R> InstructionType<R> create(GuiDisplay display, Supplier<InstructionExecution<R>> executionFactory, ValueType<R> returnType, Collection<Parameter<?>> parameters) {
        return new InstructionType<>(display, executionFactory, returnType, List.copyOf(parameters));
    }

    public static <R> InstructionType<R> create(GuiDisplay display, Supplier<InstructionExecution<R>> executionFactory, ValueType<R> returnType, Parameter<?>... parameters) {
        return new InstructionType<>(display, executionFactory, returnType, List.of(parameters));
    }

    public Collection<Parameter<?>> getParameters() {
        return parameters;
    }

    public ValueType<R> getReturnType() {
        return returnType;
    }

    public GuiDisplay getDisplay() {
        return display;
    }

    public InstructionExecution<R> createExecution(ArgumentList parameters, MinionFakePlayer minion) {
        InstructionExecution<R> execution = executionFactory.get();
        execution.readArguments(parameters, minion);
        return execution;
    }

    public InstructionExecution<R> loadExecution(ReadView view, MinionFakePlayer minion) {
        InstructionExecution<R> execution = executionFactory.get();
        execution.load(view, minion);
        return execution;
    }
}

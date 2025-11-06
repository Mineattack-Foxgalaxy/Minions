package io.github.skippyall.minions.program.instruction;

import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.supplier.Parameter;
import io.github.skippyall.minions.program.supplier.ValueSupplierList;
import net.minecraft.storage.ReadView;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class InstructionType<R extends InstructionRuntime<R>> {
    private final GuiDisplay display;
    private final Collection<Parameter<?>> parameters;
    private final Collection<Parameter<?>> returnParameters;
    private final Supplier<InstructionExecution<R>> executionFactory;

    public InstructionType(GuiDisplay display, Supplier<InstructionExecution<R>> executionFactory, Collection<Parameter<?>> parameters, Collection<Parameter<?>> returnParameters) {
        this.display = display;
        this.parameters = List.copyOf(parameters);
        this.returnParameters = List.copyOf(returnParameters);
        this.executionFactory = executionFactory;
    }

    public Collection<Parameter<?>> getParameters() {
        return parameters;
    }

    public Collection<Parameter<?>> getReturnParameters() {
        return returnParameters;
    }

    public GuiDisplay getDisplay() {
        return display;
    }

    public InstructionExecution<R> createExecution(ValueSupplierList<R> parameters, R minion) {
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

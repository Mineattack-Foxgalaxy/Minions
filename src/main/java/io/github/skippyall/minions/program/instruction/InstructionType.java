package io.github.skippyall.minions.program.instruction;

import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.supplier.Parameter;
import io.github.skippyall.minions.program.supplier.ValueSupplierList;
import net.minecraft.storage.ReadView;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * Defines the semantics of an instruction and creates {@link InstructionExecution}
 * InstructionTypes for minions can be registered to {@link io.github.skippyall.minions.registration.MinionRegistries#INSTRUCTION_TYPES}
 * @param <R> The runtime that this instruction can be executed in
 */
public class InstructionType<R extends InstructionRuntime<R>> {
    private final List<Parameter<?>> parameters;
    private final List<Parameter<?>> returnParameters;
    private final Supplier<InstructionExecution<R>> executionFactory;

    public InstructionType(Supplier<InstructionExecution<R>> executionFactory, Collection<Parameter<?>> parameters, Collection<Parameter<?>> returnParameters) {
        this.parameters = List.copyOf(parameters);
        this.returnParameters = List.copyOf(returnParameters);
        this.executionFactory = executionFactory;
    }

    public List<Parameter<?>> getParameters() {
        return parameters;
    }

    public List<Parameter<?>> getReturnParameters() {
        return returnParameters;
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

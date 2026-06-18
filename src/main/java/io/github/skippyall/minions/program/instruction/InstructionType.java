package io.github.skippyall.minions.program.instruction;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.supplier.Parameter;
import io.github.skippyall.minions.program.supplier.ParameterValueList;
import io.github.skippyall.minions.registration.MinionRegistries;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * Defines the semantics of an instruction and creates {@link InstructionExecution}
 * InstructionTypes can be registered to {@link MinionRegistries#INSTRUCTION_TYPES}
 * @param <R> The runtime that this instruction can be executed in
 */
public class InstructionType<R extends InstructionRuntime<R>> {
    private final List<Parameter<?>> parameters;
    private final List<Parameter<?>> returnParameters;
    private final Supplier<InstructionExecution<R>> executionFactory;

    private final Codec<? extends InstructionExecution<R>> executionCodec;

    public InstructionType(Supplier<InstructionExecution<R>> executionFactory, Collection<Parameter<?>> parameters, Collection<Parameter<?>> returnParameters, Codec<? extends InstructionExecution<R>> executionCodec) {
        this.parameters = List.copyOf(parameters);
        this.returnParameters = List.copyOf(returnParameters);
        this.executionFactory = executionFactory;
        this.executionCodec = executionCodec;
    }

    public Codec<? extends InstructionExecution<R>> getExecutionCodec() {
        return executionCodec;
    }

    public List<Parameter<?>> getParameters() {
        return parameters;
    }

    public List<Parameter<?>> getReturnParameters() {
        return returnParameters;
    }

    public InstructionExecution<R> createExecution(ParameterValueList arguments, R minion) {
        InstructionExecution<R> execution = executionFactory.get();
        execution.readArguments(arguments, minion);
        return execution;
    }
}

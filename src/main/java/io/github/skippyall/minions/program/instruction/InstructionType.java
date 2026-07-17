package io.github.skippyall.minions.program.instruction;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.handler.Parameter;
import io.github.skippyall.minions.program.handler.ParameterValueList;
import io.github.skippyall.minions.registration.MinionRegistries;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

/**
 * Defines the semantics of an instruction and creates {@link InstructionExecution}
 * InstructionTypes can be registered to {@link MinionRegistries#INSTRUCTION_TYPES}
 */
public class InstructionType {
    private final List<Parameter<?>> parameters;
    private final List<Parameter<?>> returnParameters;
    private final List<Context.Key<?>> contextKeys;
    private final Supplier<InstructionExecution> executionFactory;

    private final Codec<? extends InstructionExecution> executionCodec;

    public InstructionType(Supplier<InstructionExecution> executionFactory, Collection<Parameter<?>> parameters, Collection<Parameter<?>> returnParameters, Collection<Context.Key<?>> contextKeys, Codec<? extends InstructionExecution> executionCodec) {
        this.parameters = List.copyOf(parameters);
        this.returnParameters = List.copyOf(returnParameters);
        this.contextKeys = List.copyOf(contextKeys);
        this.executionFactory = executionFactory;
        this.executionCodec = executionCodec;
    }

    public Codec<? extends InstructionExecution> getExecutionCodec() {
        return executionCodec;
    }

    public List<Parameter<?>> getParameters() {
        return parameters;
    }

    public List<Parameter<?>> getReturnParameters() {
        return returnParameters;
    }

    public List<Context.Key<?>> getContextKeys() {
        return contextKeys;
    }

    public InstructionExecution createExecution(ParameterValueList arguments, Context minion) {
        InstructionExecution execution = executionFactory.get();
        execution.readArguments(arguments, minion);
        return execution;
    }
}

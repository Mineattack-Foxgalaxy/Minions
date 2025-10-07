package io.github.skippyall.minions.program.instruction;

import io.github.skippyall.minions.gui.GuiDisplay;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.argument.Parameter;
import io.github.skippyall.minions.program.argument.ArgumentList;
import io.github.skippyall.minions.program.value.ValueType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.recipe.Recipe;
import net.minecraft.storage.ReadView;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class InstructionType<Return, R extends InstructionRuntime<R>> {
    private final GuiDisplay display;
    private final Collection<Parameter<?>> parameters;
    private final ValueType<Return> returnType;
    private final Supplier<InstructionExecution<Return,R>> executionFactory;

    private InstructionType(GuiDisplay display, Supplier<InstructionExecution<Return,R>> executionFactory, ValueType<Return> returnType, Collection<Parameter<?>> parameters) {
        this.display = display;
        this.parameters = parameters;
        this.returnType = returnType;
        this.executionFactory = executionFactory;
    }

    public static <Return,R extends InstructionRuntime<R>> InstructionType<Return,R> create(GuiDisplay display, Supplier<InstructionExecution<Return,R>> executionFactory, ValueType<Return> returnType, Collection<Parameter<?>> parameters) {
        return new InstructionType<>(display, executionFactory, returnType, List.copyOf(parameters));
    }

    public static <Return,R extends InstructionRuntime<R>> InstructionType<Return,R> create(GuiDisplay display, Supplier<InstructionExecution<Return,R>> executionFactory, ValueType<Return> returnType, Parameter<?>... parameters) {
        return new InstructionType<>(display, executionFactory, returnType, List.of(parameters));
    }

    public Collection<Parameter<?>> getParameters() {
        return parameters;
    }

    public ValueType<Return> getReturnType() {
        return returnType;
    }

    public GuiDisplay getDisplay() {
        return display;
    }

    public InstructionExecution<Return,R> createExecution(ArgumentList<R> parameters, R minion) {
        InstructionExecution<Return,R> execution = executionFactory.get();
        execution.readArguments(parameters, minion);
        return execution;
    }

    public InstructionExecution<Return,R> loadExecution(ReadView view, R minion) {
        InstructionExecution<Return,R> execution = executionFactory.get();
        execution.load(view, minion);
        return execution;
    }
}

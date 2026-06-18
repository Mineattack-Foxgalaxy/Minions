package io.github.skippyall.minions.program.instruction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.listener.SerializableListenerManager;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.supplier.ParameterValueList;
import io.github.skippyall.minions.registration.MinionRegistries;
import net.minecraft.util.StringRepresentable;

public class ExecutingInstruction<R extends InstructionRuntime<R>> {
    public static final MapCodec<ExecutingInstruction<?>> MAP_CODEC = MinionRegistries.INSTRUCTION_TYPES.byNameCodec().dispatchMap(
            "instruction",
            ExecutingInstruction::getInstructionType,
            ExecutingInstruction::codecHelper
    );

    private final InstructionType<? super R> instructionType;
    private final InstructionExecution<? super R> execution;
    private State state;

    private final SerializableListenerManager<Listener> listeners;

    public ExecutingInstruction(InstructionType<? super R> instructionType, InstructionExecution<? super R> execution) {
        this(instructionType, execution, State.EXECUTING, new SerializableListenerManager<>());
    }

    private ExecutingInstruction(InstructionType<? super R> instructionType, InstructionExecution<? super R> execution, State state, SerializableListenerManager<Listener> listeners) {
        this.instructionType = instructionType;
        this.execution = execution;
        this.state = state;
        this.listeners = listeners;
    }

    public InstructionType<? super R> getInstructionType() {
        return instructionType;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void tick(R minion) {
        if(state.isCurrentlyExecuting()) {
            if(execution.isDone(minion)) {
                stop(minion);
            } else {
                execution.tick(minion);
                if (execution.isDone(minion)) {
                    stop(minion);
                }
            }
        }
    }

    public void stop(R runtime) {
        ParameterValueList list = new ParameterValueList();
        execution.stop(list, runtime);
        state = State.STOPPED;
        listeners.forEach(listener -> listener.onStop(runtime, list));
    }

    public void addListener(Listener listener) {
        listeners.addListener(listener);
    }

    public void removeListener(Listener listener) {
        listeners.removeListener(listener);
    }

    private static <R extends InstructionRuntime<R>> MapCodec<ExecutingInstruction<R>> codecHelper(InstructionType<R> instructionType) {
        //TODO use checked superclass codec instead of unchecked cast
        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ((Codec<InstructionExecution<? super R>>) (Codec<?>) instructionType.getExecutionCodec()).fieldOf("execution").forGetter(i -> i.execution),
                        State.CODEC.fieldOf("state").forGetter(ExecutingInstruction::getState),
                        SerializableListenerManager.getCodec(MinionRegistries.EXECUTING_INSTRUCTION_LISTENER_CODECS).fieldOf("listeners").forGetter(i -> i.listeners)
                ).apply(
                        instance,
                        (execution, state, listeners) ->
                                new ExecutingInstruction<R>(instructionType, execution, state, listeners)
                ));
    }

    public enum State implements StringRepresentable {
        EXECUTING("executing"),
        UNLOADED("unloaded"),
        STOPPED("stopped");

        public static final Codec<State> CODEC = StringRepresentable.fromEnum(State::values);

        private final String serialName;

        State(String serialName) {
            this.serialName = serialName;
        }
        public boolean isCurrentlyExecuting() {
            return this == EXECUTING;
        }

        @Override
        public String getSerializedName() {
            return serialName;
        }
    }

    public interface Listener extends SerializableListenerManager.SerializableListener {
        default void onStop(InstructionRuntime<?> runtime, ParameterValueList returnValues) {}
    }
}

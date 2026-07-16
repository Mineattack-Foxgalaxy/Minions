package io.github.skippyall.minions.program.instruction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.listener.SerializableListenerManager;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.InstructionRuntime;
import io.github.skippyall.minions.program.supplier.ParameterValueList;
import io.github.skippyall.minions.registration.MinionRegistries;
import net.minecraft.util.StringRepresentable;

public class ExecutingInstruction {
    public static final MapCodec<ExecutingInstruction> MAP_CODEC = MinionRegistries.INSTRUCTION_TYPES.byNameCodec().dispatchMap(
            "instruction",
            ExecutingInstruction::getInstructionType,
            ExecutingInstruction::codecHelper
    );

    private final InstructionType instructionType;
    private final InstructionExecution execution;

    private State state;

    private final SerializableListenerManager<Listener> listeners;

    public ExecutingInstruction(InstructionType instructionType, InstructionExecution execution) {
        this(instructionType, execution, State.EXECUTING, new SerializableListenerManager<>());
    }

    private ExecutingInstruction(InstructionType instructionType, InstructionExecution execution, State state, SerializableListenerManager<Listener> listeners) {
        this.instructionType = instructionType;
        this.execution = execution;
        this.state = state;
        this.listeners = listeners;
    }

    public InstructionType getInstructionType() {
        return instructionType;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void onLoaded(InstructionRuntime runtime) {
        if(state == State.UNLOADED) {
            setState(State.EXECUTING);
        }
    }

    public void onUnloaded() {
        if(state == State.EXECUTING) {
            setState(State.UNLOADED);
        }
    }

    public void tick(Context context) {
        if(state.isCurrentlyExecuting()) {
            if(execution.isDone(context)) {
                stop(context);
            } else {
                execution.tick(context);
                if (execution.isDone(context)) {
                    stop(context);
                }
            }
        } else if(state == State.STOPPING) {
            stop(context);
        }
    }

    public void scheduleStop() {
        setState(State.STOPPING);
    }

    public void stop(Context context) {
        ParameterValueList list = new ParameterValueList();
        execution.stop(list, context);
        state = State.STOPPED;
        listeners.forEach(listener -> listener.onStop(context, list));
    }

    public void addListener(Listener listener) {
        listeners.addListener(listener);
    }

    public void removeListener(Listener listener) {
        listeners.removeListener(listener);
    }

    private static MapCodec<ExecutingInstruction> codecHelper(InstructionType instructionType) {
        //TODO use checked superclass codec instead of unchecked cast
        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ((Codec<InstructionExecution>) instructionType.getExecutionCodec()).fieldOf("execution").forGetter(i -> i.execution),
                        State.CODEC.fieldOf("state").forGetter(ExecutingInstruction::getState),
                        SerializableListenerManager.getCodec(MinionRegistries.EXECUTING_INSTRUCTION_LISTENER_CODECS).fieldOf("listeners").forGetter(i -> i.listeners)
                ).apply(
                        instance,
                        (execution, state, listeners) ->
                                new ExecutingInstruction(instructionType, execution, state, listeners)
                ));
    }

    public enum State implements StringRepresentable {
        EXECUTING("executing"),
        UNLOADED("unloaded"),
        STOPPING("stopping"),
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
        default void onStop(Context context, ParameterValueList returnValues) {}
    }
}

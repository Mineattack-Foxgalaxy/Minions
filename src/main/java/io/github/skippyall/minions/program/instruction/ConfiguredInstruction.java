package io.github.skippyall.minions.program.instruction;

import io.github.skippyall.minions.MinionRegistries;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.argument.ArgumentList;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.jetbrains.annotations.Nullable;

public class ConfiguredInstruction<Return,R> {
    private final InstructionType<Return,R> instruction;
    private final ArgumentList<R> arguments;
    private @Nullable InstructionExecution<Return, R> execution;
    private final String name;

    private ConfiguredInstruction(InstructionType<Return,R> instruction, ArgumentList<R> arguments, @Nullable InstructionExecution<Return,R> execution, String name) {
        this.instruction = instruction;
        this.arguments = arguments;
        this.execution = execution;
        this.name = name;
    }

    public ConfiguredInstruction(InstructionType<Return,R> instruction, String name) {
        this(instruction, new ArgumentList<>(), null, name);
    }

    public InstructionType<Return,R> getInstruction() {
        return instruction;
    }

    public ArgumentList<R> getArguments() {
        return arguments;
    }

    public String getName() {
        return name;
    }

    public boolean canRun() {
        return instruction != null && arguments != null && arguments.hasArgumentForAll(instruction.getParameters());
    }

    public boolean isRunning() {
        return execution != null;
    }

    public @Nullable InstructionExecution<Return,R> getExecution() {
        return execution;
    }

    public void run(R minion) {
        if(canRun() && !isRunning()) {
            try {
                execution = instruction.createExecution(arguments, minion);
                execution.start(minion);
            } catch (Exception e) {
                Minions.LOGGER.error("An error occurred while executing configured Instruction {} of minion {}", name, minion.getGameProfile().getName(), e);

            }
        }
    }

    public void tick(R minion) {
        if(isRunning()) {
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

    public void stop(R minion) {
        if(isRunning()) {
            execution.stop(minion);
            execution = null;
        }
    }

    public void save(WriteView view, R minion) {
        view.put("instruction", MinionRegistries.INSTRUCTION_TYPES.getCodec(), instruction);
        view.put("arguments", ArgumentList.CODEC, arguments);
        view.putBoolean("running", isRunning());
        if(isRunning()) {
            execution.save(view.get("execution"), minion);
        }
    }

    public static <Return,R> ConfiguredInstruction<Return,R> load(ReadView view, R minion, String name) {
        //noinspection unchecked
        InstructionType<Return,R> instructionType = (InstructionType<Return,R>) view.read("instruction", MinionRegistries.INSTRUCTION_TYPES.getCodec()).orElseThrow();

        ArgumentList<R> arguments = view.read("arguments", ArgumentList.get).orElseThrow();

        boolean running = view.getBoolean("running", false);

        if(running) {
            ReadView executionView = view.getReadView("execution");
            try {
                InstructionExecution<Return,R> execution = instructionType.loadExecution(executionView, minion);
                return new ConfiguredInstruction<>(instructionType, arguments, execution, name);
            } catch (Exception e) {

            }
        }

        return new ConfiguredInstruction<>(instructionType, arguments, null, name);
    }
}

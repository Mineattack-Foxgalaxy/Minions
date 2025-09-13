package io.github.skippyall.minions.program.instruction;

import io.github.skippyall.minions.MinionRegistries;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.argument.ArgumentList;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.jetbrains.annotations.Nullable;

public class ConfiguredInstruction<R> {
    private final InstructionType<R> instruction;
    private final ArgumentList arguments;
    private @Nullable InstructionExecution<R> execution;
    private final String name;

    private ConfiguredInstruction(InstructionType<R> instruction, ArgumentList arguments, @Nullable InstructionExecution<R> execution, String name) {
        this.instruction = instruction;
        this.arguments = arguments;
        this.execution = execution;
        this.name = name;
    }

    public ConfiguredInstruction(InstructionType<R> instruction, String name) {
        this(instruction, new ArgumentList(), null, name);
    }

    public InstructionType<R> getInstruction() {
        return instruction;
    }

    public ArgumentList getArguments() {
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

    public @Nullable InstructionExecution<R> getExecution() {
        return execution;
    }

    public void run(MinionFakePlayer minion) {
        if(canRun() && !isRunning()) {
            try {
                execution = instruction.createExecution(arguments, minion);
                execution.start(minion);
            } catch (Exception e) {
                Minions.LOGGER.error("An error occurred while executing configured Instruction {} of minion {}", name, minion.getGameProfile().getName(), e);

            }
        }
    }

    public void tick(MinionFakePlayer minion) {
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

    public void stop(MinionFakePlayer minion) {
        if(isRunning()) {
            execution.stop(minion);
            execution = null;
        }
    }

    public void save(WriteView view, MinionFakePlayer minion) {
        view.put("instruction", MinionRegistries.INSTRUCTION_TYPES.getCodec(), instruction);
        view.put("arguments", ArgumentList.CODEC, arguments);
        view.putBoolean("running", isRunning());
        if(isRunning()) {
            execution.save(view.get("execution"), minion);
        }
    }

    public static <R> ConfiguredInstruction<R> load(ReadView view, MinionFakePlayer minion, String name) {
        //noinspection unchecked
        InstructionType<R> instructionType = (InstructionType<R>) view.read("instruction", MinionRegistries.INSTRUCTION_TYPES.getCodec()).orElseThrow();

        ArgumentList arguments = view.read("arguments", ArgumentList.CODEC).orElseThrow();

        boolean running = view.getBoolean("running", false);

        if(running) {
            ReadView executionView = view.getReadView("execution");
            try {
                InstructionExecution<R> execution = instructionType.loadExecution(executionView, minion);
                return new ConfiguredInstruction<>(instructionType, arguments, execution, name);
            } catch (Exception e) {

            }
        }

        return new ConfiguredInstruction<>(instructionType, arguments, null, name);
    }
}

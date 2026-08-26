package io.github.skippyall.minions.minion.program.instruction;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.handler.Parameter;
import io.github.skippyall.minions.program.handler.ParameterValueList;
import io.github.skippyall.minions.program.instruction.InstructionExecution;
import io.github.skippyall.minions.registration.ValueTypes;

public class EchoExecution implements InstructionExecution {
    public static final Parameter<String> MESSAGE = new Parameter<>("message", ValueTypes.STRING);
    public static final Parameter<String> ECHO = new Parameter<>("echo", ValueTypes.STRING);
    public static final Codec<EchoExecution> CODEC = Codec.STRING.xmap(EchoExecution::new, e -> e.message);

    private String message;

    public EchoExecution() {}

    public EchoExecution(String message) {
        this.message = message;
    }

    @Override
    public boolean isDone(Context context) {
        return true;
    }

    @Override
    public void stop(ParameterValueList list, Context context) {
        list.setValue(ECHO, message);
    }

    @Override
    public void readArguments(ParameterValueList arguments, Context context) {
        message = arguments.getValue(MESSAGE);
    }
}

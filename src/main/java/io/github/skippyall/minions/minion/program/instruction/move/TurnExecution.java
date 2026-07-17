package io.github.skippyall.minions.minion.program.instruction.move;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.handler.Parameter;
import io.github.skippyall.minions.program.handler.ParameterValueList;
import io.github.skippyall.minions.registration.ExecutionContext;
import io.github.skippyall.minions.registration.ValueTypes;

public class TurnExecution extends AbstractTurnExecution {
    public static final Codec<TurnExecution> CODEC = getCodec(TurnExecution::new);

    public static final Parameter<Double> ANGLE = new Parameter<>("maxAngle", ValueTypes.DOUBLE);
    public static final Parameter<TurnDirection> DIRECTION = new Parameter<>("direction", ValueTypes.TURN_DIRECTION);

    public TurnExecution() {}

    public TurnExecution(float targetYaw, float targetPitch) {
        super(targetYaw, targetPitch);
    }

    @Override
    public void readArguments(ParameterValueList arguments, Context context) {
        MinionFakePlayer minion = context.getOrThrow(ExecutionContext.MINION_KEY);

        float maxAngle = arguments.getValue(ANGLE).floatValue();
        TurnDirection direction = arguments.getValue(DIRECTION);

        float turnYaw = maxAngle * direction.xFactor;
        float turnPitch = maxAngle * direction.yFactor;

        targetYaw = minion.getYRot() + turnYaw;
        targetPitch = minion.getXRot() + turnPitch;
    }
}

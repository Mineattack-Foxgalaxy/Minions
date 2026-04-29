package io.github.skippyall.minions.minion.program.instruction.move;

import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.program.supplier.Parameter;
import io.github.skippyall.minions.program.supplier.ParameterValueList;
import io.github.skippyall.minions.registration.ValueTypes;

public class TurnExecution extends AbstractTurnExecution {
    public static final Parameter<Double> ANGLE = new Parameter<>("maxAngle", ValueTypes.DOUBLE);
    public static final Parameter<TurnDirection> DIRECTION = new Parameter<>("direction", ValueTypes.TURN_DIRECTION);

    @Override
    public void readArguments(ParameterValueList arguments, MinionRuntime minion) {
        float maxAngle = arguments.getValue(ANGLE).floatValue();
        TurnDirection direction = arguments.getValue(DIRECTION);

        float turnYaw = maxAngle * direction.xFactor;
        float turnPitch = maxAngle * direction.yFactor;

        targetYaw = minion.getMinion().getYRot() + turnYaw;
        targetPitch = minion.getMinion().getXRot() + turnPitch;
    }
}

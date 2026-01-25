package io.github.skippyall.minions.instruction.move;

import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.program.supplier.ValueSupplierList;
import io.github.skippyall.minions.program.supplier.Parameter;
import io.github.skippyall.minions.registration.ValueTypes;

public class TurnExecution extends AbstractTurnExecution {
    public static final Parameter<Double> ANGLE = new Parameter<>("maxAngle", ValueTypes.DOUBLE);
    public static final Parameter<TurnDirection> DIRECTION = new Parameter<>("direction", ValueTypes.TURN_DIRECTION);

    @Override
    public void readArguments(ValueSupplierList<MinionRuntime> arguments, MinionRuntime minion) {
        float maxAngle = arguments.getValue(ANGLE, minion).floatValue();
        TurnDirection direction = arguments.getValue(DIRECTION, minion);

        float turnYaw = maxAngle * direction.xFactor;
        float turnPitch = maxAngle * direction.yFactor;

        targetYaw = minion.getMinion().getYaw() + turnYaw;
        targetPitch = minion.getMinion().getPitch() + turnPitch;
    }
}

package io.github.skippyall.minions.instruction.move;

import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.program.supplier.Parameter;
import io.github.skippyall.minions.program.supplier.ValueSupplierList;
import io.github.skippyall.minions.registration.ValueTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class TurnVectorExecution extends AbstractTurnExecution {
    public static final Parameter<Double> X = new Parameter<>("x", ValueTypes.DOUBLE);
    public static final Parameter<Double> Y = new Parameter<>("y", ValueTypes.DOUBLE);
    public static final Parameter<Double> Z = new Parameter<>("z", ValueTypes.DOUBLE);

    @Override
    public void readArguments(ValueSupplierList<MinionRuntime> arguments, MinionRuntime runtime) {
        double x = arguments.getValue(X, runtime);
        double y = arguments.getValue(Y, runtime);
        double z = arguments.getValue(Z, runtime);

        Vec3d vector = new Vec3d(x, y, z);
        Vec2f rotation = vectorToRotation(vector);
        targetYaw = rotation.x;
        targetPitch = rotation.y;
    }

    //copied from Entity#lookAt (why no helper, Mojang?)
    public static Vec2f vectorToRotation(Vec3d vector) {
        double g = Math.sqrt(vector.x * vector.x + vector.z * vector.z);
        float pitch = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(vector.y, g) * 180.0F / (float)Math.PI)));
        float yaw = MathHelper.wrapDegrees((float)(MathHelper.atan2(vector.z, vector.x) * 180.0F / (float)Math.PI) - 90.0F);

        return new Vec2f(yaw, pitch);
    }
}

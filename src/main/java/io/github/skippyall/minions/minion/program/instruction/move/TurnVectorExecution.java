package io.github.skippyall.minions.minion.program.instruction.move;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.handler.Parameter;
import io.github.skippyall.minions.program.handler.ParameterValueList;
import io.github.skippyall.minions.registration.ValueTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class TurnVectorExecution extends AbstractTurnExecution {
    public static final Codec<TurnVectorExecution> CODEC = getCodec(TurnVectorExecution::new);

    public static final Parameter<Double> X = new Parameter<>("x", ValueTypes.DOUBLE);
    public static final Parameter<Double> Y = new Parameter<>("y", ValueTypes.DOUBLE);
    public static final Parameter<Double> Z = new Parameter<>("z", ValueTypes.DOUBLE);

    public TurnVectorExecution() {}

    public TurnVectorExecution(float targetYaw, float targetPitch) {
        super(targetYaw, targetPitch);
    }

    @Override
    public void readArguments(ParameterValueList arguments, Context context) {
        double x = arguments.getValue(X);
        double y = arguments.getValue(Y);
        double z = arguments.getValue(Z);

        Vec3 vector = new Vec3(x, y, z);
        Vec2 rotation = vectorToRotation(vector);
        targetYaw = rotation.x;
        targetPitch = rotation.y;
    }

    //copied from Entity#lookAt (why no helper, Mojang?)
    public static Vec2 vectorToRotation(Vec3 vector) {
        double g = Math.sqrt(vector.x * vector.x + vector.z * vector.z);
        float pitch = Mth.wrapDegrees((float)(-(Mth.atan2(vector.y, g) * 180.0F / (float)Math.PI)));
        float yaw = Mth.wrapDegrees((float)(Mth.atan2(vector.z, vector.x) * 180.0F / (float)Math.PI) - 90.0F);

        return new Vec2(yaw, pitch);
    }
}

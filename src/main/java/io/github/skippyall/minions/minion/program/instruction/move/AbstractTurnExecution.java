package io.github.skippyall.minions.minion.program.instruction.move;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.instruction.InstructionExecution;
import io.github.skippyall.minions.registration.ExecutionContext;

import java.util.function.BiFunction;

public abstract class AbstractTurnExecution implements InstructionExecution {
    protected float targetYaw;
    protected float targetPitch;

    private static final float anglePerTick = 10;

    public AbstractTurnExecution() {}

    public AbstractTurnExecution(float targetYaw, float targetPitch) {
        this.targetYaw = targetYaw;
        this.targetPitch = targetPitch;
    }

    public static <E extends AbstractTurnExecution> Codec<E> getCodec(BiFunction<Float, Float, E> constructor) {
        return RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.FLOAT.fieldOf("targetYaw").forGetter(e -> e.targetYaw),
                        Codec.FLOAT.fieldOf("targetPitch").forGetter(e -> e.targetPitch)
                ).apply(instance, constructor)
        );
    }

    @Override
    public void tick(Context context) {
        MinionFakePlayer minion = context.getOrThrow(ExecutionContext.MINION_KEY);

        float rotateYaw = targetYaw - minion.getYRot();
        float rotatePitch = targetPitch - minion.getXRot();

        minion.getMinionActionPack().turn(Math.min(rotateYaw, anglePerTick), Math.min(rotatePitch, anglePerTick));
    }

    @Override
    public boolean isDone(Context context) {
        MinionFakePlayer minion = context.getOrThrow(ExecutionContext.MINION_KEY);
        return Math.abs(targetYaw - minion.getYRot()) < 0.001F && Math.abs(targetPitch - minion.getXRot()) < 0.001F;
    }
}

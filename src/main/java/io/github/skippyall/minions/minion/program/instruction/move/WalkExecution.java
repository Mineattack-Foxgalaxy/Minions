package io.github.skippyall.minions.minion.program.instruction.move;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.instruction.InstructionExecution;
import io.github.skippyall.minions.program.handler.Parameter;
import io.github.skippyall.minions.program.handler.ParameterValueList;
import io.github.skippyall.minions.registration.ExecutionContext;
import io.github.skippyall.minions.registration.ValueTypes;
import net.minecraft.world.entity.MoverType;

public class WalkExecution implements InstructionExecution {
    public static final Codec<WalkExecution> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.DOUBLE.fieldOf("totalBlocksToMove").forGetter(e -> e.totalBlocksToMove),
                    Codec.DOUBLE.fieldOf("blocksMoved").forGetter(e -> e.blocksMoved)
            ).apply(instance, WalkExecution::new));

    public static final Parameter<Double> blocksToMoveParam = new Parameter<>("blocksToMove", ValueTypes.DOUBLE);
    private static final float ACCURACY = 1F / 32F;

    private double totalBlocksToMove;
    private double blocksMoved;

    public WalkExecution() {}

    public WalkExecution(double totalBlocksToMove, double blocksMoved) {
        this.totalBlocksToMove = totalBlocksToMove;
        this.blocksMoved = blocksMoved;
    }

    @Override
    public void tick(Context context) {
        MinionFakePlayer minion = context.getOrThrow(ExecutionContext.MINION_KEY);
        double speed = Math.min(minion.getSpeed(), totalBlocksToMove - blocksMoved);
        minion.move(MoverType.SELF, minion.getDirection().getUnitVec3().normalize().scale(speed));
        blocksMoved += speed;
    }

    @Override
    public boolean isDone(Context context) {
        return totalBlocksToMove - blocksMoved < ACCURACY;
    }

    @Override
    public void readArguments(ParameterValueList parameters, Context context) {
        totalBlocksToMove = parameters.getValue(blocksToMoveParam).floatValue();
        blocksMoved = 0;
    }
}

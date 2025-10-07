package io.github.skippyall.minions.program.instruction.execution;

import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.instruction.InstructionExecution;
import io.github.skippyall.minions.program.argument.Parameter;
import io.github.skippyall.minions.program.argument.ArgumentList;
import io.github.skippyall.minions.program.value.ValueTypes;
import net.minecraft.entity.MovementType;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

public class WalkExecution implements InstructionExecution<Void,MinionFakePlayer> {
    public static final Parameter<Float> blocksToMoveParam = new Parameter<>("blocksToMove", ValueTypes.FLOAT);
    private final float ACCURACY = 1F / 32F;

    private float totalBlocksToMove;
    private float blocksMoved;

    @Override
    public void tick(MinionFakePlayer minion) {
        float speed = Math.min(minion.getMovementSpeed(), totalBlocksToMove - blocksMoved);
        minion.move(MovementType.SELF, minion.getHorizontalFacing().getDoubleVector().normalize().multiply(speed));
        blocksMoved += speed;
    }

    @Override
    public boolean isDone(MinionFakePlayer minion) {
        return totalBlocksToMove - blocksMoved < ACCURACY;
    }

    @Override
    public Void stop(MinionFakePlayer minion) {
        return null;
    }

    @Override
    public void readArguments(ArgumentList<MinionFakePlayer> parameters, MinionFakePlayer minion) {
        totalBlocksToMove = parameters.getValue(blocksToMoveParam, minion);
        blocksMoved = 0;
    }

    @Override
    public void save(WriteView view, MinionFakePlayer minion) {
        view.putFloat("totalBlocksToMove", totalBlocksToMove);
        view.putFloat("blocksMoved", blocksMoved);
    }

    @Override
    public void load(ReadView view, MinionFakePlayer minion) {
        totalBlocksToMove = view.getFloat("totalBlocksToMove", 0F);
        blocksMoved = view.getFloat("blocksMoved", 0F);
    }
}

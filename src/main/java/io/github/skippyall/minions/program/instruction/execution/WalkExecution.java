package io.github.skippyall.minions.program.instruction.execution;

import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.program.consumer.ValueConsumerList;
import io.github.skippyall.minions.program.instruction.InstructionExecution;
import io.github.skippyall.minions.program.supplier.Parameter;
import io.github.skippyall.minions.program.supplier.ValueSupplierList;
import io.github.skippyall.minions.program.value.ValueTypes;
import net.minecraft.entity.MovementType;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

public class WalkExecution implements InstructionExecution<MinionRuntime> {
    public static final Parameter<Float> blocksToMoveParam = new Parameter<>("blocksToMove", ValueTypes.FLOAT);
    private static final float ACCURACY = 1F / 32F;

    private float totalBlocksToMove;
    private float blocksMoved;

    @Override
    public void tick(MinionRuntime minion) {
        float speed = Math.min(minion.getMinion().getMovementSpeed(), totalBlocksToMove - blocksMoved);
        minion.getMinion().move(MovementType.SELF, minion.getMinion().getHorizontalFacing().getDoubleVector().normalize().multiply(speed));
        blocksMoved += speed;
    }

    @Override
    public boolean isDone(MinionRuntime minion) {
        return totalBlocksToMove - blocksMoved < ACCURACY;
    }

    @Override
    public void stop(MinionRuntime minion, ValueConsumerList<MinionRuntime> valueConsumers) {

    }

    @Override
    public void readArguments(ValueSupplierList<MinionRuntime> parameters, MinionRuntime minion) {
        totalBlocksToMove = parameters.getValue(blocksToMoveParam, minion);
        blocksMoved = 0;
    }

    @Override
    public void save(WriteView view, MinionRuntime minion) {
        view.putFloat("totalBlocksToMove", totalBlocksToMove);
        view.putFloat("blocksMoved", blocksMoved);
    }

    @Override
    public void load(ReadView view, MinionRuntime minion) {
        totalBlocksToMove = view.getFloat("totalBlocksToMove", 0F);
        blocksMoved = view.getFloat("blocksMoved", 0F);
    }
}

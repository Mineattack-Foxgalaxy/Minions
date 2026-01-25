package io.github.skippyall.minions.instruction.move;

import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.program.instruction.InstructionExecution;
import io.github.skippyall.minions.program.supplier.Parameter;
import io.github.skippyall.minions.program.supplier.ValueSupplierList;
import io.github.skippyall.minions.registration.ValueTypes;
import net.minecraft.entity.MovementType;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

public class WalkExecution implements InstructionExecution<MinionRuntime> {
    public static final Parameter<Double> blocksToMoveParam = new Parameter<>("blocksToMove", ValueTypes.DOUBLE);
    private static final float ACCURACY = 1F / 32F;

    private double totalBlocksToMove;
    private double blocksMoved;

    @Override
    public void tick(MinionRuntime minion) {
        double speed = Math.min(minion.getMinion().getMovementSpeed(), totalBlocksToMove - blocksMoved);
        minion.getMinion().move(MovementType.SELF, minion.getMinion().getHorizontalFacing().getDoubleVector().normalize().multiply(speed));
        blocksMoved += speed;
    }

    @Override
    public boolean isDone(MinionRuntime minion) {
        return totalBlocksToMove - blocksMoved < ACCURACY;
    }

    @Override
    public void readArguments(ValueSupplierList<MinionRuntime> parameters, MinionRuntime minion) {
        totalBlocksToMove = parameters.getValue(blocksToMoveParam, minion).floatValue();
        blocksMoved = 0;
    }

    @Override
    public void save(WriteView view, MinionRuntime minion) {
        view.putDouble("totalBlocksToMove", totalBlocksToMove);
        view.putDouble("blocksMoved", blocksMoved);
    }

    @Override
    public void load(ReadView view, MinionRuntime minion) {
        totalBlocksToMove = view.getDouble("totalBlocksToMove", 0F);
        blocksMoved = view.getDouble("blocksMoved", 0F);
    }
}

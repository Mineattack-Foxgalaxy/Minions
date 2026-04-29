package io.github.skippyall.minions.minion.program.instruction.move;

import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.program.instruction.InstructionExecution;
import io.github.skippyall.minions.program.supplier.Parameter;
import io.github.skippyall.minions.program.supplier.ParameterValueList;
import io.github.skippyall.minions.registration.ValueTypes;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class WalkExecution implements InstructionExecution<MinionRuntime> {
    public static final Parameter<Double> blocksToMoveParam = new Parameter<>("blocksToMove", ValueTypes.DOUBLE);
    private static final float ACCURACY = 1F / 32F;

    private double totalBlocksToMove;
    private double blocksMoved;

    @Override
    public void tick(MinionRuntime minion) {
        double speed = Math.min(minion.getMinion().getSpeed(), totalBlocksToMove - blocksMoved);
        minion.getMinion().move(MoverType.SELF, minion.getMinion().getDirection().getUnitVec3().normalize().scale(speed));
        blocksMoved += speed;
    }

    @Override
    public boolean isDone(MinionRuntime minion) {
        return totalBlocksToMove - blocksMoved < ACCURACY;
    }

    @Override
    public void readArguments(ParameterValueList parameters, MinionRuntime minion) {
        totalBlocksToMove = parameters.getValue(blocksToMoveParam).floatValue();
        blocksMoved = 0;
    }

    @Override
    public void save(ValueOutput view, MinionRuntime minion) {
        view.putDouble("totalBlocksToMove", totalBlocksToMove);
        view.putDouble("blocksMoved", blocksMoved);
    }

    @Override
    public void load(ValueInput view, MinionRuntime minion) {
        totalBlocksToMove = view.getDoubleOr("totalBlocksToMove", 0F);
        blocksMoved = view.getDoubleOr("blocksMoved", 0F);
    }
}

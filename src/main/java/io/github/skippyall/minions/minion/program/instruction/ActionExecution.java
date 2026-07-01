package io.github.skippyall.minions.minion.program.instruction;

import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.EntityPlayerActionPack;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.ExecutionContext;
import io.github.skippyall.minions.program.instruction.execution.ContinuousInstructionExecution;
import io.github.skippyall.minions.program.supplier.ParameterValueList;

public class ActionExecution implements ContinuousInstructionExecution {
    private final EntityPlayerActionPack.ActionType action;

    public ActionExecution(EntityPlayerActionPack.ActionType action) {
        this.action = action;
    }

    @Override
    public void start(ExecutionContext context) {
        MinionFakePlayer minion = context.getOrThrow(MinionRuntime.MINION_KEY);
        EntityPlayerActionPack ap = minion.getMinionActionPack();
        if(!ap.hasAction(action)) {
            minion.getMinionActionPack().start(action, EntityPlayerActionPack.Action.startContinuous());
        }
    }

    @Override
    public void stop(ParameterValueList list, ExecutionContext context) {
        MinionFakePlayer minion = context.getOrThrow(MinionRuntime.MINION_KEY);
        minion.getMinionActionPack().stop(action);
    }

    @Override
    public void pause(ExecutionContext context) {
        context.getOrThrow(MinionRuntime.MINION_KEY).getMinionActionPack().stop(action);
    }

    @Override
    public void resume(ExecutionContext context) {
        context.getOrThrow(MinionRuntime.MINION_KEY).getMinionActionPack().start(action, EntityPlayerActionPack.Action.continuous());
    }

    @Override
    public void readArguments(ParameterValueList parameters, ExecutionContext context) {}
}

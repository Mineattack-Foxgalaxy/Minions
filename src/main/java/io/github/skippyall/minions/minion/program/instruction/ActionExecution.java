package io.github.skippyall.minions.minion.program.instruction;

import io.github.skippyall.minions.minion.fakeplayer.EntityPlayerActionPack;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.instruction.InstructionExecution;
import io.github.skippyall.minions.program.handler.ParameterValueList;
import io.github.skippyall.minions.registration.ExecutionContext;

public class ActionExecution implements InstructionExecution.Continuous {
    private final EntityPlayerActionPack.ActionType action;

    public ActionExecution(EntityPlayerActionPack.ActionType action) {
        this.action = action;
    }

    @Override
    public void start(Context context) {
        MinionFakePlayer minion = context.getOrThrow(ExecutionContext.MINION_KEY);
        EntityPlayerActionPack ap = minion.getMinionActionPack();
        if(!ap.hasAction(action)) {
            minion.getMinionActionPack().start(action, EntityPlayerActionPack.Action.startContinuous());
        }
    }

    @Override
    public void stop(ParameterValueList list, Context context) {
        MinionFakePlayer minion = context.getOrThrow(ExecutionContext.MINION_KEY);
        minion.getMinionActionPack().stop(action);
    }

    @Override
    public void pause(Context context) {
        context.getOrThrow(ExecutionContext.MINION_KEY).getMinionActionPack().stop(action);
    }

    @Override
    public void resume(Context context) {
        context.getOrThrow(ExecutionContext.MINION_KEY).getMinionActionPack().start(action, EntityPlayerActionPack.Action.continuous());
    }

    @Override
    public void readArguments(ParameterValueList parameters, Context context) {}
}

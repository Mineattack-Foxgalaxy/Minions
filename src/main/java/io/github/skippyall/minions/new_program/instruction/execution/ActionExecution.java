package io.github.skippyall.minions.new_program.instruction.execution;

import io.github.skippyall.minions.minion.fakeplayer.EntityPlayerActionPack;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.new_program.argument.ArgumentList;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

public class ActionExecution implements ContinuousInstructionExecution<Void> {
    private final EntityPlayerActionPack.ActionType action;

    public ActionExecution(EntityPlayerActionPack.ActionType action) {
        this.action = action;
    }

    @Override
    public void start(MinionFakePlayer minion) {
        minion.getMinionActionPack().start(action, EntityPlayerActionPack.Action.continuous());
    }

    @Override
    public Void stop(MinionFakePlayer minion) {
        minion.getMinionActionPack().stop(action);
        return null;
    }

    @Override
    public void readArguments(ArgumentList parameters, MinionFakePlayer minion) {}

    @Override
    public void save(WriteView view, MinionFakePlayer minion) {}

    @Override
    public void load(ReadView view, MinionFakePlayer minion) {}
}

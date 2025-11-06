package io.github.skippyall.minions.program.instruction.execution;

import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.EntityPlayerActionPack;
import io.github.skippyall.minions.program.consumer.ValueConsumerList;
import io.github.skippyall.minions.program.supplier.ValueSupplierList;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

public class ActionExecution implements ContinuousInstructionExecution<MinionRuntime> {
    private final EntityPlayerActionPack.ActionType action;

    public ActionExecution(EntityPlayerActionPack.ActionType action) {
        this.action = action;
    }

    @Override
    public void start(MinionRuntime minion) {
        minion.getMinion().getMinionActionPack().start(action, EntityPlayerActionPack.Action.continuous());
    }

    @Override
    public void stop(MinionRuntime minion, ValueConsumerList<MinionRuntime> valueConsumers) {
        minion.getMinion().getMinionActionPack().stop(action);
    }

    @Override
    public void readArguments(ValueSupplierList<MinionRuntime> parameters, MinionRuntime minion) {}

    @Override
    public void save(WriteView view, MinionRuntime minion) {}

    @Override
    public void load(ReadView view, MinionRuntime minion) {
        minion.getMinion().getMinionActionPack().start(action, EntityPlayerActionPack.Action.continuous());
    }
}

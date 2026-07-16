package io.github.skippyall.minions.registration;

import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.block.miniontrigger.MinionTriggerBlockEntity;
import io.github.skippyall.minions.program.Context;

public class ResolutionContext {
    public static final Context.Key<String> PARAMETER_NAME = new Context.Key<>(Minions.id("parameter_name"));
    public static final Context.Key<MinionTriggerBlockEntity> MINION_TRIGGER = new Context.Key<>(Minions.id("minion_trigger"));
}

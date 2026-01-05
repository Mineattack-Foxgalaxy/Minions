package io.github.skippyall.minions.minion;

import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.util.SerializableListenerManager;

public interface MinionListener extends SerializableListenerManager.SerializableListener {
    default void onMinionSpawn(MinionFakePlayer minion) {}

    default void onMinionRemove(MinionFakePlayer minion) {}

    default void onInstructionsUpdate(MinionFakePlayer minion) {}

    default void onInstructionRename(MinionFakePlayer minion, ConfiguredInstruction<?> instruction, String newName) {}
}

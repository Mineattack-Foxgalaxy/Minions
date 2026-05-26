package io.github.skippyall.minions.minion;

import io.github.skippyall.minions.listener.SerializableListenerManager;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import org.jspecify.annotations.Nullable;

public interface MinionListener extends SerializableListenerManager.SerializableListener {
    default void onMinionSpawn(MinionFakePlayer minion) {}

    default void onMinionRemove(MinionFakePlayer minion) {}

    default void onInstructionsUpdate(MinionFakePlayer minion) {}

    default void onInstructionRename(MinionFakePlayer minion, ConfiguredInstruction<?> instruction, String oldName, String newName) {}

    interface Delegating extends MinionListener {
        @Nullable MinionListener getBacking();

        @Override
        default void onMinionSpawn(MinionFakePlayer minion) {
            if(getBacking() != null) {
                getBacking().onMinionSpawn(minion);
            }
        }

        @Override
        default void onMinionRemove(MinionFakePlayer minion) {
            if(getBacking() != null) {
                getBacking().onMinionRemove(minion);
            }
        }

        @Override
        default void onInstructionsUpdate(MinionFakePlayer minion) {
            if(getBacking() != null) {
                getBacking().onInstructionsUpdate(minion);
            }
        }

        @Override
        default void onInstructionRename(MinionFakePlayer minion, ConfiguredInstruction<?> instruction, String oldName, String newName) {
            if(getBacking() != null) {
                getBacking().onInstructionRename(minion, instruction, oldName, newName);
            }
        }
    }
}

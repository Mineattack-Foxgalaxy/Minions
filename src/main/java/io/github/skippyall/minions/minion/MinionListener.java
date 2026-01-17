package io.github.skippyall.minions.minion;

import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.util.SerializableListenerManager;
import org.jetbrains.annotations.Nullable;

public interface MinionListener extends SerializableListenerManager.SerializableListener {
    default void onMinionSpawn(MinionFakePlayer minion) {}

    default void onMinionRemove(MinionFakePlayer minion) {}

    default void onInstructionsUpdate(MinionFakePlayer minion) {}

    default void onInstructionRename(MinionFakePlayer minion, ConfiguredInstruction<?> instruction, String newName) {}

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
                getBacking().onMinionSpawn(minion);
            }
        }

        @Override
        default void onInstructionRename(MinionFakePlayer minion, ConfiguredInstruction<?> instruction, String newName) {
            if(getBacking() != null) {
                getBacking().onInstructionRename(minion, instruction, newName);
            }
        }
    }
}

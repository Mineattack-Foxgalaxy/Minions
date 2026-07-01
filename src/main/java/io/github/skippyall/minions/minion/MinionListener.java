package io.github.skippyall.minions.minion;

import io.github.skippyall.minions.listener.SerializableListenerManager;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import org.jspecify.annotations.Nullable;

public interface MinionListener extends SerializableListenerManager.SerializableListener {
    default void onMinionSpawn(MinionFakePlayer minion) {}

    default void onMinionRemove(MinionFakePlayer minion) {}

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
    }
}

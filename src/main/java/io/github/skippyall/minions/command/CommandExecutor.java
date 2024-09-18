package io.github.skippyall.minions.command;

import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import net.minecraft.server.network.ServerPlayerEntity;

public interface CommandExecutor {
    void execute(ServerPlayerEntity player, MinionFakePlayer minion);
}

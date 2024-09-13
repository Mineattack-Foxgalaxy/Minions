package io.github.skippyall.minions.command;

import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import net.minecraft.item.Item;
import net.minecraft.network.packet.LoginPackets;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public interface Command {
    Text getName();
    Text getDescription();
    Item getItemRepresentation();

    void onRun(ServerPlayerEntity player, MinionFakePlayer minion);
}

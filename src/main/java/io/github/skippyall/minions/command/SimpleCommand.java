package io.github.skippyall.minions.command;

import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SimpleCommand implements Command {
    private final Text name;
    private final Text description;
    private final Item itemRepresentation;
    private final BiConsumer<ServerPlayerEntity, MinionFakePlayer> onRun;

    public SimpleCommand(Text name, Text description, Item itemRepresentation, BiConsumer<ServerPlayerEntity, MinionFakePlayer> onRun) {
        this.name = name;
        this.description = description;
        this.itemRepresentation = itemRepresentation;
        this.onRun = onRun;
    }

    @Override
    public Text getName() {
        return name;
    }

    @Override
    public Text getDescription() {
        return description;
    }

    @Override
    public Item getItemRepresentation() {
        return itemRepresentation;
    }

    @Override
    public void onRun(ServerPlayerEntity player, MinionFakePlayer minion) {
        onRun.accept(player, minion);
    }
}

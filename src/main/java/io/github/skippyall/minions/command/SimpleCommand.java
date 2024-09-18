package io.github.skippyall.minions.command;

import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class SimpleCommand implements Command {
    private final Text name;
    private final Text description;
    private final Item itemRepresentation;
    private final CommandExecutor executor;

    public SimpleCommand(Text name, Text description, Item itemRepresentation, CommandExecutor executor) {
        this.name = name;
        this.description = description;
        this.itemRepresentation = itemRepresentation;
        this.executor = executor;
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
    public void execute(ServerPlayerEntity player, MinionFakePlayer minion) {
        executor.execute(player, minion);
    }
}

package io.github.skippyall.minions.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.skippyall.minions.minion.MinionData;
import io.github.skippyall.minions.minion.MinionPersistentState;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.literal;

public class ListSubcommand {
    public static final LiteralArgumentBuilder<ServerCommandSource> LIST = literal("list")
            .executes(ListSubcommand::list);

    public static int list(CommandContext<ServerCommandSource> context) {
        Collection<MinionData> minions = MinionPersistentState.get(context.getSource().getServer()).getMinionData().values();
        for (MinionData minion : minions) {
            context.getSource().sendFeedback(() -> Text.literal(minion.name() + "(" + minion.uuid() + "):" + minion.isSpawned()), false);
        }
        return 0;
    }
}

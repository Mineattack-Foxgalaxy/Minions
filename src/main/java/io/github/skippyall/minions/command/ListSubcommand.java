package io.github.skippyall.minions.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.github.skippyall.minions.minion.MinionData;
import io.github.skippyall.minions.minion.MinionPersistentState;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.literal;

public class ListSubcommand {
    public static final LiteralArgumentBuilder<CommandSourceStack> LIST = literal("list")
            .executes(ListSubcommand::list);

    public static int list(CommandContext<CommandSourceStack> context) {
        Collection<MinionData> minions = MinionPersistentState.get(context.getSource().getServer()).getMinionData().values();
        for (MinionData minion : minions) {
            context.getSource().sendSuccess(() -> Component.literal(minion.name() + "(" + minion.uuid() + "):" + minion.isSpawned()), false);
        }
        return 0;
    }
}

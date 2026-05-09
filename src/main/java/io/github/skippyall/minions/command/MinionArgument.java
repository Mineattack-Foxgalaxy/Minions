package io.github.skippyall.minions.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.skippyall.minions.minion.MinionData;
import io.github.skippyall.minions.minion.MinionPersistentState;
import io.github.skippyall.minions.minion.MinionProfileUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MinionArgument {
    public static final SimpleCommandExceptionType MINION_NOT_PRESENT = new SimpleCommandExceptionType(Component.translatable("minions.command.minion.not_present"));

    public static final MinionSuggestionProvider SUGGESTION_PROVIDER = new MinionSuggestionProvider();

    public static MinionData parse(MinecraftServer server, String argument) throws CommandSyntaxException {
        Optional<MinionData> data = Optional.empty();
        if(argument.startsWith(MinionProfileUtils.getPrefix())) {
            data = MinionPersistentState.get(server).getMinionWithName(argument);
        } else {
            try {
                data = Optional.ofNullable(MinionPersistentState.get(server).getMinionData(UUID.fromString(argument)));
            } catch (IllegalArgumentException ignored) {}
        }

        if(data.isEmpty()) {
            throw MINION_NOT_PRESENT.create();
        }
        return data.get();
    }

    public static class MinionSuggestionProvider implements SuggestionProvider<CommandSourceStack> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
            for (MinionData data : MinionPersistentState.get(context.getSource().getServer()).getMinionDataList()) {
                builder.suggest(data.getName());
            }

            return builder.buildFuture();
        }
    }
}

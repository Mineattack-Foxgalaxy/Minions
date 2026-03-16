package io.github.skippyall.minions.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.skippyall.minions.docs.DocsManager;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

public class DocsSubcommand {
    public static final LiteralArgumentBuilder<ServerCommandSource> DOCS = literal("docs")
            .then(
                    argument("docName", IdentifierArgumentType.identifier())
                            .suggests(DocsSubcommand::getSuggestions)
                            .executes(DocsSubcommand::execute)
            );

    public static CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        DocsManager.getDocsEntryIds().forEach(id -> builder.suggest(id.toString()));
        return CompletableFuture.completedFuture(builder.build());
    }

    public static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Identifier id = IdentifierArgumentType.getIdentifier(context, "docName");
        DocsManager.showDocsEntry(context.getSource().getPlayerOrThrow(), id);
        return 1;
    }
}

package io.github.skippyall.minions.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.github.skippyall.minions.docs.DocsManager;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.resources.ResourceLocation;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class DocsSubcommand {
    public static final LiteralArgumentBuilder<CommandSourceStack> DOCS = literal("docs")
            .then(
                    argument("docName", ResourceLocationArgument.id())
                            .suggests(DocsSubcommand::getSuggestions)
                            .executes(DocsSubcommand::execute)
            );

    public static CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        DocsManager.getDocsEntryIds().forEach(id -> builder.suggest(id.toString()));
        return CompletableFuture.completedFuture(builder.build());
    }

    public static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocationArgument.getId(context, "docName");
        DocsManager.showDocsEntry(context.getSource().getPlayerOrException(), id);
        return 1;
    }
}

package io.github.skippyall.minions.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.skippyall.minions.minion.MinionData;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.server.permissions.Permissions;
import org.jspecify.annotations.Nullable;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class SpawnSubcommand {
    public static final LiteralArgumentBuilder<CommandSourceStack> SPAWN = literal("spawn")
            .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
            .then(argument("minion", StringArgumentType.word())
                    .suggests(MinionArgument.SUGGESTION_PROVIDER)
                    .then(argument("pos", Vec3Argument.vec3())
                            .executes(context ->
                                    spawnCommand(
                                            context.getSource(),
                                            StringArgumentType.getString(context, "minion"),
                                            Vec3Argument.getCoordinates(context, "pos"),
                                            false
                                    )
                            )
                            .then(argument("force", BoolArgumentType.bool())
                                    .executes(context ->
                                            spawnCommand(
                                                    context.getSource(),
                                                    StringArgumentType.getString(context, "minion"),
                                                    Vec3Argument.getCoordinates(context, "pos"),
                                                    BoolArgumentType.getBool(context, "force")
                                            )
                                    )
                            )
                    )
                    .executes(context ->
                            spawnCommand(
                                    context.getSource(),
                                    StringArgumentType.getString(context, "minion"),
                                    null,
                                    false
                            ))
            );

    public static int spawnCommand(CommandSourceStack source, String minion, @Nullable Coordinates pos, boolean force) throws CommandSyntaxException {
        MinionData data = MinionArgument.parse(source.getServer(), minion);
        MinionFakePlayer.spawnMinion(data, source.getLevel(), pos != null ? pos.getPosition(source) : null, pos != null ? pos.getRotation(source) : null, force);
        return 0;
    }
}

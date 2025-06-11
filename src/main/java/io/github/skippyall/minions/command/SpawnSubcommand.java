package io.github.skippyall.minions.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.skippyall.minions.minion.MinionData;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import net.minecraft.command.argument.PosArgument;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SpawnSubcommand {
    public static final LiteralArgumentBuilder<ServerCommandSource> SPAWN = literal("spawn")
            .requires(source -> source.hasPermissionLevel(2))
            .then(argument("minion", StringArgumentType.word())
                    .suggests(MinionArgument.SUGGESTION_PROVIDER)
                    .then(argument("pos", Vec3ArgumentType.vec3())
                            .executes(context ->
                                    spawnCommand(
                                            context.getSource(),
                                            StringArgumentType.getString(context, "minion"),
                                            Vec3ArgumentType.getPosArgument(context, "pos"),
                                            false
                                    )
                            )
                            .then(argument("force", BoolArgumentType.bool())
                                    .executes(context ->
                                            spawnCommand(
                                                    context.getSource(),
                                                    StringArgumentType.getString(context, "minion"),
                                                    Vec3ArgumentType.getPosArgument(context, "pos"),
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

    public static int spawnCommand(ServerCommandSource source, String minion, PosArgument pos, boolean force) throws CommandSyntaxException {
        MinionData data = MinionArgument.parse(minion);
        MinionFakePlayer.spawnMinion(data, source.getWorld(), pos != null ? pos.getPos(source) : null, pos != null ? pos.getRotation(source) : null, force);
        return 0;
    }
}

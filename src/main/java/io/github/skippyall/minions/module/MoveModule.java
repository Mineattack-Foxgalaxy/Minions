package io.github.skippyall.minions.module;

import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.command.SimpleCommand;
import io.github.skippyall.minions.input.TextInput;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

import static io.github.skippyall.minions.module.Modules.register;

public class MoveModule {
    public static final SimpleCommand WALK_COMMAND =
            new SimpleCommand(
                    Text.literal("Walk"),
                    Text.literal("Walk a specific amount of blocks forward"),
                    Items.IRON_BOOTS,
                    (player, minion) -> TextInput.inputFloat(player, Text.literal("Amount of Blocks"), "1")
                            .thenAccept(minion::moveForward)
            );

    public static final SimpleCommand TURN_RIGHT_COMMAND =
            new SimpleCommand(
                    Text.literal("Turn Right"),
                    Text.literal("Turn a specific amount of degrees right"),
                    Items.COMPASS,
                    (player, minion) -> TextInput.inputFloat(player, Text.literal("Degrees"), "90")
                            .thenAccept(degrees -> minion.getMinionActionPack().turn(degrees, 0))
            );

    public static final SimpleCommand TURN_LEFT_COMMAND =
            new SimpleCommand(
                    Text.literal("Turn Left"),
                    Text.literal("Turn a specific amount of degrees left"),
                    Items.COMPASS,
                    (player, minion) -> TextInput.inputFloat(player, Text.literal("Degrees"), "90")
                            .thenAccept(degrees -> minion.getMinionActionPack().turn(-degrees, 0))
            );

    public static final SimpleModuleItem MOVE_MODULE =
            register(Identifier.of(Minions.MOD_ID, "move_module"),
                    List.of(),
                    List.of(
                            WALK_COMMAND,
                            TURN_RIGHT_COMMAND,
                            TURN_LEFT_COMMAND
                    ),
                    Items.IRON_BOOTS
            );

    public static void registerMe() {}
}

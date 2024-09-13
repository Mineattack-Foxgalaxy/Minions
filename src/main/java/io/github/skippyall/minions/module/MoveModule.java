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
    public static final SimpleCommand WALK_COMMAND = new SimpleCommand(Text.literal("Walk"), Text.literal("Walk a specific amount of blocks forward"), Items.IRON_BOOTS, (player, minion) -> {
        TextInput.inputText(player, Text.literal("Amount of Blocks"), "1")
                .thenAccept(string -> {
                    try {
                        float blocks = Float.parseFloat(string);
                        minion.moveForward(blocks);
                    } catch (NumberFormatException e) {
                        player.sendMessage(Text.literal("No valid number"));
                    }
                });
    });

    public static final SimpleCommand TURN_RIGHT_COMMAND = new SimpleCommand(Text.literal("Turn Right"), Text.literal("Turn a specific amount of degrees right"), Items.COMPASS, ((player, minion) -> {
        TextInput.inputText(player, Text.literal("Degrees"), "90")
                .thenAccept(string -> {
                    try {
                        float degrees = Float.parseFloat(string);
                        minion.getMinionActionPack().turn(degrees, 0);
                    } catch (NumberFormatException e) {
                        player.sendMessage(Text.literal("No valid number"));
                    }
                });
    }));

    public static final SimpleCommand TURN_LEFT_COMMAND = new SimpleCommand(Text.literal("Turn Left"), Text.literal("Turn a specific amount of degrees left"), Items.COMPASS, ((player, minion) -> {
        TextInput.inputText(player, Text.literal("Degrees"), "90")
                .thenAccept(string -> {
                    try {
                        float degrees = Float.parseFloat(string);
                        minion.getMinionActionPack().turn(-degrees, 0);
                    } catch (NumberFormatException e) {
                        player.sendMessage(Text.literal("No valid number"));
                    }
                });
    }));

    public static final SimpleModuleItem MOVE_MODULE = register(Identifier.of(Minions.MOD_ID, "move_module"), new SimpleModuleItem(List.of(), List.of(WALK_COMMAND, TURN_RIGHT_COMMAND, TURN_LEFT_COMMAND), Items.IRON_BOOTS));

    public static void registerMe() {}
}

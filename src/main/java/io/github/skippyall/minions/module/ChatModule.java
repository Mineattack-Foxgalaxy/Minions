package io.github.skippyall.minions.module;

import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.command.SimpleCommand;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.github.skippyall.minions.module.Modules.register;

public class ChatModule {
    public static final SimpleModuleItem CHAT_MODULE = register(Identifier.of(Minions.MOD_ID, "chat_module"),
            new SimpleModuleItem(new ArrayList<>(), Arrays.asList(
                    new SimpleCommand(Text.of("Message"), Text.of("Send Message in Public Chat"), Items.PAPER, (player, minion) -> minion.getServer().getPlayerManager().broadcast(Text.of("message"), true)),
                    new SimpleCommand(Text.of("Prvt-Message"), Text.of("Send Message to one Person"), Items.TRIAL_KEY, (player, minion) -> {})
            ), Items.PAPER));

    public static void registerMe() {}
}

package io.github.skippyall.minions.module;

import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.command.SimpleCommand;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

import static io.github.skippyall.minions.module.Modules.register;

public class MountModule {
    public static final SimpleCommand MOUNT_COMMAND = new SimpleCommand(
            Text.of("Mount"),
            Text.of("Mount the minion to the nearest mountable Entity"),
            Items.MINECART,
            (player, minion) -> minion.getMinionActionPack().mount(true)
    );

    public static final SimpleCommand DISMOUNT_COMMAND = new SimpleCommand(
            Text.of("Dismount"),
            Text.of("Dismount the minion"),
            Items.BARRIER,
            (player, minion) -> minion.getMinionActionPack().dismount()
    );

    public static final SimpleModuleItem MOUNT_MODULE =
            register(Identifier.of(Minions.MOD_ID, "mount_module"),
                    List.of(),
                    List.of(
                            MOUNT_COMMAND,
                            DISMOUNT_COMMAND
                    ),
                    Items.MINECART
            );

    public static void registerMe() {
    }

}

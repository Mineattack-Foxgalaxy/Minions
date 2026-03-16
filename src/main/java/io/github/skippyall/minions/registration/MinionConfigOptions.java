package io.github.skippyall.minions.registration;

import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.minion.MinionConfig;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static io.github.skippyall.minions.minion.MinionConfig.booleanOption;

public class MinionConfigOptions {
    public static final MinionConfig.Option<Boolean> showInServerList = register(booleanOption(id("show_in_server_list"), false));
    public static final MinionConfig.Option<Boolean> showInTabList = register(booleanOption(id("show_in_tab_list"), false));
    public static final MinionConfig.Option<Boolean> sendLoginMessage = register(booleanOption(id("send_login_message"), false));
    public static final MinionConfig.Option<Boolean> sendLogoutMessage = register(booleanOption(id("send_logout_message"), false));
    public static final MinionConfig.Option<Boolean> countForSleeping = register(booleanOption(id("count_for_sleeping"), false));
    public static final MinionConfig.Option<Boolean> countForPlayerLimit = register(booleanOption(id("count_for_player_limit"), false));
    public static final MinionConfig.Option<Boolean> spawnAndDespawnMobs = register(booleanOption(id("spawn_and_despawn_mobs"), false));

    private static <T> MinionConfig.Option<T> register(MinionConfig.Option<T> option) {
        return Registry.register(MinionRegistries.MINION_CONFIG_OPTIONS, option.key(), option);
    }

    private static Identifier id(String name) {
        return Identifier.of(Minions.MOD_ID, name);
    }

    public static void register() {}
}

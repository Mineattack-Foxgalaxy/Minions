package io.github.skippyall.minions;

import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.minion.MinionData;
import io.github.skippyall.minions.minion.MinionItem;
import io.github.skippyall.minions.minion.MinionPersistentState;
import io.github.skippyall.minions.module.Modules;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DamageResistantComponent;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ChunkTicketManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Minions implements ModInitializer {
    public static final String MOD_ID = "minions";
    public static final TagKey<DamageType> MINION_ITEM_RESISTS = TagKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(MOD_ID, "minion_item_resists"));
    public static final MinionItem MINION_ITEM = registerItem(Identifier.of(MOD_ID, "minion"), settings -> new MinionItem(settings.component(DataComponentTypes.DAMAGE_RESISTANT, new DamageResistantComponent(MINION_ITEM_RESISTS)),false));
    public static final SimplePolymerItem BASIC_UPGRADE_BASE = registerItem(Identifier.of(MOD_ID, "basic_upgrade_base"), settings -> new SimplePolymerItem(settings, Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE));
    public static final SimplePolymerItem ADVANCED_UPGRADE_BASE = registerItem(Identifier.of(MOD_ID, "advanced_upgrade_base"), settings -> new SimplePolymerItem(settings, Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE));

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final List<Runnable> executeOnNextTick = new ArrayList<>();

    @Override
    public void onInitialize() {
        MinionData.register();
        PolymerEntityUtils.registerType();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            MinionPersistentState.create(server);
            MinionPersistentState.INSTANCE.getMinionData().forEach(data -> {
                MinionFakePlayer.spawnMinionAt(data, server.getOverworld(), null, null);
            });
        });
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            exec(() -> {
                for (Runnable run:executeOnNextTick) {
                    run.run();
                }
                executeOnNextTick.clear();
            });
        });

        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            MobCapCommand.registerCommand(commandDispatcher);
        });

        Modules.register();
    }

    private static <T extends Item> T registerItem(Identifier identifier, Function<Item.Settings, T> constructor, Item.Settings settings) {
        return Registry.register(Registries.ITEM, identifier, constructor.apply(settings.registryKey(RegistryKey.of(RegistryKeys.ITEM, identifier))));
    }

    private static <T extends Item> T registerItem(Identifier identifier, Function<Item.Settings, T> constructor) {
        return registerItem(identifier, constructor, new Item.Settings());
    }

    private static synchronized void exec(Runnable run) {
        run.run();
    }

    public static void addExecuteOnNextTick(Runnable run) {
        exec(() -> {
            executeOnNextTick.add(run);
        });
    }
}

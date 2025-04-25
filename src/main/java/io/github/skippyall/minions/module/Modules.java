package io.github.skippyall.minions.module;

import io.github.skippyall.minions.MinionItems;
import io.github.skippyall.minions.module.command.Command;
import io.github.skippyall.minions.program.block.CodeBlock;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.List;

public class Modules {
    public static void register() {
        ChatModule.registerMe();
        MountModule.registerMe();
        MoveModule.registerMe();
        MobSpawningModule.registerMe();
        InteractModule.registerMe();
        AttackModule.registerMe();
    }

    public static SimpleModuleItem register(Identifier id, List<CodeBlock<?,?>> codeBlocks, List<Command> commands, Item vanillaItem) {
        return MinionItems.registerItem(id, settings -> new SimpleModuleItem(codeBlocks, commands, settings, vanillaItem));
    }
}

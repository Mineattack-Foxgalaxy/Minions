package io.github.skippyall.minions.program.module;

import eu.pb4.polymer.core.api.item.PolymerItem;
import io.github.skippyall.minions.program.block.CodeBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;

import java.util.List;

public abstract class Module extends Item implements PolymerItem {
    private final String name;
    public Module(String name) {
        super(new FabricItemSettings().maxCount(1));
        this.name = name;
    }

    public String getModuleName() {
        return name;
    }

    public abstract List<CodeBlock> getCodeBlocks();
}

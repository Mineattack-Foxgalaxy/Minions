package io.github.skippyall.minions.program.module;

import eu.pb4.polymer.core.api.item.PolymerItem;
import io.github.skippyall.minions.program.block.CodeBlock;

import java.util.List;

public abstract class Modul implements PolymerItem {
    private final String name;
    Modul(String name) {
        this.name = name;
    }

    public abstract List<CodeBlock> getCodeBlocks();
}

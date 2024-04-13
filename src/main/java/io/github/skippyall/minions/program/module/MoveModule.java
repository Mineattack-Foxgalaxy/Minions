package io.github.skippyall.minions.program.module;

import io.github.skippyall.minions.program.block.CodeBlock;
import io.github.skippyall.minions.program.block.CodeBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MoveModule extends Module {
    MoveModule() {
        super("Movement");
    }

    public List<CodeBlock> getCodeBlocks() {
        List<CodeBlock> codeBlocks = new ArrayList<>();
        codeBlocks.add(CodeBlocks.GO);
        return codeBlocks;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return Items.PURPLE_GLAZED_TERRACOTTA;
    }
}

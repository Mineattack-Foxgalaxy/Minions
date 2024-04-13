package io.github.skippyall.minions.program.module;

import io.github.skippyall.minions.program.block.CodeBlock;
import io.github.skippyall.minions.program.block.CodeBlocks;
import io.github.skippyall.minions.program.block.ForwardBlock;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MoveModul extends Modul{
    MoveModul() {
        super("Movement");
    }

    public List<CodeBlock> getCodeBlocks() {
        List<CodeBlock> codeBlocks = new ArrayList<>();
        codeBlocks.add(CodeBlocks.FORWARD);
        return codeBlocks;
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayer player) {
        return Items.PURPLE_GLAZED_TERRACOTTA;
    }
}

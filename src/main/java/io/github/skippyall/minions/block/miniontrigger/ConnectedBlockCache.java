package io.github.skippyall.minions.block.miniontrigger;

import io.github.skippyall.minions.block.input.BlockValueConsumer;
import io.github.skippyall.minions.block.input.BlockValueSupplier;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.program.value.TypedValue;
import io.github.skippyall.minions.program.value.ValueType;
import io.github.skippyall.minions.registration.MinionBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.function.TriConsumer;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ConnectedBlockCache {
    private Map<String, BlockValueSupplier> connectedSupplierBlocks = new HashMap<>();
    private Map<String, BlockValueConsumer> connectedConsumerBlocks = new HashMap<>();

    private ConnectedBlockCache() {}

    public static ConnectedBlockCache create(Level level, BlockPos pos, ConfiguredInstruction instruction) {
        ConnectedBlockCache cache = new ConnectedBlockCache();
        findConnectedBlocks(level, pos, 16, MinionBlocks.TRIGGER_CONNECTOR, (level2, connectedPos, dir) -> cache.addConnectedBlock(level2, connectedPos, dir, instruction));
        return cache;
    }

    private void addConnectedBlock(Level level, BlockPos pos, Direction dir, ConfiguredInstruction instruction) {
        String signText = findSignText(level, pos);
        if(signText == null) {
            return;
        }

        BlockValueSupplier provider = BlockValueSupplier.SIDED.find(level, pos, dir.getOpposite());
        if(provider != null && instruction.getInstruction().hasParameter(signText)) {
            connectedSupplierBlocks.put(signText, provider);
            return;
        }

        BlockValueConsumer consumer = BlockValueConsumer.SIDED.find(level, pos, dir.getOpposite());
        if(consumer != null && instruction.getInstruction().hasReturnParameter(signText)) {
            connectedConsumerBlocks.put(signText, consumer);
        }
    }

    public @Nullable TypedValue<?> getConnectedBlockValue(String paramName) {
        BlockValueSupplier block = connectedSupplierBlocks.get(paramName);
        if (block != null) {
            return block.getValue();
        }
        return null;
    }

    public void setConnectedBlockValue(String paramName, TypedValue<?> value) {
        BlockValueConsumer block = connectedConsumerBlocks.get(paramName);
        if (block != null) {
            block.acceptValue(value);
        }
    }

    public @Nullable ValueType<?> getConnectedBlockType(String paramName) {
        BlockValueConsumer block = connectedConsumerBlocks.get(paramName);
        if (block != null) {
            return block.getType();
        }
        return null;
    }

    public static @Nullable String findSignText(Level level, BlockPos pos) {
        for(Direction dir : Direction.values()) {
            BlockPos neighbor = pos.relative(dir);
            BlockState state = level.getBlockState(neighbor);
            if(state.getBlock() instanceof SignBlock block && level.getBlockEntity(neighbor) instanceof SignBlockEntity be) {
                StringBuilder text = new StringBuilder();
                for(Component partialText : be.getFrontText().getMessages(false)) {
                    text.append(partialText.getString().strip());
                }
                return text.toString();
            }
        }
        return null;
    }

    public static void findConnectedBlocks(Level level, BlockPos startPos, int range, Block connectorBlock, TriConsumer<Level, BlockPos, Direction> blockChecker) {
        Collection<BlockPos> visitedPositions = new HashSet<>();
        Collection<BlockPos> currentPositions = new ArrayList<>();
        Collection<BlockPos> newPositions = new HashSet<>();

        currentPositions.add(startPos);

        for(int i = 0; i < range && !currentPositions.isEmpty(); i++) {
            visitedPositions.addAll(currentPositions);

            for(BlockPos pos : currentPositions) {
                for(Direction dir : Direction.values()) {
                    BlockPos newPos = pos.relative(dir);
                    if(!visitedPositions.contains(newPos)) {
                        if (level.getBlockState(newPos).getBlock() == connectorBlock) {
                            newPositions.add(newPos);
                        } else {
                            blockChecker.accept(level, newPos, dir);
                        }
                    }
                }
            }
            currentPositions.clear();
            currentPositions.addAll(newPositions);
            newPositions.clear();
        }
    }
}

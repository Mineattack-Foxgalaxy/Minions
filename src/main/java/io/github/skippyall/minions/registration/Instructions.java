package io.github.skippyall.minions.registration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.minion.fakeplayer.EntityPlayerActionPack;
import io.github.skippyall.minions.minion.program.instruction.ActionExecution;
import io.github.skippyall.minions.minion.program.instruction.MineBlockExecution;
import io.github.skippyall.minions.minion.program.instruction.inventory.SwapItemExecution;
import io.github.skippyall.minions.minion.program.instruction.move.ContinuousWalkExecution;
import io.github.skippyall.minions.minion.program.instruction.move.TurnExecution;
import io.github.skippyall.minions.minion.program.instruction.move.TurnVectorExecution;
import io.github.skippyall.minions.minion.program.instruction.move.WalkExecution;
import io.github.skippyall.minions.program.Context;
import io.github.skippyall.minions.program.instruction.InstructionExecution;
import io.github.skippyall.minions.program.instruction.InstructionType;
import io.github.skippyall.minions.program.handler.Parameter;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class Instructions {
    public static final InstructionType WALK = register(
            "walk",
            WalkExecution::new,
            List.of(WalkExecution.blocksToMoveParam),
            List.of(ExecutionContext.MINION_KEY),
            WalkExecution.CODEC
    );
    public static final InstructionType WALK_CONTINUOUS = register(
            "walk_continuous",
            ContinuousWalkExecution::new,
            List.of(ExecutionContext.MINION_KEY),
            ContinuousWalkExecution.CODEC
    );

    public static final InstructionType TURN = register(
            "turn",
            TurnExecution::new,
            List.of(TurnExecution.ANGLE, TurnExecution.DIRECTION),
            List.of(ExecutionContext.MINION_KEY),
            TurnExecution.CODEC
    );

    public static final InstructionType TURN_VECTOR = register(
            "turn_vector",
            TurnVectorExecution::new,
            List.of(TurnVectorExecution.X, TurnVectorExecution.Y, TurnVectorExecution.Z),
            List.of(ExecutionContext.MINION_KEY),
            TurnVectorExecution.CODEC
    );

    public static final InstructionType ATTACK = register(
            "attack",
            () -> new ActionExecution(EntityPlayerActionPack.ActionType.ATTACK),
            List.of(ExecutionContext.MINION_KEY),
            MapCodec.unitCodec(() -> new ActionExecution(EntityPlayerActionPack.ActionType.ATTACK))
    );

    public static final InstructionType MINE_BLOCK = register(
            "mine_block",
            MineBlockExecution::new,
            List.of(ExecutionContext.MINION_KEY),
            MineBlockExecution.CODEC
    );

    public static final InstructionType USE = register(
            "use",
            () -> new ActionExecution(EntityPlayerActionPack.ActionType.USE),
            List.of(ExecutionContext.MINION_KEY),
            MapCodec.unitCodec(() -> new ActionExecution(EntityPlayerActionPack.ActionType.USE))
    );

    public static final InstructionType SWAP_ITEM = register(
            "swap_item",
            SwapItemExecution::new,
            List.of(SwapItemExecution.FROM_SLOT, SwapItemExecution.FROM_SCREEN, SwapItemExecution.TO_SLOT, SwapItemExecution.TO_SCREEN),
            List.of(ExecutionContext.MINION_KEY),
            SwapItemExecution.CODEC
    );

    private static InstructionType register(String id, Supplier<InstructionExecution> factory, Collection<Parameter<?>> parameters, Collection<Parameter<?>> returnParameters, Collection<Context.Key<?>> keys, Codec<? extends InstructionExecution> executionCodec) {
        Identifier identifier = Identifier.fromNamespaceAndPath(Minions.MOD_ID, id);
        return Registry.register(MinionRegistries.INSTRUCTION_TYPES, identifier, new InstructionType(factory, parameters, returnParameters, keys, executionCodec));
    }

    private static InstructionType register(String id, Supplier<InstructionExecution> factory, Collection<Parameter<?>> parameters, Collection<Context.Key<?>> keys, Codec<? extends InstructionExecution> executionCodec) {
        return register(id, factory, parameters, List.of(), List.of(), executionCodec);
    }

    private static InstructionType register(String id, Supplier<InstructionExecution> factory, Collection<Context.Key<?>> keys, Codec<? extends InstructionExecution> executionCodec) {
        return register(id, factory, List.of(), List.of(), executionCodec);
    }

    public static void register() {

    }
}

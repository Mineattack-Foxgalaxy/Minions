package io.github.skippyall.minions.registration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.EntityPlayerActionPack;
import io.github.skippyall.minions.minion.program.instruction.ActionExecution;
import io.github.skippyall.minions.minion.program.instruction.MineBlockExecution;
import io.github.skippyall.minions.minion.program.instruction.inventory.SwapItemExecution;
import io.github.skippyall.minions.minion.program.instruction.move.ContinuousWalkExecution;
import io.github.skippyall.minions.minion.program.instruction.move.TurnExecution;
import io.github.skippyall.minions.minion.program.instruction.move.TurnVectorExecution;
import io.github.skippyall.minions.minion.program.instruction.move.WalkExecution;
import io.github.skippyall.minions.program.instruction.InstructionExecution;
import io.github.skippyall.minions.program.instruction.InstructionType;
import io.github.skippyall.minions.program.supplier.Parameter;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class Instructions {
    public static final InstructionType<MinionRuntime> WALK = register(
            "walk",
            WalkExecution::new,
            List.of(WalkExecution.blocksToMoveParam),
            WalkExecution.CODEC

    );
    public static final InstructionType<MinionRuntime> WALK_CONTINUOUS = register(
            "walk_continuous",
            ContinuousWalkExecution::new,
            ContinuousWalkExecution.CODEC
    );

    public static final InstructionType<MinionRuntime> TURN = register(
            "turn",
            TurnExecution::new,
            List.of(TurnExecution.ANGLE, TurnExecution.DIRECTION),
            TurnExecution.CODEC
    );

    public static final InstructionType<MinionRuntime> TURN_VECTOR = register(
            "turn_vector",
            TurnVectorExecution::new,
            List.of(TurnVectorExecution.X, TurnVectorExecution.Y, TurnVectorExecution.Z),
            TurnVectorExecution.CODEC
    );

    public static final InstructionType<MinionRuntime> ATTACK = register(
            "attack",
            () -> new ActionExecution(EntityPlayerActionPack.ActionType.ATTACK),
            MapCodec.unitCodec(() -> new ActionExecution(EntityPlayerActionPack.ActionType.ATTACK))
    );

    public static final InstructionType<MinionRuntime> MINE_BLOCK = register(
            "mine_block",
            MineBlockExecution::new,
            MineBlockExecution.CODEC
    );

    public static final InstructionType<MinionRuntime> USE = register(
            "use",
            () -> new ActionExecution(EntityPlayerActionPack.ActionType.USE),
            MapCodec.unitCodec(() -> new ActionExecution(EntityPlayerActionPack.ActionType.USE))
    );

    public static final InstructionType<MinionRuntime> SWAP_ITEM = register(
            "swap_item",
            SwapItemExecution::new,
            List.of(SwapItemExecution.FROM_SLOT, SwapItemExecution.FROM_SCREEN, SwapItemExecution.TO_SLOT, SwapItemExecution.TO_SCREEN),
            SwapItemExecution.CODEC
    );

    private static InstructionType<MinionRuntime> register(String id, Supplier<InstructionExecution<MinionRuntime>> factory, Collection<Parameter<?>> parameters, Collection<Parameter<?>> returnParameters, Codec<? extends InstructionExecution<MinionRuntime>> executionCodec) {
        Identifier identifier = Identifier.fromNamespaceAndPath(Minions.MOD_ID, id);
        return Registry.register(MinionRegistries.INSTRUCTION_TYPES, identifier, new InstructionType<>(factory, parameters, returnParameters, executionCodec));
    }

    private static InstructionType<MinionRuntime> register(String id, Supplier<InstructionExecution<MinionRuntime>> factory, Collection<Parameter<?>> parameters, Codec<? extends InstructionExecution<MinionRuntime>> executionCodec) {
        return register(id, factory, parameters, List.of(), executionCodec);
    }

    private static InstructionType<MinionRuntime> register(String id, Supplier<InstructionExecution<MinionRuntime>> factory, Codec<? extends InstructionExecution<MinionRuntime>> executionCodec) {
        return register(id, factory, List.of(), List.of(), executionCodec);
    }

    public static void register() {

    }
}

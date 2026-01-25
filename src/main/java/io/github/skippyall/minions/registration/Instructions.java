package io.github.skippyall.minions.registration;

import io.github.skippyall.minions.program.instruction.InstructionExecution;
import io.github.skippyall.minions.program.instruction.InstructionType;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.EntityPlayerActionPack;
import io.github.skippyall.minions.instruction.ActionExecution;
import io.github.skippyall.minions.instruction.MineBlockExecution;
import io.github.skippyall.minions.instruction.move.ContinuousWalkExecution;
import io.github.skippyall.minions.instruction.move.TurnExecution;
import io.github.skippyall.minions.instruction.move.TurnVectorExecution;
import io.github.skippyall.minions.instruction.move.WalkExecution;
import io.github.skippyall.minions.program.supplier.Parameter;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public class Instructions {
    public static final InstructionType<MinionRuntime> WALK = register(
            "walk",
            WalkExecution::new,
            List.of(WalkExecution.blocksToMoveParam)
    );
    public static final InstructionType<MinionRuntime> WALK_CONTINUOUS = register(
            "walk_continuous",
            ContinuousWalkExecution::new
    );

    public static final InstructionType<MinionRuntime> TURN = register(
            "turn",
            TurnExecution::new,
            List.of(TurnExecution.ANGLE, TurnExecution.DIRECTION)
    );

    public static final InstructionType<MinionRuntime> TURN_VECTOR = register(
            "turn_vector",
            TurnVectorExecution::new,
            List.of(TurnVectorExecution.X, TurnVectorExecution.Y, TurnVectorExecution.Z)
    );

    public static final InstructionType<MinionRuntime> ATTACK = register(
            "attack",
            () -> new ActionExecution(EntityPlayerActionPack.ActionType.ATTACK)
    );

    public static final InstructionType<MinionRuntime> MINE_BLOCK = register(
            "mine_block",
            MineBlockExecution::new
    );

    public static final InstructionType<MinionRuntime> USE = register(
            "use",
            () -> new ActionExecution(EntityPlayerActionPack.ActionType.USE)
    );

    private static InstructionType<MinionRuntime> register(String id, Supplier<InstructionExecution<MinionRuntime>> factory, Collection<Parameter<?>> parameters, Collection<Parameter<?>> returnParameters) {
        Identifier identifier = Identifier.of(Minions.MOD_ID, id);
        return Registry.register(MinionRegistries.INSTRUCTION_TYPES, identifier, new InstructionType<>(factory, parameters, returnParameters));
    }

    private static InstructionType<MinionRuntime> register(String id, Supplier<InstructionExecution<MinionRuntime>> factory, Collection<Parameter<?>> parameters) {
        return register(id, factory, parameters, List.of());
    }

    private static InstructionType<MinionRuntime> register(String id, Supplier<InstructionExecution<MinionRuntime>> factory) {
        return register(id, factory, List.of(), List.of());
    }

    public static void register() {

    }
}

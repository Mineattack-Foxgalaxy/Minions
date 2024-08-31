package io.github.skippyall.minions.program.block;

import io.github.skippyall.minions.fakeplayer.EntityPlayerActionPack;
import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.fakeplayer.ServerPlayerInterface;
import io.github.skippyall.minions.program.runtime.MinionRuntime;
import io.github.skippyall.minions.program.runtime.ProgramRuntime;
import io.github.skippyall.minions.program.statement.Statement;
import io.github.skippyall.minions.program.tuple.Tuple2;
import io.github.skippyall.minions.program.variables.Types;

import java.util.List;

public class GoBlock extends CodeBlock<Void, Tuple2<Float, Float>> {
    public GoBlock() {
        super("move", List.of(Types.FLOAT, Types.FLOAT), Types.VOID);
    }

    public Void execute(ProgramRuntime runtime, Tuple2<Float, Float> args, Statement<Void, Tuple2<Float, Float>>.Run run) {
        if(runtime instanceof MinionRuntime minionRuntime) {
            MinionFakePlayer minion = minionRuntime.getMinion();
            EntityPlayerActionPack action = ((ServerPlayerInterface) minion).getActionPack();
            minion.moveForward(args.v0());
            minion.moveSideways(args.v1());
        }
        return null;
    }
}
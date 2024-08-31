package io.github.skippyall.minions.program.block;

import io.github.skippyall.minions.fakeplayer.EntityPlayerActionPack;
import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.runtime.MinionRuntime;
import io.github.skippyall.minions.program.runtime.ProgramRuntime;
import io.github.skippyall.minions.program.statement.Statement;
import io.github.skippyall.minions.program.tuple.Tuple0;
import io.github.skippyall.minions.program.variables.Types;

import java.util.List;

public class UseBlock extends CodeBlock<Void, Tuple0>{
    public UseBlock(String name) {
        super("use", List.of(), Types.VOID);
    }

    @Override
    public Void execute(ProgramRuntime runtime, Tuple0 args, Statement<Void,Tuple0>.Run run) {
        if(runtime instanceof MinionRuntime minionRuntime) {
            MinionFakePlayer minion = minionRuntime.getMinion();
            minion.getMinionActionPack().start(EntityPlayerActionPack.ActionType.USE, EntityPlayerActionPack.Action.once());
        }
        return null;
    }
}

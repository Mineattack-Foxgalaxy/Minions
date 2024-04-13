package io.github.skippyall.minions.program.block;

import io.github.skippyall.minions.fakeplayer.EntityPlayerActionPack;
import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.fakeplayer.ServerPlayerInterface;
import io.github.skippyall.minions.program.variables.IntegerType;
import io.github.skippyall.minions.program.variables.Type;
import io.github.skippyall.minions.program.variables.Types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GoBlock extends CodeBlock{
    public GoBlock() {
        super("move", List.of(Types.FLOAT, Types.FLOAT), Types.VOID);
    }

    public Object execute(MinionFakePlayer minion, Object... args) {
        EntityPlayerActionPack action = ((ServerPlayerInterface)minion).getActionPack();
        minion.moveForward((Float) args[0]);
        minion.moveSideways((Float) args[1]);
        return null;
    }
}
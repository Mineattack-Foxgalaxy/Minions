package io.github.skippyall.minions.program.block;

import io.github.skippyall.minions.fakeplayer.EntityPlayerActionPack;
import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.fakeplayer.ServerPlayerInterface;
import io.github.skippyall.minions.program.variables.Type;
import io.github.skippyall.minions.program.variables.Types;

import java.util.List;

public class UseBlock extends CodeBlock{
    public UseBlock(String name, List<Type> arguments, Type returnType) {
        super("use", List.of(), Types.VOID);
    }

    @Override
    public Object execute(MinionFakePlayer minion, Object... args) {
        minion.getActionPack().start(EntityPlayerActionPack.ActionType.USE, EntityPlayerActionPack.Action.once());
        return null;
    }
}

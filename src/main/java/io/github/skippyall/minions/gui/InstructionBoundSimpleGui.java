package io.github.skippyall.minions.gui;

import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.program.instruction.ConfiguredInstructionListener;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;

public class InstructionBoundSimpleGui extends MinionBoundSimpleGui implements ConfiguredInstructionListener {
    protected final ConfiguredInstruction<MinionRuntime> instruction;

    public InstructionBoundSimpleGui(ScreenHandlerType<?> type, ServerPlayerEntity player, MinionFakePlayer minion, ConfiguredInstruction<MinionRuntime> instruction) {
        super(type, player, minion);
        this.instruction = instruction;
        instruction.addListener(this);
    }

    @Override
    public void onInstructionRemove(ConfiguredInstruction<?> instruction) {
        close();
    }

    @Override
    public void onClose() {
        super.onClose();
        instruction.removeListener(this);
    }
}

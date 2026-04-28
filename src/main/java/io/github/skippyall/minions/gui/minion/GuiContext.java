package io.github.skippyall.minions.gui.minion;

import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.program.supplier.Parameter;
import net.minecraft.server.network.ServerPlayerEntity;

public interface GuiContext {
    ServerPlayerEntity getViewer();

    static GuiContext create(ServerPlayerEntity viewer) {
        return new GuiContextImpl(viewer);
    }

    interface Minion extends GuiContext {
        MinionFakePlayer getMinion();

        static GuiContext.Minion create(GuiContext context, MinionFakePlayer minion) {
            return new GuiContextImpl.MinionImpl(context, minion);
        }
    }

    interface Instruction extends Minion {
        ConfiguredInstruction<MinionRuntime> getInstruction();

        String getName();

        void setName(String name);

        static GuiContext.Instruction create(GuiContext.Minion context, ConfiguredInstruction<MinionRuntime> instruction, String name) {
            return new GuiContextImpl.InstructionImpl(context, instruction, name);
        }
    }

    interface ValueSupplier extends Instruction {
        Parameter<?> getParameter();

        static GuiContext.ValueSupplier create(GuiContext.Instruction context, Parameter<?> parameter) {
            return new GuiContextImpl.ValueSupplierImpl(context, parameter);
        }
    }
}

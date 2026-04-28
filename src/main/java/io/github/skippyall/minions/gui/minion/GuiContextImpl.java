package io.github.skippyall.minions.gui.minion;

import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction;
import io.github.skippyall.minions.program.supplier.Parameter;
import net.minecraft.server.network.ServerPlayerEntity;

//If only this mod was kotlin
public class GuiContextImpl implements GuiContext {
    private final ServerPlayerEntity viewer;

    public GuiContextImpl(ServerPlayerEntity viewer) {
        this.viewer = viewer;
    }

    @Override
    public ServerPlayerEntity getViewer() {
        return viewer;
    }

    public static class MinionImpl extends DelegatingGuiContextImpl<GuiContext> implements GuiContext.Minion {
        private final MinionFakePlayer minion;

        public MinionImpl(GuiContext context, MinionFakePlayer minion) {
            super(context);
            this.minion = minion;
        }

        @Override
        public MinionFakePlayer getMinion() {
            return minion;
        }
    }

    public static class InstructionImpl extends DelegatingMinionImpl<GuiContext.Minion> implements GuiContext.Instruction {
        private final ConfiguredInstruction<MinionRuntime> instruction;
        private String name;

        public InstructionImpl(GuiContext.Minion context, ConfiguredInstruction<MinionRuntime> instruction, String name) {
            super(context);
            this.instruction = instruction;
            this.name = name;
        }

        @Override
        public ConfiguredInstruction<MinionRuntime> getInstruction() {
            return instruction;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }
    }

    public static class ValueSupplierImpl extends DelegatingInstructionImpl<GuiContext.Instruction> implements GuiContext.ValueSupplier {
        private final Parameter<?> parameter;

        public ValueSupplierImpl(GuiContext.Instruction context, Parameter<?> parameter) {
            super(context);
            this.parameter = parameter;
        }

        @Override
        public Parameter<?> getParameter() {
            return parameter;
        }
    }

    public static class DelegatingGuiContextImpl<C extends GuiContext> implements GuiContext {
        protected final C context;

        public DelegatingGuiContextImpl(C context) {
            this.context = context;
        }

        @Override
        public ServerPlayerEntity getViewer() {
            return context.getViewer();
        }
    }

    public static class DelegatingMinionImpl<C extends GuiContext.Minion> extends DelegatingGuiContextImpl<C> implements GuiContext.Minion {
        public DelegatingMinionImpl(C context) {
            super(context);
        }

        @Override
        public MinionFakePlayer getMinion() {
            return context.getMinion();
        }
    }

    public static class DelegatingInstructionImpl<C extends GuiContext.Instruction> extends DelegatingMinionImpl<C> implements GuiContext.Instruction {
        public DelegatingInstructionImpl(C context) {
            super(context);
        }

        @Override
        public ConfiguredInstruction<MinionRuntime> getInstruction() {
            return context.getInstruction();
        }

        @Override
        public String getName() {
            return context.getName();
        }

        @Override
        public void setName(String name) {
            context.setName(name);
        }
    }
}

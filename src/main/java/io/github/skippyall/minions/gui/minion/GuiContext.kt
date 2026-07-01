package io.github.skippyall.minions.gui.minion

import io.github.skippyall.minions.gui.minion.GuiContextImpl.InstructionImpl
import io.github.skippyall.minions.gui.minion.GuiContextImpl.MinionImpl
import io.github.skippyall.minions.gui.minion.GuiContextImpl.ValueSupplierImpl
import io.github.skippyall.minions.minion.MinionRuntime
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction
import io.github.skippyall.minions.program.supplier.Parameter
import net.minecraft.server.level.ServerPlayer

interface GuiContext {
    val viewer: ServerPlayer

    companion object {
        @JvmStatic
        fun create(viewer: ServerPlayer): GuiContext {
            return GuiContextImpl(viewer)
        }
    }

    interface Minion : GuiContext {
        val minion: MinionFakePlayer

        companion object {
            @JvmStatic
            fun create(context: GuiContext, minion: MinionFakePlayer): Minion {
                return MinionImpl(
                    if(context is MinionImpl) context.context else context,
                    minion
                )
            }
        }
    }

    interface Instruction : Minion {
        val instruction: ConfiguredInstruction

        var name: String

        companion object {
            @JvmStatic
            fun create(context: Minion, instruction: ConfiguredInstruction, name: String): Instruction {
                return InstructionImpl(
                    if(context is InstructionImpl) context.context else context,
                    instruction,
                    name
                )
            }
        }
    }

    interface ValueSupplier : Instruction {
        val parameter: Parameter<*>

        companion object {
            @JvmStatic
            fun create(context: Instruction, parameter: Parameter<*>): ValueSupplier {
                return ValueSupplierImpl(
                    if(context is ValueSupplierImpl) context.context else context,
                    parameter
                )
            }
        }
    }
}

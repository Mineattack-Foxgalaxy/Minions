package io.github.skippyall.minions.gui.minion

import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer
import io.github.skippyall.minions.program.instruction.ConfiguredInstruction
import io.github.skippyall.minions.program.supplier.Parameter
import net.minecraft.server.level.ServerPlayer

//Thank you kotlin
class GuiContextImpl(override val viewer: ServerPlayer) : GuiContext {
    class MinionImpl(
        val context: GuiContext,
        override val minion: MinionFakePlayer
    ) : GuiContext by context, GuiContext.Minion

    class InstructionImpl(
        val context: GuiContext.Minion,
        override val instruction: ConfiguredInstruction,
        override var name: String
    ) : GuiContext.Minion by context, GuiContext.Instruction

    class ValueSupplierImpl(
        val context: GuiContext.Instruction,
        override val parameter: Parameter<*>
    ) : GuiContext.Instruction by context, GuiContext.ValueSupplier
}

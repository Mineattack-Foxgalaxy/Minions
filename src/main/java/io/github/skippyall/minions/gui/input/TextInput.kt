package io.github.skippyall.minions.gui.input

import eu.pb4.sgui.api.elements.GuiElementBuilder
import eu.pb4.sgui.api.gui.AnvilInputGui
import io.github.skippyall.minions.gui.MinionsGui
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.AnvilMenu
import net.minecraft.world.item.Items
import java.util.concurrent.CompletableFuture

class TextInput<T>(
    parent: MinionsGui,
    val title: Component,
    val defaultValue: String,
    val parser: suspend (String) -> Result<T, Component>
) : MinionsGui(parent) {
    private val valid: GuiElementBuilder = GuiElementBuilder()
        .setItem(Items.EMERALD_BLOCK)
        .setName(Component.literal("OK"))
        .setCallback(Runnable { this.onConfirm() })

    private val invalid: GuiElementBuilder = GuiElementBuilder()
        .setItem(Items.REDSTONE_BLOCK)

    private lateinit var gui: AnvilInputGui

    private var result: Result<T, Component>? = null
    val deferred = CompletableDeferred<T?>()

    init {
        updateConfirmButton(defaultValue)
        open()
    }

    override fun open() {
        gui = object : AnvilInputGui(viewer, false) {
            override fun onInput(input: String) {
                updateConfirmButton(input)
            }

            override fun onPlayerClose(success: Boolean) {
                onBackingClosed()
                if (deferred.isActive) {
                    deferred.complete(null)
                }
            }
        }

        gui.setTitle(title)
        gui.setDefaultInputValue(defaultValue)
        gui.open()
    }

    override fun closeBacking() {
        gui.close()
    }

    fun updateConfirmButton(input: String) {
        scope.launch {
            val result = parser(input)
            this@TextInput.result = result
            if (result.isSuccess()) {
                gui.setSlot(AnvilMenu.RESULT_SLOT, valid)
            } else {
                val text = result.getErrorOrThrow()
                gui.setSlot(AnvilMenu.RESULT_SLOT, invalid.setName(text))
            }
        }
    }

    fun onConfirm() {
        result?.ifSuccess { success: T ->
            deferred.complete(success)
        }
    }

    companion object {
        @JvmStatic
        fun <T>input(
            gui: MinionsGui,
            title: Component,
            defaultValue: String,
            parser: suspend (String) -> Result<T, Component>,
        ): Deferred<T?> {
            val input = TextInput(
                parent = gui,
                title = title,
                defaultValue = defaultValue,
                parser = parser,
            )

            return input.deferred
        }

        @JvmStatic
        fun <T>inputFuture(
            gui: MinionsGui,
            title: Component,
            defaultValue: String,
            parser: (String) -> Result<T, Component>,
        ): CompletableFuture<T?> {
            return input(
                gui = gui,
                title = title,
                defaultValue = defaultValue,
                parser = parser
            ).asCompletableFuture()
        }

        @JvmStatic
        fun inputString(
            gui: MinionsGui,
            title: Component,
            defaultValue: String,
        ): Deferred<String?> {
            return input<String>(
                gui = gui,
                title = title,
                defaultValue = defaultValue,
                parser = { result: String? -> Result.Success<String, Component>(result) },
            )
        }

        @JvmStatic
        fun inputStringFuture(
            gui: MinionsGui,
            title: Component,
            defaultValue: String,
        ): CompletableFuture<String?> {
            return inputString(
                gui = gui,
                title = title,
                defaultValue = defaultValue,
            ).asCompletableFuture()
        }

        @JvmStatic
        fun inputLong(
            gui: MinionsGui,
            title: Component,
            defaultValue: Long,
        ): Deferred<Long?> {
            return input<Long>(
                gui = gui,
                title = title,
                defaultValue = defaultValue.toString(),
                parser = { string ->
                    Result.wrapCustomError<Long, Component>(
                        { string.toLong() },
                        Component.translatable("minions.command.input.int.fail")
                    )
                },
            )
        }

        @JvmStatic
        fun inputLongFuture(
            gui: MinionsGui,
            title: Component,
            defaultValue: Long,
        ): CompletableFuture<Long?> {
            return inputLong(
                gui = gui,
                title = title,
                defaultValue = defaultValue,
            ).asCompletableFuture()
        }

        @JvmStatic
        fun inputDouble(
            gui: MinionsGui,
            title: Component,
            defaultValue: Double,
        ): Deferred<Double?> {
            return input<Double>(
                gui = gui,
                title = title,
                defaultValue = defaultValue.toString(),
                parser = { string ->
                    Result.wrapCustomError<Double, Component>(
                        { string.toDouble() },
                        Component.translatable("minions.command.input.int.fail")
                    )
                },
            )
        }

        @JvmStatic
        fun inputDoubleFuture(
            gui: MinionsGui,
            title: Component,
            defaultValue: Double,
        ): CompletableFuture<Double?> {
            return inputDouble(
                gui = gui,
                title = title,
                defaultValue = defaultValue,
            ).asCompletableFuture()
        }
    }
}

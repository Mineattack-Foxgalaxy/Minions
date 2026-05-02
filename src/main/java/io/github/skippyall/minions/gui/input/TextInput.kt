package io.github.skippyall.minions.gui.input

import eu.pb4.sgui.api.elements.GuiElementBuilder
import eu.pb4.sgui.api.gui.AnvilInputGui
import io.github.skippyall.minions.gui.MinionsGui
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
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
    private var isConfirm = false

    val job = Job()

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
                if (job.isActive && !isConfirm) {
                    job.cancel()
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
        result?.ifSuccess { _: T? ->
            isConfirm = true
        }
        job.complete()
    }

    companion object {
        @JvmStatic
        suspend fun <T>input(
            gui: MinionsGui,
            title: Component,
            defaultValue: String,
            parser: suspend (String) -> Result<T, Component>,
        ): T? {
            val input = TextInput(
                parent = gui,
                title = title,
                defaultValue = defaultValue,
                parser = parser,
            )

            input.job.join()

            return input.result?.getOrDefault(null)
        }

        @JvmStatic
        fun <T>inputFuture(
            gui: MinionsGui,
            title: Component,
            defaultValue: String,
            parser: (String) -> Result<T, Component>,
        ): CompletableFuture<T?> {
            return gui.scope.async {
                val input = TextInput(
                    parent = gui,
                    title = title,
                    defaultValue = defaultValue,
                    parser = parser,
                )

                input.job.join()

                return@async input.result?.getOrDefault(null)
            }.asCompletableFuture()
        }

        @JvmStatic
        suspend fun inputString(
            gui: MinionsGui,
            title: Component,
            defaultValue: String,
        ): String? {
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
            return gui.scope.async {
                inputString(
                    gui,
                    title,
                    defaultValue
                )
            }.asCompletableFuture()
        }

        @JvmStatic
        suspend fun inputLong(
            gui: MinionsGui,
            title: Component,
            defaultValue: Long,
        ): Long? {
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
            return gui.scope.async {
                inputLong(
                    gui,
                    title,
                    defaultValue
                )
            }.asCompletableFuture()
        }

        @JvmStatic
        suspend fun inputDouble(
            gui: MinionsGui,
            title: Component,
            defaultValue: Double,
        ): Double? {
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
            return gui.scope.async {
                inputDouble(
                    gui,
                    title,
                    defaultValue
                )
            }.asCompletableFuture()
        }
    }
}

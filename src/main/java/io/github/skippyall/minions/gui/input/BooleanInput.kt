package io.github.skippyall.minions.gui.input

import eu.pb4.sgui.api.elements.GuiElementBuilder
import eu.pb4.sgui.api.gui.SimpleGui
import io.github.skippyall.minions.gui.MinionsGui
import io.github.skippyall.minions.gui.minion.SimpleMinionsGui
import net.minecraft.network.chat.Component
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.Items
import java.util.concurrent.CompletableFuture

object BooleanInput {
    @JvmStatic
    @JvmOverloads
    fun confirm(
        parent: MinionsGui,
        title: Component,
        falseText: Component = Component.translatable("minions.gui.abort"),
        trueText: Component = Component.translatable("minions.gui.confirm")
    ): CompletableFuture<Boolean> {
        val future = CompletableFuture<Boolean>()

        SimpleMinionsGui(parent) { onClose: Runnable, me: SimpleMinionsGui ->
            val gui: SimpleGui = object : SimpleGui(MenuType.GENERIC_3x3, parent.viewer, false) {
                override fun onPlayerClose(success: Boolean) {
                    future.complete(false)
                    onClose.run()
                }
            }
            gui.setTitle(title)

            gui.setSlot(
                3, GuiElementBuilder(Items.REDSTONE_BLOCK)
                    .setName(falseText)
                    .setCallback(Runnable {
                        future.complete(false)
                        me.goBack()
                    })
            )

            gui.setSlot(
                5, GuiElementBuilder(Items.EMERALD_BLOCK)
                    .setName(trueText)
                    .setCallback(Runnable {
                        future.complete(true)
                        me.goBack()
                    })
            )

            gui.open()
            gui
        }
        return future
    }
}
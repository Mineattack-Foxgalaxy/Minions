package io.github.skippyall.minions.gui

import eu.pb4.sgui.api.elements.GuiElementBuilder
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.Items

abstract class MinionsGui {
    protected val parent: MinionsGui?
    @JvmField
    val viewer: ServerPlayer
    protected var child: MinionsGui? = null
    private var open = true

    private val job: CompletableJob
    val scope: CoroutineScope

    constructor(parent: MinionsGui) {
        this.viewer = parent.viewer
        this.parent = parent
        parent.child = this
        job = Job(parent.job)
        scope = CoroutineScope(Dispatchers.Unconfined.plus(CoroutineName("MinionsGui")).plus(job))
    }

    constructor(viewer: ServerPlayer) {
        this.viewer = viewer
        this.parent = null
        job = Job(null)
        scope = CoroutineScope(Dispatchers.Unconfined.plus(CoroutineName("MinionsGui")).plus(job))
    }

    protected abstract fun open()

    protected fun reopen() {
        open()
    }

    fun onBackingClosed() {
        if (child != null && child!!.open) {
            return
        }

        close(true)
    }

    @JvmOverloads
    fun close(alreadyClosed: Boolean = false) {
        if (open) {
            scope.cancel(null)
            open = false
            if (child != null) {
                child!!.close(alreadyClosed)
            } else if (!alreadyClosed) {
                closeBacking()
            }
        }
    }

    fun goBack() {
        if (parent != null) {
            open = false
            parent.child = null
            parent.reopen()
            close(true)
        } else {
            close(false)
        }
    }

    fun backButton(): GuiElementBuilder? {
        return GuiElementBuilder(Items.MANGROVE_DOOR)
            .setName(Component.translatable("gui.back"))
            .setCallback(Runnable { this.goBack() })
    }

    protected abstract fun closeBacking()
}

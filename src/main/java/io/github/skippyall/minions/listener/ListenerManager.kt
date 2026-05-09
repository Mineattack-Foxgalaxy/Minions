package io.github.skippyall.minions.listener

import java.util.concurrent.CopyOnWriteArraySet

open class ListenerManager<T>(
    protected val listeners: MutableSet<T> = CopyOnWriteArraySet(),
    val onChange: () -> Unit = {},
) : MutableIterable<T> by listeners {

    fun addListener(listener: T) {
        listeners.add(listener)
        onChange()
    }

    fun removeListener(listener: T) {
        listeners.remove(listener)
        onChange()
    }

    override fun iterator(): MutableIterator<T> {
        val iterator = listeners.iterator()
        return object : MutableIterator<T> by iterator {
            override fun remove() {
                iterator.remove()
                onChange()
            }
        }
    }
}

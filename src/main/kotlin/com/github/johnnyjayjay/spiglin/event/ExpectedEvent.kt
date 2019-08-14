package com.github.johnnyjayjay.spiglin.event

import org.bukkit.event.Event

data class ExpectedEvent<in T : Event>(
    private val amount: Int = 0,
    private val predicate: (T) -> Boolean = { true },
    private val action: (T) -> Unit
) {
    var done: Boolean = false
        internal set

    var callCount = 0
        private set

    fun call(event: T) {
        if (done)
            return

        if (predicate(event)) {
            action(event)
            if (++callCount == amount) {
                done = true
            }
        }
    }
}
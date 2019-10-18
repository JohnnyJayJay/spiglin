package com.github.johnnyjayjay.spiglin.event

import org.bukkit.event.Event

data class Expectation<in T : Event>(
    private val amount: Int = 1,
    private val predicate: (T) -> Boolean = { true },
    private val action: (T) -> Unit
) {

    var fulfilled: Boolean = false
        internal set

    private var callCount = 0

    fun call(event: T) {
        if (fulfilled)
            return

        if (predicate(event)) {
            action(event)
            if (++callCount == amount) {
                fulfilled = true
            }
        }
    }

    fun cancel() {
        fulfilled = true
    }

    companion object {
        const val INDEFINITE_AMOUNT = 0
    }
}
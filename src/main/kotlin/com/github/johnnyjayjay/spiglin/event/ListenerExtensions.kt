package com.github.johnnyjayjay.spiglin.event

import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import java.util.concurrent.TimeUnit

fun Plugin.register(listener: Listener) {
    Bukkit.getPluginManager().registerEvents(listener, this)
}

inline fun <reified T : Event> Plugin.hear(crossinline action: (T) -> Unit) {
    register(object : Listener {
        @EventHandler
        fun onEvent(event: T) {
            action(event)
        }
    })
}

inline fun <reified T : Event> expect(
    amount: Int = 1,
    noinline predicate: (T) -> Boolean = { true },
    timeout: Long = 0,
    timeoutUnit: TimeUnit = TimeUnit.MILLISECONDS,
    noinline timeoutAction: () -> Unit = {},
    noinline action: (T) -> Unit
): ExpectedEvent<T> {
    val expectation = ExpectedEvent(amount, predicate, action)
    EventExpecter.add(T::class, expectation, timeout, timeoutUnit, timeoutAction)
    return expectation
}


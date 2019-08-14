package com.github.johnnyjayjay.spiglin

import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

fun Plugin.register(listener: Listener) {
    Bukkit.getPluginManager().registerEvents(listener, this)
}

fun <T : Event> Plugin.hear(action: (T) -> Unit) {
    register(SingleEventListener(action))
}

private class SingleEventListener<T : Event>(private val action: (T) -> Unit) : Listener {
    @EventHandler
    fun onEvent(event: T) = action(event)
}

object EventExpecter : Listener {

    var scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    private val expectedEvents: Multimap<KClass<out Event>, ExpectedEvent<Nothing>> =
        MultimapBuilder.hashKeys().hashSetValues().build()

    fun <T : Event> add(
        type: KClass<T>,
        amount: Int = -1,
        predicate: (T) -> Boolean = { true },
        timeout: Long = -1,
        timeoutUnit: TimeUnit = TimeUnit.MILLISECONDS,
        timeoutAction: () -> Unit = {},
        action: (T) -> Unit
    ): ExpectedEvent<T> {
        val expectation = ExpectedEvent(amount, predicate, action)
        expectedEvents.put(type, expectation)
        if (timeout > 0) {
            scheduler.schedule({
                timeoutAction()
                remove(type, expectation)
            }, timeout, timeoutUnit)
        }
        return expectation
    }

    fun <T : Event> remove(type: KClass<T>) {
        synchronized(this) {
            expectedEvents.removeAll(type).forEach { it.done = true }
        }
    }

    fun <T : Event> remove(type: KClass<T>, expectation: ExpectedEvent<T>) {
        synchronized(this) {
            expectedEvents.remove(type, expectation)
            expectation.done = true
        }
    }

    @EventHandler
    fun onEvent(event: Event) {
        expectedEvents[event.javaClass.kotlin].forEach {
            (it as ExpectedEvent<Event>).call(event)
            if (it.done) {
                remove(event.javaClass.kotlin, it)
            }
        }
    }

    data class ExpectedEvent<in T : Event> internal constructor(
        private val amount: Int,
        private val predicate: (T) -> Boolean,
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
                if (++callCount >= amount) {
                    done = true
                }
            }
        }
    }

}
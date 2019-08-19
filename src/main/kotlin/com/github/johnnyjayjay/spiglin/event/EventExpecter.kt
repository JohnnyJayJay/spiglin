package com.github.johnnyjayjay.spiglin.event

import com.google.common.collect.Multimap
import com.google.common.collect.MultimapBuilder
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

object EventExpecter : Listener {

    var scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    private val expectedEvents: Multimap<KClass<out Event>, Expectation<*>> =
        MultimapBuilder.hashKeys().hashSetValues().build()

    fun <T : Event> add(
        type: KClass<T>,
        expectation: Expectation<T>,
        timeout: Long = 0,
        timeoutUnit: TimeUnit = TimeUnit.MILLISECONDS,
        timeoutAction: () -> Unit = {}
    ) {
        expectedEvents.put(type, expectation)
        if (timeout > 0) {
            scheduler.schedule({
                if (!expectation.fulfilled) {
                    timeoutAction()
                    remove(type, expectation)
                }
            }, timeout, timeoutUnit)
        }
    }

    fun <T : Event> removeAll(type: KClass<T>) {
        synchronized(this) {
            expectedEvents.removeAll(type).forEach { it.fulfilled = true }
        }
    }

    fun <T : Event> remove(type: KClass<T>, expectation: Expectation<T>) {
        synchronized(this) {
            expectedEvents.remove(type, expectation)
            expectation.fulfilled = true
        }
    }

    @EventHandler
    fun onEvent(event: Event) {
        expectedEvents[event.javaClass.kotlin].forEach {
            @Suppress("UNCHECKED_CAST")
            (it as Expectation<Event>).call(event)
            if (it.fulfilled) {
                remove(event.javaClass.kotlin, it)
            }
        }
    }
}


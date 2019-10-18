package com.github.johnnyjayjay.spiglin.event

import com.github.johnnyjayjay.spiglin.PluginManager
import org.apache.commons.lang.Validate
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

interface ExtendedListener<in T : Event> : Listener {
    fun onEvent(event: T)
}

inline fun <reified T : Event> listener(
    crossinline action: Listener.(T) -> Unit
) = object : ExtendedListener<T> {
    override fun onEvent(event: T) {
        action(event)
    }
}

inline fun <reified T : Event> Plugin.hear(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    crossinline action: Listener.(T) -> Unit
) = listener(action).also { it.register(this, priority, ignoreCancelled) }

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Event> ExtendedListener<T>.register(
    plugin: Plugin,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false
) = register<T>(plugin, priority, ignoreCancelled) { listener, event ->
    (listener as ExtendedListener<T>).onEvent(event as T)
}

inline fun <reified T : Event> Listener.register(
    plugin: Plugin,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    noinline eventExecutor: (Listener, Event) -> Unit
) = PluginManager.registerEvent(T::class.java, this, priority, eventExecutor, plugin, ignoreCancelled)

fun Listener.register(plugin: Plugin) = PluginManager.registerEvents(this, plugin)

fun Listener.unregister() = HandlerList.unregisterAll(this)

var expectationPool: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    set(value) {
        Validate.isTrue(!value.isShutdown, "ScheduledExecutorService must not be shut down")
        field = value
    }

inline fun <reified T : Event> Plugin.expect(
    amount: Int = 1,
    noinline predicate: (T) -> Boolean = { true },
    timeout: Long = Expectation.INDEFINITE_AMOUNT,
    timeoutUnit: TimeUnit = TimeUnit.MILLISECONDS,
    noinline timeoutAction: () -> Unit = {},
    noinline action: (T) -> Unit
): Expectation<T> {
    val expectation = Expectation(amount, predicate, action)
    val listener = hear<T> {
        expectation.call(it)
        if (expectation.fulfilled) {
            unregister()
        }
    }

    if (timeout > 0) {
        expectationPool.schedule({
            if (!expectation.fulfilled) {
                timeoutAction()
                listener.unregister()
            }
        }, timeout, timeoutUnit)
    }
    return expectation
}


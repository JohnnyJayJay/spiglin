package com.github.johnnyjayjay.spiglin.event

import com.github.johnnyjayjay.spiglin.PluginManager
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * An functional extension of the [Listener] marker interface that can be used for single-event listeners
 * as an alternative to the annotation-based system provided by Bukkit.
 *
 * @param T The type of event to listen for
 *
 * @see ExtendedListener.register
 */
interface ExtendedListener<in T : Event> : Listener {
    fun onEvent(event: T)
}

/**
 * Creates an ad hoc listener based on [ExtendedListener] with the specified action.
 *
 * @param action The action to run if such an event is fired
 * @param T The type of event to listen for
 *
 * @return The created listener
 *
 * @see ExtendedListener.register
 */
inline fun <reified T : Event> listener(
    crossinline action: Listener.(T) -> Unit
) = object : ExtendedListener<T> {
    override fun onEvent(event: T) {
        action(event)
    }
}

/**
 * Creates an ad hoc listener based on [ExtendedListener] and instantly registers it with the specified arguments.
 *
 * @param action The action to run if such an event is fired
 * @param T The type of event to listen for
 *
 * @return The created listener
 *
 * @see PluginManager.registerEvent
 */
inline fun <reified T : Event> Plugin.hear(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    crossinline action: Listener.(T) -> Unit
) = listener(action).also { it.register(this, priority, ignoreCancelled) }

/**
 * Registers an instance of [ExtendedListener] using its designated [EventExecutor].
 *
 * @see PluginManager.registerEvent
 */
@Suppress("UNCHECKED_CAST")
inline fun <reified T : Event> ExtendedListener<T>.register(
    plugin: Plugin,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false
) = register<T>(plugin, priority, ignoreCancelled) { listener, event ->
    (listener as ExtendedListener<T>).onEvent(event as T)
}

/**
 * Registers a listener - delegates directly to [PluginManager.registerEvent]
 */
inline fun <reified T : Event> Listener.register(
    plugin: Plugin,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    noinline eventExecutor: (Listener, Event) -> Unit
) = PluginManager.registerEvent(T::class.java, this, priority, eventExecutor, plugin, ignoreCancelled)


/**
 * Registers a listener via the annotation-based system provided by Bukkit ([EventHandler])
 *
 * @see PluginManager.registerEvents
 */
fun Listener.register(plugin: Plugin) = PluginManager.registerEvents(this, plugin)

/**
 * Unregisters a listener from all [HandlerList]s.
 */
fun Listener.unregister() = HandlerList.unregisterAll(this)

/**
 * The thread pool used for expectation timeout. Must not be shut down if timeouts are used.
 */
var expectationPool: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

/**
 * Creates an expectation, which is met if the specified amount of events
 * that match the given predicate is fired before the timeout.
 * Every time an event is fired, the specified action is called.
 *
 * If the expectation is met or if it times out, the listener is unregistered automatically.
 *
 * @param amount The amount of expected events
 * @param predicate A function determining whether an event should be accepted or not
 * @param timeout A timeout after which, should this expectation not have been met,
 * the listener is unregistered and the timeoutAction is run.
 * @param timeoutUnit The TimeUnit of the provided timeout
 * @param timeoutAction The action to run if the expectation times out
 * @param action The action to run for each event that meets the expectation
 *
 * @return
 */
inline fun <reified T : Event> Plugin.expect(
    amount: Int = 1,
    crossinline predicate: (T) -> Boolean = { true },
    timeout: Long = 0,
    timeoutUnit: TimeUnit = TimeUnit.MILLISECONDS,
    crossinline timeoutAction: () -> Unit = {},
    crossinline action: (T) -> Unit
): ExtendedListener<T> {
    var callCount = 0
    val listener = hear<T> {
        if (predicate(it)) {
            action(it)
            if (++callCount >= amount) {
                unregister()
            }
        }
    }

    if (timeout > 0) {
        expectationPool.schedule({
            if (callCount < amount) {
                timeoutAction()
                listener.unregister()
            }
        }, timeout, timeoutUnit)
    }
    return listener
}


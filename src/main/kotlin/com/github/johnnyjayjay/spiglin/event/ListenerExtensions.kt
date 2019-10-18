package com.github.johnnyjayjay.spiglin.event

import com.github.johnnyjayjay.spiglin.PluginManager
import com.github.johnnyjayjay.spiglin.event.subject.*
import org.apache.commons.lang.Validate
import org.bukkit.Chunk
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.event.*
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

inline fun <reified T : Event> Plugin.hear(
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    noinline action: Listener.(T) -> Unit
): Listener {
    return ListenerGenerator.generateListener(T::class, action, priority, ignoreCancelled)
        .also { it.register(this) }
}

fun Listener.register(plugin: Plugin) {
    PluginManager.registerEvents(this, plugin)
}

fun Listener.unregister() {
    HandlerList.unregisterAll(this)
}

internal val subjects = mapOf<KClass<*>, GenericSubjectListener<*>>(
    Block::class to BlockSubjectListener,
    Chunk::class to ChunkSubjectListener,
    Entity::class to EntitySubjectListener,
    Inventory::class to InventorySubjectListener,
    ItemStack::class to ItemSubjectListener,
    World::class to WorldSubjectListener
)

fun Plugin.registerSubjects(vararg subjectClasses: KClass<*>) {
    subjectClasses.asSequence()
        .map { subjects[it] }
        .map { it ?: throw IllegalArgumentException("Subject does not exist") }
        .forEach { it.register(this) }
}

var expectationPool = Executors.newSingleThreadScheduledExecutor()
    set(value) {
        Validate.isTrue(!value.isShutdown, "ScheduledExecutorService must not be shut down")
        field = value
    }

inline fun <reified T : Event> Plugin.expect(
    amount: Int = 1,
    noinline predicate: (T) -> Boolean = { true },
    timeout: Long = 0,
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


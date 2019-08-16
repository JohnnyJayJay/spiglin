package com.github.johnnyjayjay.spiglin.event

import com.github.johnnyjayjay.spiglin.interaction.*
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import java.lang.IllegalArgumentException
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

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

fun Plugin.registerExpecter() {
    register(EventExpecter)
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
        .forEach { register(it) }
}

inline fun <reified T : Event> expect(
    amount: Int = 1,
    noinline predicate: (T) -> Boolean = { true },
    timeout: Long = 0,
    timeoutUnit: TimeUnit = TimeUnit.MILLISECONDS,
    noinline timeoutAction: () -> Unit = {},
    noinline action: (T) -> Unit
): Expectation<T> {
    val expectation = Expectation(amount, predicate, action)
    EventExpecter.add(T::class, expectation, timeout, timeoutUnit, timeoutAction)
    return expectation
}


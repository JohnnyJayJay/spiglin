package com.github.johnnyjayjay.spiglin

import com.google.common.collect.Multimap
import org.bukkit.event.Event
import kotlin.reflect.KClass

// TODO better file structure, docs
interface Interactable<T : Interactable<T>> {

    val events: Multimap<KClass<out Event>, (Event) -> Unit>

    @Suppress("UNCHECKED_CAST")
    fun <E : Event> attach(eventClass: KClass<E>, action: (E) -> Unit): T {
        events.put(eventClass, action as (Event) -> Unit)
        return this as T
    }

    @Suppress("UNCHECKED_CAST")
    fun <E : Event> detach(eventClass: KClass<E>, action: (E) -> Unit): T {
        events.remove(eventClass, action)
        return this as T
    }

    @Suppress("UNCHECKED_CAST")
    fun <E : Event> detachAll(eventClass: KClass<E>): T {
        events.removeAll(eventClass)
        return this as T
    }

    @Suppress("UNCHECKED_CAST")
    fun detachAll(): T {
        events.clear()
        return this as T
    }

    fun call(event: Event) {
        events[event.javaClass.kotlin]
            ?.forEach { it(event) }
    }
}
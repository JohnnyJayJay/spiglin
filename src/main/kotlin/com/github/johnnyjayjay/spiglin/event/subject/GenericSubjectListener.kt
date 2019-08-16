package com.github.johnnyjayjay.spiglin.event.subject

import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.util.*

abstract class GenericSubjectListener<T> : Listener {

    private val registry: MutableMap<T, MutableCollection<(Event) -> Unit>> = WeakHashMap()

    @EventHandler
    fun onEvent(event: Event) {
        fromEvent(event).forEach { subject ->
            registry[subject]?.forEach { action ->
                action(event)
            }
        }
    }

    fun subscribe(subject: T, action: (Event) -> Unit) {
        registry.computeIfAbsent(subject) {
            mutableSetOf()
        }.add(action)
    }

    fun unsubscribe(subject: T, action: (Event) -> Unit) {
        registry[subject]?.remove(action)
    }

    fun unsubscribe(subject: T) {
        registry.remove(subject)
    }

    protected abstract fun fromEvent(event: Event): Collection<T>;

}


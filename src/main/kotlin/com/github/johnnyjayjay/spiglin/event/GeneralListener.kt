package com.github.johnnyjayjay.spiglin.event

import net.bytebuddy.implementation.bind.annotation.Argument
import net.bytebuddy.implementation.bind.annotation.This
import org.bukkit.event.Event
import org.bukkit.event.Listener
import kotlin.ClassCastException

data class GeneralListener<T : Event>(val action: Listener.(T) -> Unit) {
    @Suppress("UNCHECKED_CAST")
    fun onEvent(@This listener: Listener, @Argument(0) event: Event) {
        try {
            listener.action(event as T) // TODO catch other exceptions
        } catch (e: ClassCastException) {
            throw AssertionError(e)
        }
    }
}
package com.github.johnnyjayjay.spiglin.event.subject

import org.bukkit.event.Event
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.inventory.Inventory

object InventorySubjectListener : GenericSubjectListener<Inventory>() {
    override fun fromEvent(event: Event) =
        if (event is InventoryEvent) listOf(event.inventory) else emptyList()
}

inline fun <reified T : Event> Inventory.on(crossinline action: (T) -> Unit) {
    InventorySubjectListener.subscribe(this) {
        if (it is T)
            action(it)
    }
}
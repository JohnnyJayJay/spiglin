package com.github.johnnyjayjay.spiglin.event.subject

import org.bukkit.entity.Entity
import org.bukkit.event.Event
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityEvent
import org.bukkit.event.player.PlayerEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.vehicle.VehicleEvent

// TODO add block events
object EntitySubjectListener : GenericSubjectListener<Entity>() {

    override fun fromEvent(event: Event): Collection<Entity> {
        val entities = mutableListOf<Entity>()
        with(entities) {
            when (event) {
                is PlayerInteractEntityEvent -> {
                    add(event.player)
                    add(event.rightClicked)
                }
                is EntityDamageByEntityEvent -> {
                    add(event.damager)
                    add(event.entity)
                }
                is PlayerEvent -> add(event.player)
                is EntityEvent -> add(event.entity)
                is VehicleEvent -> add(event.vehicle)
                else -> {}
            }
        }
        return entities
    }

}

inline fun <reified T : Event> Entity.on(crossinline action: (T) -> Unit) {
    EntitySubjectListener.subscribe(this) {
        if (it is T)
            action(it)
    }
}
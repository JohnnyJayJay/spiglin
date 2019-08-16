package com.github.johnnyjayjay.spiglin.event.subject

import org.bukkit.World
import org.bukkit.event.Event
import org.bukkit.event.weather.WeatherEvent
import org.bukkit.event.world.WorldEvent

object WorldSubjectListener : GenericSubjectListener<World>() {

    override fun fromEvent(event: Event): Collection<World> {
        val world = when (event) {
            is WeatherEvent -> event.world
            is WorldEvent -> event.world
            else -> null
        }
        return if (world == null) emptyList() else listOf(world)
    }

}

inline fun <reified T : Event> World.on(crossinline action: (T) -> Unit) {
    WorldSubjectListener.subscribe(this) {
        if (it is T)
            action(it)
    }
}
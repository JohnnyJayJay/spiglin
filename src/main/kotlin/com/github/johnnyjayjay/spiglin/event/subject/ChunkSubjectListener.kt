package com.github.johnnyjayjay.spiglin.event.subject

import org.bukkit.Chunk
import org.bukkit.event.Event
import org.bukkit.event.world.ChunkEvent

object ChunkSubjectListener : GenericSubjectListener<Chunk>() {
    override fun fromEvent(event: Event) =
        if (event is ChunkEvent) listOf(event.chunk) else emptyList()
}

inline fun <reified T : Event> Chunk.on(crossinline action: (T) -> Unit) {
    ChunkSubjectListener.subscribe(this) {
        if (it is T)
            action(it)
    }
}
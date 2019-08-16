package com.github.johnnyjayjay.spiglin.event.subject

import org.bukkit.block.Block
import org.bukkit.event.Event
import org.bukkit.event.block.BlockEvent
import org.bukkit.event.player.PlayerInteractEvent

object BlockSubjectListener : GenericSubjectListener<Block>() {

    override fun fromEvent(event: Event): Collection<Block> {
        val block = when (event) {
            is PlayerInteractEvent -> event.clickedBlock
            is BlockEvent -> event.block
            else -> null
        }
        return if (block != null) listOf(block) else emptyList()
    }

}

inline fun <reified T : Event> Block.on(crossinline action: (T) -> Unit) {
    BlockSubjectListener.subscribe(this) {
        if (it is T)
            action(it)
    }
}
package com.github.johnnyjayjay.spiglin.block

import com.github.johnnyjayjay.spiglin.Interactable
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import org.bukkit.block.Block
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import kotlin.reflect.KClass

object BlockInteractionListener : Listener {

    @EventHandler
    fun onEvent(event: Event) {
        TODO()
    }

}

open class InteractiveBlock(block: Block) : Interactable<InteractiveBlock>, Block by block {

    override val events: Multimap<KClass<out Event>, (Event) -> Unit> = ArrayListMultimap.create()

}
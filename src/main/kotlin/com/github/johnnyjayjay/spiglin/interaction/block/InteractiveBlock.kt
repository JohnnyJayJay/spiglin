package com.github.johnnyjayjay.spiglin.interaction.block

import com.github.johnnyjayjay.spiglin.interaction.Interactable
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import org.bukkit.block.Block
import org.bukkit.event.Event
import kotlin.reflect.KClass

open class InteractiveBlock(block: Block) : Interactable<InteractiveBlock>, Block by block {

    override val events: Multimap<KClass<out Event>, (Event) -> Unit> = ArrayListMultimap.create()

}

fun Block.interactive() = InteractiveBlock(this)
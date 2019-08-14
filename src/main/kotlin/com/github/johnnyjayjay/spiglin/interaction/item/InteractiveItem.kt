package com.github.johnnyjayjay.spiglin.interaction.item

import com.github.johnnyjayjay.spiglin.interaction.Interactable
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import org.bukkit.event.Event
import org.bukkit.inventory.ItemStack
import kotlin.reflect.KClass

/**
 * An ItemStack with two actions attached, one for [PlayerInteractEvent]s,
 * and one for [InventoryClickEvent]s involving this item.
 *
 * @constructor Creates an [InteractiveItem] that derives from the given delegate ItemStack.
 *              Changes to that ItemStack will not affect this [InteractiveItem].
 */
open class InteractiveItem(delegate: ItemStack) : ItemStack(delegate),
    Interactable<InteractiveItem> {

    override val events: Multimap<KClass<out Event>, (Event) -> Unit> =
        ArrayListMultimap.create()

}

/**
 * Returns an ItemStack
 */
fun ItemStack.interactive() = InteractiveItem(this)


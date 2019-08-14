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
 * @constructor Creates an [InteractableItem] that derives from the given delegate ItemStack.
 *              Changes to that ItemStack will not affect this [InteractiveItem].
 */
open class InteractableItem(delegate: ItemStack) : ItemStack(delegate),
    Interactable<InteractableItem> {

    override val events: Multimap<KClass<out Event>, (Event) -> Unit> =
        ArrayListMultimap.create()

}

/**
 * Returns a new [InteractableItem] based on this ItemStack.
 */
fun ItemStack.interactable() = InteractableItem(this)

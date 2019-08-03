package com.github.johnnyjayjay.spiglin.item

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

/**
 * A listener used to react to [InteractiveItem] actions.
 * It must be registered as a listener to Bukkit to enable [InteractiveItem]s.
 * Everything apart from registering is handled internally.
 */
object InteractiveItemListener : Listener {

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        val item = event.currentItem
        if (item is InteractiveItem && item.active) {
            item.clickAction(event)
        }
    }

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val item = event.item
        if (item is InteractiveItem && item.active) {
            item.interactAction(event)
        }
    }
}

/**
 * An ItemStack with two actions attached, one for [PlayerInteractEvent]s,
 * and one for [InventoryClickEvent]s involving this item.
 *
 * @constructor Creates an [InteractiveItem] that derives from the given delegate ItemStack.
 *              Changes to that ItemStack will not affect this [InteractiveItem].
 */
class InteractiveItem(delegate: ItemStack) : ItemStack(delegate) {

    /** A boolean value determining whether interaction is enabled for this item. */
    var active: Boolean = true

    /** A function called when this item is clicked in an inventory. */
    var clickAction: (InventoryClickEvent) -> Unit = {}

    /** A function called when a player interacts with this item in their hand. */
    var interactAction: (PlayerInteractEvent) -> Unit = {}

    infix fun whenInteracted(interactAction: (PlayerInteractEvent) -> Unit) =
        this.apply { this.interactAction = interactAction }

    infix fun whenClicked(clickAction: (InventoryClickEvent) -> Unit) =
        this.apply { this.clickAction = clickAction }
}

/**
 * Creates and returns a new [InteractiveItem] based on this ItemStack.
 */
fun ItemStack.interactive() = InteractiveItem(this)

/**
 * Creates a new [InteractiveItem], sets its [InteractiveItem.clickAction] and returns the result.
 */
infix fun ItemStack.whenClicked(clickAction: (InventoryClickEvent) -> Unit) =
    interactive() whenClicked clickAction

/**
 * Creates a new [InteractiveItem], sets its [InteractiveItem.interactAction] and returns the result.
 */
infix fun ItemStack.whenInteracted(interactAction: (PlayerInteractEvent) -> Unit) =
    interactive() whenInteracted interactAction

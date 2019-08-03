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

class InteractiveItem(delegate: ItemStack) : ItemStack(delegate) {

    var active: Boolean = true
    var clickAction: (InventoryClickEvent) -> Unit = {}
    var interactAction: (PlayerInteractEvent) -> Unit = {}

    infix fun whenInteracted(interactAction: (PlayerInteractEvent) -> Unit) =
        this.apply { this.interactAction = interactAction }

    infix fun whenClicked(clickAction: (InventoryClickEvent) -> Unit) =
        this.apply { this.clickAction = clickAction }
}

fun ItemStack.interactive() = InteractiveItem(this)

infix fun ItemStack.whenClicked(clickAction: (InventoryClickEvent) -> Unit) =
    interactive() whenClicked clickAction

infix fun ItemStack.whenInteracted(interactAction: (PlayerInteractEvent) -> Unit) =
    interactive() whenInteracted interactAction

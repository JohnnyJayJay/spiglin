package com.github.johnnyjayjay.spiglin.inventory

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

/**
 * A listener used to react to item clicks in registered inventories.
 * It must be registered as a listener to Bukkit to enable interactive inventories.
 * Everything apart from registering is handled internally.
 */
object InteractiveInventoryListener : Listener {

    private val inventories: MutableSet<Inventory> = mutableSetOf()

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.inventory in inventories) {
            val item = event.currentItem
            if (item is ClickableInventoryItem) {
                item.action(event)
            }
        }
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        remove(event.inventory)
    }

    internal fun add(inventory: Inventory) = inventories.add(inventory)

    internal fun remove(inventory: Inventory) = inventories.remove(inventory)

    internal fun contains(inventory: Inventory) = inventory in inventories

}

/**
 * An ItemStack with an action attached that will be executed should this ItemStack be
 * clicked in an [interactive Inventory][Inventory.interactive].
 *
 * @property stack The [ItemStack][org.bukkit.inventory.ItemStack] this derives from.
 * @property action The function to be called when this item is clicked in an interactive inventory.
 */
data class ClickableInventoryItem(val stack: ItemStack, val action: (InventoryClickEvent) -> Unit) : ItemStack(stack)

/**
 * Attaches an action to an existing ItemStack to create a [ClickableInventoryItem].
 */
infix fun ItemStack.withAction(action: (InventoryClickEvent) -> Unit): ClickableInventoryItem {
    return ClickableInventoryItem(this, action)
}
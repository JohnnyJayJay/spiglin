package com.github.johnnyjayjay.spiglin.inventory

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object ItemInteractionListener : Listener {

    private val inventories: MutableSet<Inventory> = mutableSetOf()

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.inventory in inventories) {
            val item = event.currentItem
            if (item is ClickableItem) {
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

data class ClickableItem(val stack: ItemStack, val action: (InventoryClickEvent) -> Unit) : ItemStack(stack)

infix fun ItemStack.withAction(action: (InventoryClickEvent) -> Unit): ItemStack {
    return ClickableItem(this, action)
}
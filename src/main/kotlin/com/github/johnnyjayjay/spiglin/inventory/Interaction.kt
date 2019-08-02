package com.github.johnnyjayjay.spiglin.inventory

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object ClickListener : Listener {

    internal val inventories: MutableMap<Inventory, Set<ClickableItem>> = mutableMapOf()

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        inventories[event.inventory]?.firstOrNull {
            it == event.currentItem
        }?.action(event)
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        if (event.inventory in inventories) {
            inventories.remove(event.inventory)
        }
    }

}

internal data class ClickableItem(val stack: ItemStack, val action: (InventoryClickEvent) -> Unit) : ItemStack(stack)

infix fun ItemStack.withAction(action: (InventoryClickEvent) -> Unit): ItemStack {
    return ClickableItem(this, action)
}
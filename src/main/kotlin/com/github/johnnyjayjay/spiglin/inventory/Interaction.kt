package com.github.johnnyjayjay.spiglin.inventory

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

object ItemInteractionListener : Listener {

    private val clickables: MutableSet<ClickableItem> = mutableSetOf()

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        val item = event.currentItem
        if (item in clickables) {
            (item as ClickableItem).action(event)
        }
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        clickables.removeAll(event.inventory.contents.filterIsInstance<ClickableItem>())
    }

    fun add(inventory: Inventory) {
        clickables.addAll(inventory.contents.filterIsInstance<ClickableItem>())
    }

}

data class ClickableItem(val stack: ItemStack, val action: (InventoryClickEvent) -> Unit) : ItemStack(stack)

infix fun ItemStack.withAction(action: (InventoryClickEvent) -> Unit): ItemStack {
    return ClickableItem(this, action)
}
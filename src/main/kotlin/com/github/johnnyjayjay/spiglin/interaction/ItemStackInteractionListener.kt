package com.github.johnnyjayjay.spiglin.interaction

import com.github.johnnyjayjay.spiglin.inventory.get
import org.bukkit.event.Event
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.entity.EntityDropItemEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.ItemMergeEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.inventory.InventoryPickupItemEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack

/**
 * A listener used to react to [InteractableItem] actions.
 * It must be registered as a listener to Bukkit to enable [InteractableItem]s.
 * Everything apart from registering is handled internally.
 */
object ItemStackInteractionListener : InteractionListener<ItemStack>() {

    override fun fromEvent(event: Event): Collection<ItemStack> {
        val items = mutableListOf<ItemStack?>()
        with(items) {
            when (event) {
                is PlayerInteractEvent -> add(event.item)
                is PlayerDropItemEvent -> add(event.itemDrop.itemStack)
                is PlayerItemBreakEvent -> add(event.brokenItem)
                is PlayerItemConsumeEvent -> add(event.item)
                is PlayerItemDamageEvent -> add(event.item)
                is PlayerItemHeldEvent -> {
                    val inventory = event.player.inventory
                    add(inventory[event.previousSlot])
                    add(inventory[event.newSlot])
                }
                is EntityPickupItemEvent -> add(event.item.itemStack)
                is EntityDropItemEvent -> add(event.itemDrop.itemStack)
                is ItemMergeEvent -> add(event.entity.itemStack)
                is EnchantItemEvent -> add(event.item)
                is InventoryClickEvent -> add(event.currentItem)
                is InventoryDragEvent -> {
                    add(event.cursor)
                    add(event.oldCursor)
                    addAll(event.newItems.values)
                }
                is InventoryMoveItemEvent -> add(event.item)
                is InventoryPickupItemEvent -> add(event.item.itemStack)
                is BlockDropItemEvent -> addAll(event.items.map { it.itemStack })
                else -> {}
            }
        }
        return items.filterNotNull()
    }

}

inline fun <reified T : Event> ItemStack.on(crossinline action: (T) -> Unit) {
    ItemStackInteractionListener.subscribe(this) {
        if (it is T)
            action(it)
    }
}
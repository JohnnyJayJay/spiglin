package com.github.johnnyjayjay.spiglin.interaction.item

import com.github.johnnyjayjay.spiglin.inventory.get
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
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

/**
 * A listener used to react to [InteractableItem] actions.
 * It must be registered as a listener to Bukkit to enable [InteractableItem]s.
 * Everything apart from registering is handled internally.
 */
object InteractableItemListener : Listener {

    @EventHandler
    fun onEvent(event: Event) {
        val item = when (event) {
            is PlayerInteractEvent -> event.item
            is PlayerDropItemEvent -> event.itemDrop.itemStack
            is PlayerItemBreakEvent -> event.brokenItem
            is PlayerItemConsumeEvent -> event.item
            is PlayerItemDamageEvent -> event.item
            is PlayerItemHeldEvent -> event.player.inventory[event.newSlot]
            is PlayerTakeLecternBookEvent -> event.book
            is EntityPickupItemEvent -> event.item.itemStack
            is EntityDropItemEvent -> event.itemDrop.itemStack
            is ItemMergeEvent -> event.entity.itemStack
            is EnchantItemEvent -> event.item
            is InventoryClickEvent -> event.currentItem
            is InventoryDragEvent -> event.oldCursor
            is InventoryMoveItemEvent -> event.item
            is InventoryPickupItemEvent -> event.item.itemStack
            is BlockDropItemEvent -> {
                event.items.asSequence()
                    .map { it.itemStack }
                    .filterIsInstance<InteractableItem>()
                    .forEach { it.call(event) }
                return
            }
            else -> null
        }

        if (item is InteractableItem) {
            item.call(event)
        }
    }

}
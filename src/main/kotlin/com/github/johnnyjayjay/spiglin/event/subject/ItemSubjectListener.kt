package com.github.johnnyjayjay.spiglin.event.subject

import com.github.johnnyjayjay.spiglin.inventory.get
import org.bukkit.event.Event
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.enchantment.PrepareItemEnchantEvent
import org.bukkit.event.entity.EntityDropItemEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.ItemMergeEvent
import org.bukkit.event.inventory.*
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack

/**
 * A listener used to react to [InteractableItem] actions.
 * It must be registered as a listener to Bukkit to enable [InteractableItem]s.
 * Everything apart from registering is handled internally.
 */
object ItemSubjectListener : GenericSubjectListener<ItemStack>() {

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
                is PrepareItemEnchantEvent -> add(event.item)
                is PrepareAnvilEvent -> add(event.result)
                is PrepareItemCraftEvent -> add(event.recipe?.result)
                is InventoryClickEvent -> add(event.currentItem)
                is InventoryDragEvent -> addAll(event.newItems.values)
                is InventoryMoveItemEvent -> add(event.item)
                is InventoryPickupItemEvent -> add(event.item.itemStack)
                is BlockDropItemEvent -> addAll(event.items.map { it.itemStack })
                is BrewingStandFuelEvent -> add(event.fuel)
                is FurnaceBurnEvent -> add(event.fuel)
                is FurnaceSmeltEvent -> {
                    add(event.source)
                    add(event.result)
                }
                else -> {}
            }
            Unit
        }
        return items.filterNotNull()
    }

}

inline fun <reified T : Event> ItemStack.on(crossinline action: (T) -> Unit) {
    ItemSubjectListener.subscribe(this) {
        if (it is T)
            action(it)
    }
}
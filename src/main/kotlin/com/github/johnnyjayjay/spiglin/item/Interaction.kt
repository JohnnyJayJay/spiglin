package com.github.johnnyjayjay.spiglin.item

import com.github.johnnyjayjay.spiglin.inventory.get
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.entity.EntityDropItemEvent
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.entity.ItemMergeEvent
import org.bukkit.event.inventory.*
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack
import java.util.function.Consumer
import kotlin.reflect.KClass

/**
 * A listener used to react to [InteractiveItem] actions.
 * It must be registered as a listener to Bukkit to enable [InteractiveItem]s.
 * Everything apart from registering is handled internally.
 */
object InteractiveItemListener : Listener {

    @EventHandler
    fun onEvent(event: Event) {
        val item = when (event) {
            is PlayerInteractEvent -> event.item
            is PlayerDropItemEvent -> event.itemDrop.itemStack
            is PlayerItemBreakEvent -> event.brokenItem
            is PlayerItemConsumeEvent -> event.item
            is PlayerItemDamageEvent -> event.item
            is PlayerItemHeldEvent -> event.player.inventory[event.newSlot]
            is PlayerItemMendEvent -> event.item
            is PlayerRiptideEvent -> event.item
            is PlayerSwapHandItemsEvent -> {
                if (event.mainHandItem !is InteractiveItem && event.offHandItem is InteractiveItem) {
                    event.offHandItem
                } else {
                    event.mainHandItem
                }
            }
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
                    .filterIsInstance<InteractiveItem>()
                    .forEach { it.call(event) }
                return
            }
            else -> null
        }

        if (item is InteractiveItem) {
            item.call(event)
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

    private val events: Multimap<KClass<out Event>, Consumer<Event>> = ArrayListMultimap.create()

    inline infix fun <reified T : Event> attach(action: Action<T>) {
        attach(T::class, action)
    }

    inline infix fun <reified T : Event> detach(action: Action<T>) {
        detach(T::class, action)
    }

    fun <T : Event> attach(eventClass: KClass<T>, action: Action<T>?) {
        @Suppress("UNCHECKED_CAST")
        events.put(eventClass, action as Consumer<Event>)
    }

    fun <T : Event> detach(eventClass: KClass<T>, action: Action<T>) {
        events.remove(eventClass, action)
    }

    fun <T : Event> detachAll(eventClass: KClass<T>) {
        events.removeAll(eventClass)
    }

    fun detachAll() {
        events.clear()
    }

    fun call(event: Event) {
        events[event.javaClass.kotlin]
            ?.forEach { it.accept(event) }
    }
}

fun ItemStack.toInteractiveItem() = interactive(this)

fun interactive(item: ItemStack) = InteractiveItem(item)

fun <T : Cancellable> cancel(event: T) {
    event.isCancelled = true
}

typealias Action<T> = Consumer<T>


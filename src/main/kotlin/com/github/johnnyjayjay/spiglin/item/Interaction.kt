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

    private val events: Multimap<Class<out Event>, Consumer<Event>> = ArrayListMultimap.create()

    /** A boolean value determining whether interaction is enabled for this item. */
    var active: Boolean = true

    fun <T : Event> attach(eventClass: Class<T>, action: Consumer<T>?) {
        @Suppress("UNCHECKED_CAST")
        events.put(eventClass, action as Consumer<Event>)
    }

    fun <T : Event> detach(eventClass: Class<T>, action: Consumer<T>) {
        events.remove(eventClass, action)
    }

    fun <T : Event> detachAll(eventClass: Class<T>) {
        events.removeAll(eventClass)
    }

    fun call(event: Event) {
        events[event.javaClass]
            ?.forEach { it.accept(event) }
    }
}

/**
 * If this ItemStack is an instance of [InteractiveItem], returns this;
 * Otherwise creates and returns a new [InteractiveItem] based on this ItemStack.
 */
fun ItemStack.interactive() =
    if (this is InteractiveItem) this else InteractiveItem(this)

inline infix fun <reified T : Event> ItemStack.attach(action: Consumer<T>): InteractiveItem {
    val interactive = interactive()
    interactive.attach(T::class.java, action)
    return interactive
}

fun <T : Cancellable> cancel(event: T) {
    event.isCancelled = true
}

fun <T> action(action: (T) -> Unit) = Consumer(action)


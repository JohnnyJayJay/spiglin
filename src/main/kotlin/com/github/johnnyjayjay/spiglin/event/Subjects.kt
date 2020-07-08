package com.github.johnnyjayjay.spiglin.event

import com.github.johnnyjayjay.spiglin.inventory.get
import org.apache.commons.lang.Validate
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.block.BlockEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.enchantment.PrepareItemEnchantEvent
import org.bukkit.event.entity.*
import org.bukkit.event.inventory.*
import org.bukkit.event.player.*
import org.bukkit.event.vehicle.VehicleEvent
import org.bukkit.event.weather.WeatherEvent
import org.bukkit.event.world.ChunkEvent
import org.bukkit.event.world.WorldEvent
import org.bukkit.plugin.Plugin
import kotlin.reflect.KClass

/**
 * Internal class
 */
object RetrieverRegistry {

    private val entries: MutableSet<Entry<out Event>> = LinkedHashSet()

    private data class Entry<T : Event>(val type: Class<T>, val retriever: (T) -> Set<Any?>)

    init {
        add(PlayerInteractEvent::class) {
            setOf(
                it.clickedBlock,
                it.item
            )
        }
        add(PlayerDropItemEvent::class) { setOf(it.itemDrop.itemStack) }
        add(PlayerItemBreakEvent::class) { setOf(it.brokenItem) }
        add(PlayerItemConsumeEvent::class) { setOf(it.item) }
        add(PlayerItemDamageEvent::class) { setOf(it.item) }
        add(PlayerItemHeldEvent::class) {
            setOf(
                it.player.inventory[it.previousSlot],
                it.player.inventory[it.newSlot]
            )
        }
        add(EntityPickupItemEvent::class) { setOf(it.entity, it.item.itemStack) }
        add(EntityDropItemEvent::class) { setOf(it.entity, it.itemDrop.itemStack) }
        add(ItemMergeEvent::class) { setOf(it.entity.itemStack) }
        add(EnchantItemEvent::class) { setOf(it.inventory, it.item) }
        add(PrepareItemEnchantEvent::class) { setOf(it.inventory, it.item) }
        add(PrepareAnvilEvent::class) { setOf(it.inventory, it.result) }
        add(PrepareItemCraftEvent::class) { setOf(it.inventory, it.recipe?.result) }
        add(InventoryCloseEvent::class) { setOf(it.inventory, it.player) }
        add(InventoryClickEvent::class) { setOf(it.inventory, it.currentItem, it.whoClicked) }
        add(InventoryDragEvent::class) { HashSet(it.newItems.map { it.value } + it.cursor + it.oldCursor + it.inventory) }
        add(InventoryMoveItemEvent::class) { setOf(it.initiator, it.destination, it.item) }
        add(InventoryPickupItemEvent::class) { setOf(it.inventory, it.item.itemStack) }
        add(BlockDropItemEvent::class) { HashSet(it.items.map { it.itemStack } + it.player) }
        add(BrewingStandFuelEvent::class) { setOf(it.fuel, it.block) }
        add(FurnaceBurnEvent::class) { setOf(it.fuel, it.block) }
        add(FurnaceSmeltEvent::class) { setOf(it.result, it.source, it.block) }
        add(BlockPlaceEvent::class) { setOf(it.block, it.player, it.blockAgainst, it.itemInHand) }
        add(BlockBreakEvent::class) { setOf(it.block, it.player) }
        add(PlayerInteractEntityEvent::class) { setOf(it.player, it.rightClicked) }
        add(EntityDamageByEntityEvent::class) { setOf(it.damager, it.entity) }
        add(EntityDamageByBlockEvent::class) { setOf(it.entity, it.damager) }
        add(EntityDamageEvent::class) { setOf(it.entity) }
        add(BlockEvent::class) { setOf(it.block) }
        add(PlayerEvent::class) { setOf(it.player) }
        add(EntityEvent::class) { setOf(it.entity) }
        add(VehicleEvent::class) { setOf(it.vehicle) }
        add(InventoryEvent::class) { setOf(it.inventory) }
        add(WorldEvent::class) { setOf(it.world) }
        add(WeatherEvent::class) { setOf(it.world) }
        add(ChunkEvent::class) { setOf(it.chunk) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Event> add(
        eventType: KClass<T>,
        retriever: (T) -> Set<Any?>
    ) {
        entries.add(
            Entry(
                eventType.java,
                retriever
            )
        )
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Event> find(eventType: KClass<T>): ((T) -> Set<Any?>)? {
        return entries.asSequence()
            .filter { it.type.isAssignableFrom(eventType.java) }
            .firstOrNull()
            ?.let { it.retriever as (T) -> Set<Any?> }
    }

}

/**
 * Attaches an action to this object that is executed if an event of the specified type
 * involves this instance.
 *
 * @param plugin The plugin to register the listener with
 * @param priority The priority of the event listener
 * @param ignoreCancelled Whether the action should still be executed if the event was cancelled
 * @param action The action to execute on an event that involves this instance.
 *
 * @throws IllegalArgumentException if the event type cannot be used for this kind of listener.
 *
 * @return The listener used to listen for events involving this object
 */
inline fun <reified T : Event> Any.on(
    plugin: Plugin,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    crossinline action: Listener.(T) -> Unit
): ExtendedListener<T> {
    val retriever = RetrieverRegistry.find(T::class)
    Validate.isTrue(
        retriever != null,
        "Event " + T::class + " is not available"
    )
    return plugin.hear(priority, ignoreCancelled) {
        if (this@on in retriever!!(it)) {
            action(it)
        }
    }
}
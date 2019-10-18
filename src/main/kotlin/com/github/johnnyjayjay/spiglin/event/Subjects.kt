package com.github.johnnyjayjay.spiglin.event

import com.github.johnnyjayjay.spiglin.inventory.get
import org.apache.commons.lang.Validate
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.block.BlockEvent
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

object RetrieverMap {

    private val list: MutableSet<Entry<out Event>> = LinkedHashSet()

    private data class Entry<T : Event>(val type: Class<T>, val retriever: (T) -> List<Any?>)

    init {
        add(PlayerInteractEvent::class) {
            listOf(
                it.clickedBlock,
                it.item
            )
        }
        add(PlayerDropItemEvent::class) { listOf(it.itemDrop.itemStack) }
        add(PlayerItemBreakEvent::class) { listOf(it.brokenItem) }
        add(PlayerItemConsumeEvent::class) { listOf(it.item) }
        add(PlayerItemDamageEvent::class) { listOf(it.item) }
        add(PlayerItemHeldEvent::class) {
            listOf(
                it.player.inventory[it.previousSlot],
                it.player.inventory[it.newSlot]
            )
        }
        add(EntityPickupItemEvent::class) { listOf(it.item.itemStack) }
        add(EntityDropItemEvent::class) { listOf(it.itemDrop.itemStack) }
        add(ItemMergeEvent::class) { listOf(it.entity.itemStack) }
        add(EnchantItemEvent::class) { listOf(it.item) }
        add(PrepareItemEnchantEvent::class) { listOf(it.item) }
        add(PrepareAnvilEvent::class) { listOf(it.result) }
        add(PrepareItemCraftEvent::class) { listOf(it.recipe?.result) }
        add(InventoryClickEvent::class) { listOf(it.currentItem) }
        add(InventoryDragEvent::class) { listOf(it.newItems.values) }
        add(InventoryMoveItemEvent::class) { listOf(it.item) }
        add(InventoryPickupItemEvent::class) { listOf(it.item.itemStack) }
        add(BlockDropItemEvent::class) { listOf(it.items.map { it.itemStack }) }
        add(BrewingStandFuelEvent::class) { listOf(it.fuel) }
        add(FurnaceBurnEvent::class) { listOf(it.fuel) }
        add(FurnaceSmeltEvent::class) {
            listOf(
                it.result,
                it.source
            )
        }
        add(PlayerInteractEntityEvent::class) {
            listOf(
                it.player,
                it.rightClicked
            )
        }
        add(EntityDamageByEntityEvent::class) {
            listOf(
                it.damager,
                it.entity
            )
        }
        add(EntityDamageByBlockEvent::class) {
            listOf(
                it.entity,
                it.damager
            )
        }
        add(EntityDamageEvent::class) { listOf(it.entity) }
        add(BlockEvent::class) { listOf(it.block) }
        add(PlayerEvent::class) { listOf(it.player) }
        add(EntityEvent::class) { listOf(it.entity) }
        add(VehicleEvent::class) { listOf(it.vehicle) }
        add(InventoryEvent::class) { listOf(it.inventory) }
        add(WorldEvent::class) { listOf(it.world) }
        add(WeatherEvent::class) { listOf(it.world) }
        add(ChunkEvent::class) { listOf(it.chunk) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Event> add(
        eventType: KClass<T>,
        retriever: (T) -> List<Any?>
    ) {
        list.add(
            Entry(
                eventType.java,
                retriever
            )
        )
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Event> find(eventType: KClass<T>): ((T) -> List<Any?>)? {
        return list.asSequence()
            .filter { it.type.isAssignableFrom(eventType.java) }
            .firstOrNull()
            ?.let { it.retriever as (T) -> List<Any?> }
    }

}

inline fun <reified T : Event> Any.on(
    plugin: Plugin,
    priority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false,
    crossinline action: Listener.(T) -> Unit
) {
    val retriever = RetrieverMap.find(T::class)
    Validate.isTrue(retriever != null,
        "Event " + T::class + " is not available")
    plugin.hear<T>(priority, ignoreCancelled) {
        if (this@on in retriever!!(it)) {
            action(it)
        }
    }
}
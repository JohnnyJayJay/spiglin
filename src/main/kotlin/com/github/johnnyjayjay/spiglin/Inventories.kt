package com.github.johnnyjayjay.spiglin

import org.apache.commons.lang.Validate
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

const val ROW_SIZE = 9

inline fun inventoryItems(rows: Int = 3, body: Items.() -> Unit) = Items(rows).apply(body)

inline fun inventory(plugin: Plugin? = null, body: InventoryBuilder.() -> Unit) =
    InventoryBuilder().apply(body).build(plugin)

class InventoryBuilder {

    var title: String = InventoryType.CHEST.defaultTitle
    var holder: InventoryHolder? = null
    var rows: Int = DEFAULT_ROWS
        set(value) {
            items = Items(rows)
            field = value
        }

    private var items: Items = Items(rows % ROW_SIZE)

    fun items(body: Items.() -> Unit) {
        items.body()
    }

    fun build(plugin: Plugin? = null): Inventory {
        val inventory = Bukkit.createInventory(holder, rows, title)
        inventory.setItems(items)
        if (plugin != null) {
            ClickListener.inventories[inventory] = items.clickables
        }
        return inventory
    }

    companion object {
        const val DEFAULT_ROWS = 3
    }
}

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

class Items(rows: Int) {

    val grid: Array<Array<ItemStack?>> = Array(rows) { arrayOfNulls(ROW_SIZE) }

    internal val clickables: Set<ClickableItem>
        get() {
            return grid
                .asSequence()
                .reduce { one, two ->
                    val array = arrayOfNulls<ItemStack?>(one.size + two.size)
                    for (i in one.indices)
                        array[i] = one[i] as? ClickableItem
                    for (i in two.indices)
                        array[i] = two[i] as? ClickableItem
                    array
                }
                .filterNotNull()
                .map { it as ClickableItem }
                .toSet()
        }

    infix fun ItemStack.withAction(action: (InventoryClickEvent) -> Unit): ItemStack {
        return ClickableItem(this, action)
    }

    fun fillWith(item: ItemStack, vararg except: Slot = emptyArray()) {
        grid.forEachIndexed { x, row ->
            row.forEachIndexed { y, _ ->
                if (Slot(x, y) !in except) {
                    grid[x][y] = item
                }
            }
        }
    }

    fun toContents(): Array<ItemStack?> {
        val contents = Array<ItemStack?>(grid.size * ROW_SIZE) {}
        for (row in grid) {
            for (item in row) {
                val index = grid.indexOf(row) * ROW_SIZE + row.indexOf(item)
                contents[index] = item
            }
        }
        return contents
    }

}

fun Inventory.setItems(items: Items) {
    contents = items.toContents()
}

operator fun Inventory.get(slot: Slot): ItemStack? = contents[slot.oneDimensional]

operator fun Inventory.set(slot: Slot, stack: ItemStack?) {
    contents[slot.oneDimensional] = stack
}

data class Slot(val row: Int, val column: Int) {
    val oneDimensional = row * ROW_SIZE + column

    init {
        Validate.isTrue(column in 0 until ROW_SIZE, "Column out of bounds")
        Validate.isTrue(row >= 0, "Row out of bounds")
    }

    companion object {
        val FIRST = Slot(0, 0)
    }
}

internal data class ClickableItem(val stack: ItemStack, val action: (InventoryClickEvent) -> Unit) : ItemStack(stack)

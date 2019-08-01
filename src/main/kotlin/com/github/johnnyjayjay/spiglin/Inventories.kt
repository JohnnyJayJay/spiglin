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

inline fun inventory(plugin: Plugin? = null, body: InventoryBuilder.() -> Unit) =
    InventoryBuilder().apply(body).build(plugin)

class InventoryBuilder {

    var title: String = InventoryType.CHEST.defaultTitle
    var holder: InventoryHolder? = null
    var size: Int = DEFAULT_ROWS * ROW_SIZE
        set(value) {
            Validate.isTrue(value % ROW_SIZE == 0, "Size must be a multiple of InventoryBuilder.ROW_SIZE")
            field = value
            items = Items(value % ROW_SIZE)
        }

    private var items: Items = Items(size % ROW_SIZE)

    fun items(body: Items.() -> Unit) {
        items.body()
    }

    fun build(plugin: Plugin? = null): Inventory {
        val inventory = Bukkit.createInventory(holder, size, title)
        inventory.contents = items.toContents()
        if (plugin != null) {
            ClickListener.inventories[inventory] = items.toMap()
        }
        return inventory
    }

    companion object {
        const val DEFAULT_ROWS = 3
    }
}

object ClickListener : Listener {

    internal val inventories: MutableMap<Inventory, Map<ItemStack?, (InventoryClickEvent) -> Unit>> = mutableMapOf()

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        inventories[event.inventory]?.get(event.currentItem)?.invoke(event)
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        if (event.inventory in inventories) {
            inventories.remove(event.inventory)
        }
    }

}

class Items internal constructor(rows: Int) {

    val grid: Array<Array<Item?>> = Array(rows) { arrayOfNulls(ROW_SIZE) }

    operator fun Array<Item>.set(index: Int, itemStack: ItemStack) {
        this[index] = wrap(itemStack)
    }

    infix fun ItemStack.onClick(action: (InventoryClickEvent) -> Unit): Item {
        return Item(this, action)
    }

    fun wrap(stack: ItemStack? = null, action: ((InventoryClickEvent) -> Unit)? = null) = Item(stack, action)

    fun fillWith(item: Item, vararg except: Pair<Int, Int> = emptyArray()) {
        grid.forEachIndexed { x, row ->
            row.forEachIndexed { y, item ->
                if (x to y !in except) {
                    grid[x][y] = item
                }
            }
        }
    }

    fun toMap(): Map<ItemStack?, (InventoryClickEvent) -> Unit> {
        return grid
            .asSequence()
            .reduce { one, two ->
                val array = arrayOfNulls<Items.Item?>(one.size + two.size)
                for (i in one.indices)
                    array[i] = one[i]
                for (i in two.indices)
                    array[i] = two[i]
                array
            }
            .filterNotNull()
            .filter { it.action != null }
            .fold(mutableMapOf()) { map, item ->
                map[item.stack] = item.action!!
                map
            }
    }

    fun toContents(): Array<ItemStack?> {
        val contents = Array<ItemStack?>(grid.size * ROW_SIZE) {}
        for (row in grid) {
            for (item in row) {
                val index = grid.indexOf(row) * ROW_SIZE + row.indexOf(item)
                contents[index] = item?.stack
            }
        }
        return contents
    }

    data class Item(val stack: ItemStack?, val action: ((InventoryClickEvent) -> Unit)?)
}
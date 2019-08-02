package com.github.johnnyjayjay.spiglin.inventory

import com.github.johnnyjayjay.spiglin.NEW_LINE_SPLIT
import org.apache.commons.lang.Validate
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import java.lang.ref.WeakReference

inline fun inventoryItems(rows: Int = 3, body: Items.() -> Unit) =
    Items(rows).apply(body)

inline fun inventory(plugin: Plugin? = null, body: InventoryBuilder.() -> Unit) =
    InventoryBuilder().apply(body).build(plugin)

class InventoryBuilder {

    var title: String = InventoryType.CHEST.defaultTitle
    var holder: InventoryHolder? = null
    var rows: Int = DEFAULT_ROWS
    var items: Items = Items(0)

    inline fun items(body: Items.() -> Unit) {
        items = inventoryItems(rows, body)
    }

    fun build(plugin: Plugin? = null): Inventory {
        val inventory = Bukkit.createInventory(holder, rows, title)
        inventory.items = items
        if (plugin != null) {
            ItemInteractionListener.inventories[inventory] = items.clickables
        }
        return inventory
    }

    companion object {
        const val DEFAULT_ROWS = 3
    }
}


class Items(rows: Int) {

    val grid: Array<Array<ItemStack?>> = Array(rows) { arrayOfNulls(ROW_SIZE) }
    val linearIndices: IntRange = 0 until linearInventoryIndex(rows, 8)

    internal val clickables: Set<ClickableItem>
        get() {
            return grid
                .reduce(Array<ItemStack?>::plus)
                .filterIsInstance<ClickableItem>()
                .toSet()
        }

    fun fill(target: IntRange = linearIndices) = Filler(target)

    inner class Filler internal constructor(val target: IntRange) {

        private val gridCopy = WeakReference(grid.map(Array<ItemStack?>::copyOf).toList().toTypedArray())

        infix fun with(item: ItemStack?): Filler {
            for (i in target) {
                val (row, column) = twoDimensionalInventoryIndex(i)
                grid[row, column] = item
            }
            return this
        }

        infix fun except(range: IntRange) {
            for (i in range) {
                val (row, column) = twoDimensionalInventoryIndex(i)
                grid[row, column] = gridCopy.get()!![row, column]
            }
        }

        infix fun except(positions: Iterable<Pair<Int, Int>>) {
            for ((row, column) in positions) {
                grid[row, column] = gridCopy.get()!![row, column]
            }
        }

        infix fun except(position: Pair<Int, Int>) {
            val (row, column) = position
            if (linearInventoryIndex(row, column) in target) {
                grid[row, column] = gridCopy.get()!![row, column]
            }
        }
    }

    inline fun forEachSlot(action: (Int, Int) -> Unit) {
        for (row in grid.indices) {
            for (column in grid[row].indices) {
                action(row, column)
            }
        }
    }

    fun toContents(): Array<ItemStack?> {
        val contents = Array<ItemStack?>(grid.size * ROW_SIZE) {}
        for (row in grid) {
            for (item in row) {
                val index =
                    linearInventoryIndex(grid.indexOf(row), row.indexOf(item))
                contents[index] = item
            }
        }
        return contents
    }

    companion object {

        fun from(formatString: String, bindings: Map<Char, ItemStack?>): Items {
            val rows = formatString.split(NEW_LINE_SPLIT)
            return from(rows.asSequence()
                .map { it.toCharArray() }
                .map { it.map(bindings::get) }
                .map { it.toTypedArray() }
                .toList().toTypedArray())
        }

        fun from(grid: Array<Array<ItemStack?>>): Items {
            val items = Items(grid.size)
            grid.forEachIndexed { row, _ ->
                items.grid[row] = grid[row].copyOf(grid.size)
            }
            return items
        }

        fun from(contents: Array<ItemStack?>): Items {
            val rowRemainder = contents.size % ROW_SIZE
            Validate.isTrue(rowRemainder == 0, "Array size must be a multiple of ROW_SIZE")
            val grid: Array<Array<ItemStack?>> = Array(contents.size / ROW_SIZE) { index ->
                contents.sliceArray(index until index + 9)
            }
            return from(grid)
        }
    }
}

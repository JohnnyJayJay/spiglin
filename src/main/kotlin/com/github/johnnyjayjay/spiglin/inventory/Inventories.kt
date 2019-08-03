package com.github.johnnyjayjay.spiglin.inventory

import com.github.johnnyjayjay.spiglin.item.NEW_LINE_SPLIT
import org.apache.commons.lang.Validate
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import java.lang.IndexOutOfBoundsException
import kotlin.contracts.contract

typealias Slot = Pair<Int, Int>

const val ROW_SIZE = 9

inline fun inventory(
    rows: Int = 3,
    owner: InventoryHolder? = null,
    title: String = InventoryType.CHEST.defaultTitle,
    body: Inventory.() -> Unit
) = Bukkit.createInventory(owner, rows, title).apply(body)

inline fun Inventory.forEachSlot(action: (Int, Int) -> Unit) {
    forEachSlotLinear {
        val (row, column) = it.toSlot()
        action(row, column)
    }
}

inline fun Inventory.forEachSlotLinear(action: (Int) -> Unit) {
    for (slot in this.contents.indices) {
        action(slot)
    }
}

fun items(formatString: String, bindings: Map<Char, ItemStack?>): Array<ItemStack?> {
    val rows = formatString.split(NEW_LINE_SPLIT)
    return rows.asSequence()
        .map { it.toCharArray() }
        .map { it.map(bindings::get) }
        .reduce { one, two -> one + two}
        .toTypedArray()
}

operator fun Inventory.get(index: Int): ItemStack? = contents[index]

operator fun Inventory.set(index: Int, item: ItemStack?) {
    contents[index] = item
}

operator fun Inventory.get(indices: Iterable<Int>) = indices.map { this[it] }

operator fun Inventory.set(indices: Iterable<Int>, item: ItemStack?) {
    for (i in indices) {
        this[i] = item
    }
}

operator fun Inventory.set(indices: Iterable<Int>, items: Iterable<ItemStack?>) {
    val indexIterator = indices.iterator()
    val itemIterator = items.iterator()
    while (indexIterator.hasNext() && itemIterator.hasNext()) {
        this[indexIterator.next()] = itemIterator.next()
    }
}

fun Inventory.openTo(player: Player) = player.openInventory(this)

fun Inventory.row(index: Int): IntRange {
    checkBounds(index, 0 until 9, "Row")
    val start = slot(index, 0)
    return start until start + ROW_SIZE
}

fun Inventory.column(index: Int): IntProgression {
    checkBounds(index, 0 until rows, "Column")
    val start = slot(0, index)
    return start until (rows - 1 + index) step 9
}

var Inventory.interactive: Boolean
    get() = ItemInteractionListener.contains(this)
    set(value) {
        if (value) {
            ItemInteractionListener.add(this)
        } else {
            ItemInteractionListener.remove(this)
        }
    }

val Inventory.all: IntRange
    get() = contents.indices

val Inventory.rows: Int
    get() = size / ROW_SIZE

private fun checkBounds(index: Int, bounds: IntRange, name: String) {
    if (index !in bounds) {
        throw IndexOutOfBoundsException("$name index out of bounds")
    }
}
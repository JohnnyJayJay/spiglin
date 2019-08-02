package com.github.johnnyjayjay.spiglin.inventory

import com.github.johnnyjayjay.spiglin.item.NEW_LINE_SPLIT
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

const val ROW_SIZE = 9

inline fun inventory(
    rows: Int = 3,
    owner: InventoryHolder? = null,
    title: String = InventoryType.CHEST.defaultTitle,
    body: Inventory.() -> Unit
) = Bukkit.createInventory(owner, rows, title).apply(body)

infix fun Filler<ItemStack?>.except(positions: Iterable<Pair<Int, Int>>) {
    for (position in positions) {
        val linear = linearIndex(position)
        if (linear in target) {
            array[linear] = contentBackup.get()!![linear]
        }
    }
}

infix fun Filler<ItemStack?>.except(position: Pair<Int, Int>) {
    val linear = linearIndex(position)
    if (linear in target) {
        array[linear] = contentBackup.get()!![linear]
    }
}

fun Inventory.fill(target: IntRange = contents.indices): Filler<ItemStack?> = contents.fill(target)

inline fun Inventory.forEachSlot(action: (Int, Int) -> Unit) {
    forEachSlotLinear {
        val (row, column) = twoDimensionalIndex(it)
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

operator fun Inventory.get(row: Int, column: Int): ItemStack? =
    contents[linearIndex(row, column)]

operator fun Inventory.set(row: Int, column: Int, stack: ItemStack?) {
    contents[linearIndex(row, column)] = stack
}

operator fun Inventory.get(index: Int): ItemStack? = contents[index]

operator fun Inventory.set(index: Int, item: ItemStack?) {
    contents[index] = item
}

operator fun Inventory.get(position: Pair<Int, Int>) = this[position.first, position.second]

operator fun Inventory.set(position: Pair<Int, Int>, item: ItemStack?) {
    this[position.first, position.second] = item
}

operator fun Inventory.get(range: IntRange): Array<ItemStack?> = contents.copyOfRange(range.first, range.last)

fun Inventory.openTo(player: Player) = player.openInventory(this)

package com.github.johnnyjayjay.spiglin.inventory

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

const val ROW_SIZE = 9

var Inventory.items: Items
    get() = Items.from(contents)
    set(value) { contents = value.toContents() }

operator fun Inventory.get(row: Int, column: Int): ItemStack? = contents[linearInventoryIndex(
    row,
    column
)]

operator fun Inventory.set(row: Int, column: Int, stack: ItemStack?) {
    contents[linearInventoryIndex(row, column)] = stack
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

fun row(index: Int): IntRange {
    val start = linearInventoryIndex(index, 0)
    return start..start + ROW_SIZE
}

fun Inventory.openTo(player: Player) {
    player.openInventory(this)
}

operator fun <T> Array<Array<T>>.get(outer: Int, inner: Int) = this[outer][inner]

operator fun <T> Array<Array<T>>.set(outer: Int, inner: Int, value: T) {
    this[outer][inner] = value
}

fun linearInventoryIndex(position: Pair<Int, Int>) =
    linearInventoryIndex(position.first, position.second)

fun linearInventoryIndex(row: Int, column: Int) =
    row * ROW_SIZE + column

fun twoDimensionalInventoryIndex(linearIndex: Int) =
    linearIndex / ROW_SIZE to linearIndex % ROW_SIZE
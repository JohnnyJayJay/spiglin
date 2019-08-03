package com.github.johnnyjayjay.spiglin.inventory

import com.github.johnnyjayjay.spiglin.item.NEW_LINE_SPLIT
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

const val ROW_SIZE = 9

/**
 * Creates an inventory of type [InventoryType.CHEST], applies the given body to it and returns it.
 *
 * @param rows Sets how many rows this inventory should have. Default: 3
 * @param owner Sets an optional owner of the inventory, such as a chest. Default: null
 * @param title Sets the title for this inventory. Default: [InventoryType.CHEST.defaultTitle][InventoryType.CHEST]
 * @param body A function with the new inventory as its receiver, used to configure the inventory and its contents.
 * @return An inventory created using [Bukkit.createInventory].
 */
inline fun inventory(
    rows: Int = 3,
    owner: InventoryHolder? = null,
    title: String = InventoryType.CHEST.defaultTitle,
    body: Inventory.() -> Unit
) = Bukkit.createInventory(owner, rows, title).apply(body)

/**
 * Iterates over the slots in this inventory. Other than [Inventory.forEach] or [Inventory.forEachIndexed]
 * this can be used to modify the inventory's contents.
 *
 * @param action A function that is called with row and column of the current slot for each iteration.
 */
inline fun Inventory.forEachSlot(action: (Int, Int) -> Unit) {
    forEachSlotLinear {
        val (row, column) = it.toSlot()
        action(row, column)
    }
}

/**
 * Iterates over the slots in this inventory. Other than [Inventory.forEach] or [Inventory.forEachIndexed]
 * this can be used to modify the inventory's contents.
 *
 * @param action A function that is called with the linear index of the current slot for each iteration.
 * @see slot
 */
inline fun Inventory.forEachSlotLinear(action: (Int) -> Unit) {
    for (slot in all) {
        action(slot)
    }
}

// TODO
fun items(formatString: String, bindings: Map<Char, ItemStack?>): Array<ItemStack?> {
    val rows = formatString.split(NEW_LINE_SPLIT)
    return rows.asSequence()
        .map { it.toCharArray() }
        .map { it.map(bindings::get) }
        .reduce { one, two -> one + two}
        .toTypedArray()
}

/**
 * Returns the content of this inventory at the given linear position.
 *
 * @throws IndexOutOfBoundsException If this inventory doesn't have the given slot.
 * @see slot
 */
operator fun Inventory.get(index: Int): ItemStack? = contents[index]

/**
 * Sets the content of this inventory at the given linear position.
 *
 * @throws IndexOutOfBoundsException If this inventory doesn't have the given slot.
 * @see slot
 */
operator fun Inventory.set(index: Int, item: ItemStack?) {
    contents[index] = item
}

/**
 * Returns a List of ItemStacks corresponding to the ItemStacks at the given indices.
 *
 * @param indices An iterable of linear inventory indices to retrieve the ItemStacks from.
 * @throws IndexOutOfBoundsException If this inventory doesn't have one or more of the given slots.
 * @see slots
 * @see slot
 * @see row
 * @see column
 * @see all
 */
operator fun Inventory.get(indices: Iterable<Int>) = indices.map { this[it] }

/**
 * Sets this inventory's content at all given indices to the given ItemStack.
 *
 * @param indices An iterable of linear inventory indices of the slots to set.
 * @throws IndexOutOfBoundsException If this inventory doesn't have one or more of the given slots.
 * @see slots
 * @see slot
 * @see row
 * @see column
 * @see all
 */
operator fun Inventory.set(indices: Iterable<Int>, item: ItemStack?) {
    for (i in indices) {
        this[i] = item
    }
}

/**
 * Sets this inventory's content at each given index to the equivalent in the items argument.
 * The size of indices and items need not be the same.
 * This function just sets items as long as there are both indices and items left.
 *
 * @param indices An [Iterable] of linear inventory indices of the slots to set.
 * @param items An [Iterable] of ItemStacks to set for the given indices.
 * @throws IndexOutOfBoundsException If a slot from indices that is accessed during the process does not exist.
 * @see slots
 * @see slot
 * @see row
 * @see column
 * @see all
 */
operator fun Inventory.set(indices: Iterable<Int>, items: Iterable<ItemStack?>) {
    val indexIterator = indices.iterator()
    val itemIterator = items.iterator()
    while (indexIterator.hasNext() && itemIterator.hasNext()) {
        this[indexIterator.next()] = itemIterator.next()
    }
}

/**
 * @see Inventory.addItem
 */
operator fun Inventory.plusAssign(item: ItemStack) {
    this.addItem(item)
}

/**
 * @see Inventory.addItem
 */
operator fun Inventory.plusAssign(items: Iterable<ItemStack>) {
    this.addItem(*items.toList().toTypedArray())
}

/**
 * @see Inventory.remove
 */
operator fun Inventory.minusAssign(item: ItemStack) {
    this.remove(item)
}

/**
 * @see Inventory.removeItem
 */
operator fun Inventory.minusAssign(items: Iterable<ItemStack>) {
    this.removeItem(*items.toList().toTypedArray())
}

/**
 * @see Inventory.remove
 */
operator fun Inventory.minusAssign(material: Material) {
    this.remove(material)
}

/**
 * @see Player.openInventory
 */
fun Inventory.openTo(player: Player) = player.openInventory(this)

/**
 * Returns an [IntRange] containing all the linear slots in the given row.
 *
 * @param index The index of the row, starting from 0.
 * @throws IndexOutOfBoundsException If this inventory doesn't have that row (index !in 0 until rows)
 * @see set
 * @see get
 */
fun Inventory.row(index: Int): IntRange {
    checkBounds(index, 0 until rows, "Row")
    val start = slot(index, 0)
    return start until start + ROW_SIZE
}

/**
 * Returns an [IntProgression] containing all the linear slots in the given column.
 *
 * @param index The index of the column, between 0 (inclusive) and 9 (exclusive).
 * @throws IndexOutOfBoundsException If the column is not between 0 and 9.
 * @see set
 * @see get
 */
fun Inventory.column(index: Int): IntProgression {
    checkBounds(index, 0 until 9, "Column")
    val start = slot(0, index)
    return start until (rows - 1 + index) step 9
}

/**
 * A variable determining if [InteractiveInventoryListener] should hook on this inventory
 * and listen for clicked items.
 * If this is set to true, clicks on [ClickableInventoryItem]s in this inventory will trigger their action.
 *
 * Note that [InteractiveInventoryListener] needs to be registered as Listener for this to work!
 *
 * @see InteractiveInventoryListener
 */
var Inventory.interactive: Boolean
    get() = InteractiveInventoryListener.contains(this)
    set(value) {
        if (value) {
            InteractiveInventoryListener.add(this)
        } else {
            InteractiveInventoryListener.remove(this)
        }
    }

/**
 * All linear slot indices in this inventory.
 */
val Inventory.all: IntRange
    get() = contents.indices

/**
 * The amount of rows in this inventory.
 */
val Inventory.rows: Int
    get() = size / ROW_SIZE

private fun checkBounds(index: Int, bounds: IntRange, name: String) {
    if (index !in bounds) {
        throw IndexOutOfBoundsException("$name index out of bounds")
    }
}


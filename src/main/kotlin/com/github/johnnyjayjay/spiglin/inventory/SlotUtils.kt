package com.github.johnnyjayjay.spiglin.inventory

import org.apache.commons.lang.Validate

/**
 * A typealias for [Pair<Int, Int>][Pair] used to represent a slot position in an
 * [Inventory][org.bukkit.inventory.Inventory] of type
 * [InventoryType.CHEST][org.bukkit.event.inventory.InventoryType.CHEST],
 * where [Pair.first] denotes the row and [Pair.second] denotes the column of this slot.
 *
 * @see slot
 */
typealias Slot = Pair<Int, Int>

/**
 * Converts a [Slot] to a linear inventory index.
 *
 * @param slot The [Slot] to convert.
 * @return The calculated linear index.
 * @throws IllegalArgumentException If the row is negative or the column is not in range 0..8
 */
fun slot(slot: Slot) =
    slot(slot.first, slot.second)

/**
 * Converts a two-dimensional combination of row - column to a linear inventory index.
 *
 * @param row The row of the slot.
 * @param column The column of the slot
 * @return The calculated linear index
 * @throws IllegalArgumentException If the row is negative or the column is not in range 0..8
 */
fun slot(row: Int, column: Int): Int {
    Validate.isTrue(row >= 0, "Row must not be negative")
    Validate.isTrue(column in 0 until 9, "Column must be >= 0 and < 9")
    return row * ROW_SIZE + column
}

/**
 * Converts a variable amount of two-dimensional [Slot]s to an [Iterable]
 * of their linear equivalents.
 *
 * @param slots One or more [Slot]s to convert.
 * @return An [Iterable<Int>][Iterable] of linear indices preserving the order of the argument.
 * @throws IllegalArgumentException
 *          If one or more of the rows is negative or one or more of the columns is not in range 0..8
 * @see slot
 */
fun slots(vararg slots: Slot): Iterable<Int> =
    slots.map(::slot)

/**
 * Converts a linear inventory index to a [Slot] object.
 *
 * @return A two-dimensional [Slot] containing the row and column this Int denotes.
 */
fun Int.toSlot(): Slot =
    this / ROW_SIZE to this % ROW_SIZE

/**
 * An alternative infix function for [Iterable.minus].
 */
infix fun <T> Iterable<T>.except(element: T) =
    this - element

/**
 * An alternative infix function for [Iterable.minus].
 */
infix fun <T> Iterable<T>.except(elements: Iterable<T>) =
    this - elements


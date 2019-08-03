package com.github.johnnyjayjay.spiglin.inventory

fun slot(slot: Slot) =
    slot(slot.first, slot.second)

fun slot(row: Int, column: Int) =
    row * ROW_SIZE + column

fun slots(vararg slots: Slot): Iterable<Int> = slots.map(::slot)

fun Int.toSlot(): Slot =
    this / ROW_SIZE to this % ROW_SIZE


infix fun <T> Iterable<T>.except(element: T) = this - element

infix fun <T> Iterable<T>.except(elements: Iterable<T>) = this - elements


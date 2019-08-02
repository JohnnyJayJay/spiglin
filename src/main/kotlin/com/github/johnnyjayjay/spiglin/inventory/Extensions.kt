package com.github.johnnyjayjay.spiglin.inventory

fun row(index: Int): IntRange {
    val start = linearInventoryIndex(index, 0)
    return start..start + ROW_SIZE
}

fun linearInventoryIndex(position: Pair<Int, Int>) =
    linearInventoryIndex(position.first, position.second)

fun linearInventoryIndex(row: Int, column: Int) =
    row * ROW_SIZE + column

fun twoDimensionalInventoryIndex(linearIndex: Int) =
    linearIndex / ROW_SIZE to linearIndex % ROW_SIZE
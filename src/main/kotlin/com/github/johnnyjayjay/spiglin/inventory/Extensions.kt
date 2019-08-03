package com.github.johnnyjayjay.spiglin.inventory

fun linearIndex(position: Pair<Int, Int>) =
    linearIndex(position.first, position.second)

fun linearIndex(row: Int, column: Int) =
    row * ROW_SIZE + column

fun twoDimensionalIndex(linearIndex: Int) =
    linearIndex / ROW_SIZE to linearIndex % ROW_SIZE

val Pair<Int, Int>.linear
    get() = linearIndex(first, second)

val Int.twoDimensional
    get() = twoDimensionalIndex(this)
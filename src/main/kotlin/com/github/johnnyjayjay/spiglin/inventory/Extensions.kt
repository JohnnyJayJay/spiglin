package com.github.johnnyjayjay.spiglin.inventory

import java.lang.ref.WeakReference

fun row(index: Int): IntRange {
    val start = linearIndex(index, 0)
    return start..start + ROW_SIZE
}

fun linearIndex(position: Pair<Int, Int>) =
    linearIndex(position.first, position.second)

fun linearIndex(row: Int, column: Int) =
    row * ROW_SIZE + column

fun twoDimensionalIndex(linearIndex: Int) =
    linearIndex / ROW_SIZE to linearIndex % ROW_SIZE

class Filler<T> internal constructor(internal val array: Array<T>, val target: IntRange) {

    internal val contentBackup = WeakReference(array.copyOf())

    infix fun with(value: T): Filler<T> {
        for (i in target) {
            array[i] = value
        }
        return this
    }

    infix fun except(range: IntRange) {
        for (i in range) {
            array[i] = contentBackup.get()!![i]
        }
    }
}

fun <T> Array<T>.fill(target: IntRange = indices) = Filler(this, target)
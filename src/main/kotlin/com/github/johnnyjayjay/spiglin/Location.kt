package com.github.johnnyjayjay.spiglin

import org.bukkit.Location
import org.bukkit.util.Vector

operator fun Location.plus(vector: Vector) =
    clone().add(vector)

operator fun Location.plusAssign(vector: Vector) {
    add(vector)
}

operator fun Location.minus(vector: Vector) =
    clone().subtract(vector)

operator fun Location.minusAssign(vector: Vector) {
    subtract(vector)
}

operator fun Location.times(number: Number) =
    clone().multiply(number.toDouble())

operator fun Location.timesAssign(number: Number) {
    multiply(number.toDouble())
}

operator fun Location.div(number: Number) =
    clone().multiply(1 / number.toDouble())

operator fun Location.divAssign(number: Number) {
    multiply(1 / number.toDouble())
}


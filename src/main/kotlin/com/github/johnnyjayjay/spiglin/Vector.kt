package com.github.johnnyjayjay.spiglin

import org.bukkit.util.Vector
import kotlin.math.pow
import kotlin.math.sqrt

val Vector.abs
    get() = sqrt(x.pow(2) + y.pow(2) + z.pow(2))

operator fun Vector.unaryMinus() =
    clone().apply { x = -x; y = -y; z = -z; }

operator fun Vector.unaryPlus() =
    clone().apply { x = +x; y = +y; z = +z; }

operator fun Vector.compareTo(other: Vector) =
    this.abs.compareTo(other.abs)

operator fun Vector.times(scalar: Number) =
    clone().multiply(scalar.toDouble())

operator fun Vector.timesAssign(scalar: Number){
    multiply(scalar.toDouble())
}

operator fun Vector.plus(vector: Vector) =
    clone().add(vector)

operator fun Vector.plusAssign(vector: Vector) {
    add(vector)
}

operator fun Vector.minus(vector: Vector) =
    clone().subtract(vector)

operator fun Vector.minusAssign(vector: Vector) {
    subtract(vector)
}

operator fun Vector.times(vector: Vector) =
    clone().dot(vector)

operator fun Vector.timesAssign(vector: Vector) {
    dot(vector)
}

operator fun Vector.div(vector: Vector) =
    clone().divide(vector)

operator fun Vector.divAssign(vector: Vector) {
    divide(vector)
}

infix fun Vector.x(vector: Vector) =
    clone().crossProduct(vector)




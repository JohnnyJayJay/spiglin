package com.github.johnnyjayjay.spiglin

import org.bukkit.util.Vector
import kotlin.math.pow
import kotlin.math.sqrt

/** The Euclidian Norm of this vector. */
val Vector.abs
    get() = sqrt(x.pow(2) + y.pow(2) + z.pow(2))

/** Returns a vector with x, y, z from this vector negated. */
operator fun Vector.unaryMinus() =
    clone().apply { x = -x; y = -y; z = -z; }

/** Returns an unchanged copy of this vector. */
operator fun Vector.unaryPlus() =
    clone()

/** Compares two vectors based on their Euclidian Norm (absolute value) */
operator fun Vector.compareTo(other: Vector) =
    this.abs.compareTo(other.abs)

/** Returns a vector that is the result of this vector multiplied with the given scalar. */
operator fun Vector.times(scalar: Number) =
    clone().multiply(scalar.toDouble())

/** Multiplies this vector with the given scalar. */
operator fun Vector.timesAssign(scalar: Number){
    multiply(scalar.toDouble())
}

/** Returns a vector that is the result of this vector plus the given vector. */
operator fun Vector.plus(vector: Vector) =
    clone().add(vector)

/** Adds the given vector to this vector */
operator fun Vector.plusAssign(vector: Vector) {
    add(vector)
}

/** Returns a vector that is the result of this vector minus the given vector. */
operator fun Vector.minus(vector: Vector) =
    clone().subtract(vector)

/** Subtracts the given vector from this vector */
operator fun Vector.minusAssign(vector: Vector) {
    subtract(vector)
}

/** Returns the dot product of this and another vector. */
operator fun Vector.times(vector: Vector) =
    clone().dot(vector)

/** Returns the cross product of this and another vector. */
infix fun Vector.x(vector: Vector) =
    clone().crossProduct(vector)
package com.github.johnnyjayjay.spiglin

import org.bukkit.Location
import org.bukkit.util.Vector

/** Returns a Location that is the result of this location plus the given vector */
operator fun Location.plus(vector: Vector) =
    clone().add(vector)

/** Adds the given vector to this location */
operator fun Location.plusAssign(vector: Vector) {
    add(vector)
}

/** Returns a Location that is the result of this location minus the given vector */
operator fun Location.minus(vector: Vector) =
    clone().subtract(vector)

/** Subtracts the given vector from this location */
operator fun Location.minusAssign(vector: Vector) {
    subtract(vector)
}


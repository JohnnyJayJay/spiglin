package com.github.johnnyjayjay.spiglin

import org.bukkit.*
import org.bukkit.entity.Damageable
import org.bukkit.entity.Player

/** Sets this Damageable's health to 0. */
fun Damageable.kill() {
    health = 0.0
}

/** Sets this Damageable's health to maxHealth. */
@Suppress("DEPRECATION")
fun Damageable.heal() {
    health = maxHealth
}

/** Sets this player's food level to 20. */
fun Player.feed() {
    foodLevel = 20
}

/** Hides this player from the given players.*/
@Suppress("DEPRECATION")
fun Player.hideFrom(vararg players: Player) =
    players.forEach { it.hidePlayer(this) }

/** Hides this player from the given players. */
@Suppress("DEPRECATION")
fun Player.hideFrom(players: Iterable<Player>) =
    players.forEach { it.hidePlayer(this) }

/** Hides this player from all [onlinePlayers] that match the given predicate. */
inline fun Player.hideIf(crossinline predicate: (Player) -> Boolean) {
    onlinePlayers.asSequence()
        .filter { predicate(it) }
        .forEach { this.hideFrom(it) }
}

/** Hides this player from all [onlinePlayers]. */
fun Player.hideFromAll() =
    hideFrom(onlinePlayers)

/**
 * @see Player.playEffect
 */
fun <T> Player.play(location: Location = this.location, effect: Effect, data: T? = null) =
    playEffect(location, effect, data)

/**
 * @see Player.playSound
 */
fun Player.play(
    location: Location = this.location,
    sound: Sound,
    category: SoundCategory,
    volume: Float = 1F,
    pitch: Float = 1F
) = playSound(location, sound, category, volume, pitch)

/**
 * @see Player.playNote
 */
fun Player.play(location: Location = this.location, instrument: Instrument, note: Note) =
    playNote(location,  instrument, note)





package com.github.johnnyjayjay.spiglin

import org.bukkit.*
import org.bukkit.entity.Damageable
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

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

/** Shows this player to the given players */
@Suppress("DEPRECATION")
fun Player.showTo(vararg players: Player, plugin: Plugin? = null) =
    players.forEach { if (plugin != null) it.showPlayer(plugin, this) else it.showPlayer(this) }

/** Shows this player to the given players */
@Suppress("DEPRECATION")
fun Player.showTo(players: Iterable<Player>, plugin: Plugin? = null) =
    players.forEach { if (plugin != null) it.showPlayer(plugin, this) else it.showPlayer(this) }

/** Shows this player to all [onlinePlayers] that match the given predicate. */
@Suppress("DEPRECATION")
fun Player.showIf(predicate: (Player) -> Boolean, plugin: Plugin? = null) {
    onlinePlayers.asSequence()
        .filter(predicate)
        .forEach { if (plugin != null) it.showPlayer(plugin, this) else it.showPlayer(this) }
}

/** Shows this player to all [onlinePlayers]. */
fun Player.showToAll() =
    showTo(onlinePlayers)

/** Hides this player from the given players.*/
@Suppress("DEPRECATION")
fun Player.hideFrom(vararg players: Player, plugin: Plugin? = null) =
    players.forEach { if (plugin != null) it.hidePlayer(plugin, this) else it.hidePlayer(this) }

/** Hides this player from the given players. */
@Suppress("DEPRECATION")
fun Player.hideFrom(players: Iterable<Player>, plugin: Plugin? = null) =
    players.forEach { if (plugin != null) it.hidePlayer(plugin, this) else it.hidePlayer(this) }

/** Hides this player from all [onlinePlayers] that match the given predicate. */
@Suppress("DEPRECATION")
fun Player.hideIf(predicate: (Player) -> Boolean, plugin: Plugin? = null) {
    onlinePlayers.asSequence()
        .filter(predicate)
        .forEach { if (plugin != null) it.hidePlayer(plugin, this) else it.hidePlayer(this) }
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
    playNote(location, instrument, note)





package com.github.johnnyjayjay.spiglin

import org.bukkit.*
import org.bukkit.entity.Damageable
import org.bukkit.entity.Player

fun Damageable.kill() {
    health = 0.0
}

fun Damageable.heal() {
    health = maxHealth
}

fun Player.feed() {
    foodLevel = 20
}

fun Player.hideFrom(vararg players: Player) =
    players.forEach { it.hidePlayer(this) }

fun Player.hideFrom(players: Iterable<Player>) =
    players.forEach { it.hidePlayer(this) }

inline fun Player.hideIf(crossinline predicate: (Player) -> Boolean) {
    onlinePlayers.asSequence()
        .filter { predicate(it) }
        .forEach(Player::hideFrom)
}

fun Player.hideFromAll() =
    hideFrom(onlinePlayers)

fun <T> Player.play(location: Location = this.location, effect: Effect, data: T? = null) =
    playEffect(location, effect, data)

fun Player.play(
    location: Location = this.location,
    sound: Sound,
    category: SoundCategory,
    volume: Float = 1F,
    pitch: Float = 1F
) = playSound(location, sound, category, volume, pitch)

fun Player.play(location: Location = this.location, instrument: Instrument, note: Note) =
    playNote(location,  instrument, note)





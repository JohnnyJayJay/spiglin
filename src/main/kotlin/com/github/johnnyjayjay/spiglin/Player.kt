package com.github.johnnyjayjay.spiglin

import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

fun Player.kill() {
    health = 0.0
}

fun Player.heal() {
    health = maxHealth
}

fun Player.feed() {
    foodLevel = 20
}

fun Player.hideFrom(vararg players: Player) =
    players.forEach { it.hidePlayer(this) }

fun Player.hideFrom(players: Iterable<Player>) =
    players.forEach { it.hidePlayer(this) }

fun Player.hideFromAll() =
    hideFrom(Bukkit.getOnlinePlayers())

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

fun effect(
    type: PotionEffectType,
    duration: Int = Int.MAX_VALUE,
    amplifier: Int = 1,
    ambient: Boolean = true,
    particles: Boolean = true,
    icon: Boolean = true
) = PotionEffect(type, duration, amplifier, ambient, particles, icon)



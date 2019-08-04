package com.github.johnnyjayjay.spiglin

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.Recipe
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

val players: Collection<Player>
    get() = Bukkit.getOnlinePlayers()

fun broadcast(message: String) = Bukkit.broadcastMessage(message)

fun effect(
    type: PotionEffectType,
    duration: Int = Int.MAX_VALUE,
    amplifier: Int = 1,
    ambient: Boolean = true,
    particles: Boolean = true,
    icon: Boolean = true
) = PotionEffect(type, duration, amplifier, ambient, particles, icon)


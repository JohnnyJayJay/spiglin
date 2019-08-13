package com.github.johnnyjayjay.spiglin

import org.bukkit.Bukkit
import org.bukkit.entity.Player

/**
 * @see Bukkit.getOnlinePlayers
 */
val onlinePlayers: Collection<Player>
    get() = Bukkit.getOnlinePlayers()

/**
 * @see Bukkit.broadcastMessage
 */
fun broadcast(message: String) = Bukkit.broadcastMessage(message)


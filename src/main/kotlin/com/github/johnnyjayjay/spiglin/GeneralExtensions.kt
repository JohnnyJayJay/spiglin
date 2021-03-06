package com.github.johnnyjayjay.spiglin

import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.entity.Player
import org.bukkit.plugin.PluginManager

/**
 * @see Bukkit.getOnlinePlayers
 */
val onlinePlayers: Collection<Player>
    get() = Bukkit.getOnlinePlayers()

/**
 * @see Bukkit.broadcastMessage
 */
fun broadcast(message: String) = Bukkit.broadcastMessage(message)

/**
 * A singleton that delegates to [Bukkit.getServer]
 */
object Server : Server by Bukkit.getServer()

/**
 * A singleton that delegates to [Bukkit.getPluginManager]
 */
object PluginManager : PluginManager by Bukkit.getPluginManager()
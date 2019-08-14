package com.github.johnnyjayjay.spiglin

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.Plugin

/**
 * @see Bukkit.getOnlinePlayers
 */
val onlinePlayers: Collection<Player>
    get() = Bukkit.getOnlinePlayers()

/**
 * @see Bukkit.broadcastMessage
 */
fun broadcast(message: String) = Bukkit.broadcastMessage(message)

inline fun <T : Event> Plugin.hear(crossinline action: (T) -> Unit) {
    Bukkit.getPluginManager().registerEvents(object : Listener {
        @EventHandler
        fun onEvent(event: T) = action(event)
    }, this)
}


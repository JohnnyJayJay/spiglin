package com.github.johnnyjayjay.spiglin

import org.bukkit.entity.Player
import org.bukkit.metadata.MetadataValueAdapter
import org.bukkit.plugin.Plugin

//TODO
data class ConstantMetadata<T>(val data: T, val plugin: Plugin) : MetadataValueAdapter(plugin) {

    override fun invalidate() = Unit

    override fun value() = data

}

data class VariableMetadata<T>(var data: T? = null, val plugin: Plugin) : MetadataValueAdapter(plugin) {

    override fun value() = data

    override fun invalidate() = Unit

}

fun <T> T.asConstantMetadata(plugin: Plugin) =
    ConstantMetadata(this, plugin)

fun <T> T.asVariableMetadata(plugin: Plugin) =
    VariableMetadata(this, plugin)

inline fun <reified T> Player.firstMetadata(key: String) =
    this.getMetadata(key).firstOrNull()?.let { it.value() as T }
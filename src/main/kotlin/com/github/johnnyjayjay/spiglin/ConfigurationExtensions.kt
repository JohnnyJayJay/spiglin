package com.github.johnnyjayjay.spiglin

import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.serialization.ConfigurationSerializable

/**
 * Gets the requested [ConfigurationSerializable] object at the given
 * path.
 *
 * If the Object does not exist but a default value has been specified, this
 * will return the default value. If the Object does not exist and no
 * default value was specified, this will return null.
 *
 * @param T the type of the object
 * @param path the path of the object
 * @see ConfigurationSection.getSerializable
 *
 * @return the requested [ConfigurationSerializable] object or null if none is found
 */
inline fun <reified T : ConfigurationSerializable> ConfigurationSection.getSerializable(path: String): T? =
    getSerializable(path, T::class.java)

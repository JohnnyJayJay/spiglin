package com.github.johnnyjayjay.spiglin

import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

/**
 * @see PotionEffect constructor
 */
fun effect(
    type: PotionEffectType,
    duration: Int = Int.MAX_VALUE,
    amplifier: Int = 1,
    ambient: Boolean = true,
    particles: Boolean = true,
    icon: Boolean = true
) = PotionEffect(type, duration, amplifier, ambient, particles, icon)
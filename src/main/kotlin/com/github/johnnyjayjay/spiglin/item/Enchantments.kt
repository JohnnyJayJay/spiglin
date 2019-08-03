package com.github.johnnyjayjay.spiglin.item

import org.bukkit.enchantments.Enchantment

class EnchantmentNode {

    val set: MutableSet<Container> = mutableSetOf()

    fun with(enchantment: Enchantment) =
        Container(enchantment).also { set.add(it) }

    inline fun with(vararg enchantments: Enchantment, config: Container.(Enchantment) -> Unit) {
        enchantments.forEach {
            with(it).apply { config(it) }
        }
    }

    fun with(enchantments: Map<Enchantment, Int>) {
        set.addAll(enchantments.map { Container(it.key, it.value) })
    }

    data class Container internal constructor(val enchantment: Enchantment, var level: Int = 1) {

        infix fun level(level: Int) {
            this.level = level
        }
    }
}
package com.github.johnnyjayjay.spiglin.item

import org.bukkit.enchantments.Enchantment

class EnchantmentNode {

    internal val set: MutableSet<EnchantmentContainer> = mutableSetOf()

    fun enchant(unrestricted: Boolean = false) = EnchantmentContainer(unrestricted)
}

data class EnchantmentContainer internal constructor(val unrestricted: Boolean) {
    lateinit var enchantment: Enchantment
    var level: Int = 1

    infix fun with(enchantment: Enchantment): EnchantmentContainer {
        this.enchantment = enchantment
        return this
    }

    infix fun level(level: Int) {
        this.level = level
    }
}
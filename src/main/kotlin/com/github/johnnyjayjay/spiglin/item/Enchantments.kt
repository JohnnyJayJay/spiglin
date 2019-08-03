package com.github.johnnyjayjay.spiglin.item

import org.bukkit.enchantments.Enchantment

/**
 * A class used to apply enchantments to ItemMetas and ItemStacks.
 *
 * @property set A [Set] containing the [EnchantmentContainer]s created within this node.
 * @see org.bukkit.inventory.ItemStack.enchant
 * @see org.bukkit.inventory.meta.ItemMeta.enchant
 */
class EnchantmentNode {

    private val _set: MutableSet<EnchantmentContainer> = mutableSetOf()

    val set: Set<EnchantmentContainer> = _set

    /**
     * Adds an enchantment level 1 to this node and returns the newly
     * created EnchantmentContainer (which can be used to manipulate the level).
     */
    fun with(enchantment: Enchantment) =
        EnchantmentContainer(enchantment).also { _set.add(it) }

    /**
     * Adds the given enchantments to this node and runs the config function for each.
     */
    inline fun with(vararg enchantments: Enchantment, config: EnchantmentContainer.(Enchantment) -> Unit) {
        enchantments.forEach {
            with(it).apply { config(it) }
        }
    }

    /**
     * Adds the given map of enchantments and their corresponding level to this node.
     */
    fun with(enchantments: Map<Enchantment, Int>) {
        _set.addAll(enchantments.map { EnchantmentContainer(it.key, it.value) })
    }
}


data class EnchantmentContainer internal constructor(val enchantment: Enchantment, var level: Int = 1) {

    /**
     * Sets the level for this EnchantmentContainer.
     */
    infix fun level(level: Int) {
        this.level = level
    }
}
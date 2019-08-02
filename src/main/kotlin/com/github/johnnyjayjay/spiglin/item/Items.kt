package com.github.johnnyjayjay.spiglin.item

import com.google.common.collect.Multimap
import com.google.common.collect.Multimaps
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.lang.IllegalArgumentException

internal val NEW_LINE_SPLIT = "\n".toRegex()

const val STACK = 64

inline fun item(body: ItemStackBuilder.() -> Unit) =
    ItemStackBuilder().apply(body).build()

class ItemStackBuilder {

    lateinit var type: Material
    var durability: Short? = null
    var amount: Int = 1
    var meta: ItemMeta? = null

    private var enchantments: MutableSet<EnchantmentContainer>? = null

    inline fun <reified T : ItemMeta> meta(body: T.() -> Unit) {
        meta = itemMeta(type, body)
    }

    fun enchantments(body: EnchantmentNode.() -> Unit) {
        enchantments = EnchantmentNode().apply(body).set
    }

    fun build(): ItemStack {
        val itemStack = ItemStack(type, amount)
        if (durability != null) {
            itemStack.durability = durability!!
        }
        enchantments?.forEach { itemStack.addEnchantment(it) }
        itemStack.itemMeta = meta
        return itemStack
    }

    private fun ItemStack.addEnchantment(container: EnchantmentContainer) = with(container) {
        if (unrestricted) {
            addUnsafeEnchantment(enchantment, level)
        } else {
            addEnchantment(enchantment, level)
        }
    }
}

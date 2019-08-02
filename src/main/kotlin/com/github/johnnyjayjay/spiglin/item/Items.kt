package com.github.johnnyjayjay.spiglin.item

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

internal val NEW_LINE_SPLIT = "\n".toRegex()

inline fun item(type: Material, body: ItemStack.() -> Unit) =
    ItemStack(type).apply(body)

inline fun item(copy: ItemStack, body: ItemStack.() -> Unit) =
    ItemStack(copy).apply(body)

inline fun <reified T : ItemMeta> ItemStack.meta(body: T.() -> Unit) {
    itemMeta = itemMeta(type, body)
}

inline fun ItemStack.enchantments(body: EnchantmentNode.() -> Unit) {
    EnchantmentNode().apply(body).set.forEach {
        if (it.unrestricted) {
            addUnsafeEnchantment(it.enchantment, it.level)
        } else {
            addEnchantment(it.enchantment, it.level)
        }
    }
}

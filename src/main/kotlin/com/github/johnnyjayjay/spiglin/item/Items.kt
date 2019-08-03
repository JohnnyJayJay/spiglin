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
    val newMeta = itemMeta(type, body)
    itemMeta = newMeta
}

inline fun ItemStack.enchant(unsafe: Boolean = false, body: EnchantmentNode.() -> Unit) {
    val addMethod = if (unsafe) ::addUnsafeEnchantment else ::addEnchantment
    EnchantmentNode().apply(body).let {
        it.set.forEach { container ->
            val (enchantment, level) = container
            addMethod(enchantment, level)
        }
    }
}


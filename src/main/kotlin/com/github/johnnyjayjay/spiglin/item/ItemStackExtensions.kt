package com.github.johnnyjayjay.spiglin.item

import com.github.johnnyjayjay.spiglin.interaction.item.InteractableItem
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

internal val NEW_LINE_SPLIT = "\n".toRegex()

/**
 * Creates a new [ItemStack] based on the given type and applies the given body to it.
 */
inline fun item(type: Material, interactive: Boolean = false, body: ItemStack.() -> Unit) =
    (if (interactive) InteractableItem(ItemStack(type)) else ItemStack(type)).apply(body)

/**
 * Creates a new [ItemStack] based on another ItemStack and applies the given body to it.
 *
 * @param interactive Whether this item should be an instance
 */
inline fun item(copy: ItemStack, interactive: Boolean = false, body: ItemStack.() -> Unit) =
    (if (interactive) InteractableItem(copy) else ItemStack(copy)).apply(body)

/**
 * Assigns and accesses this ItemStack's [ItemMeta].
 *
 * @see itemMeta
 */
inline fun <reified T : ItemMeta> ItemStack.meta(body: T.() -> Unit) {
    val newMeta = itemMeta(type, body)
    itemMeta = newMeta
}

/**
 * Creates an [EnchantmentNode] for this ItemStack, applies the given
 * body and adds all enchantments configured to this ItemStack.
 */
inline fun ItemStack.enchant(unsafe: Boolean = false, body: EnchantmentNode.() -> Unit) {
    val addMethod = if (unsafe) ::addUnsafeEnchantment else ::addEnchantment
    EnchantmentNode().apply(body).let {
        it.set.forEach { container ->
            val (enchantment, level) = container
            addMethod(enchantment, level)
        }
    }
}
package com.github.johnnyjayjay.spiglin

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

inline fun item(body: ItemStackBuilder.() -> Unit): ItemStack {
    val builder = ItemStackBuilder()
    builder.body()
    return builder.build()
}

inline fun itemMeta(material: Material, body: ItemMetaBuilder.() -> Unit): ItemMeta? {
    val builder = ItemMetaBuilder()
    builder.body()
    return builder.build(material)
}

class ItemStackBuilder {

    lateinit var type: Material
    var amount: Int = 1

    private var enchantments: MutableList<EnchantmentNode>? = null
    private var metaBuilder: ItemMetaBuilder? = null

    fun enchant(unsafe: Boolean = false): EnchantmentNode {
        if (enchantments == null)
            enchantments = mutableListOf()
        val node = EnchantmentNode(unsafe)
        enchantments!!.add(node)
        return node
    }

    fun meta(body: ItemMetaBuilder.() -> Unit) {
        if (metaBuilder == null)
            metaBuilder = ItemMetaBuilder()
        metaBuilder!!.body()
    }

    fun build(): ItemStack {
        val itemStack = ItemStack(type, amount)
        enchantments?.forEach { itemStack.addEnchantment(it) }
        itemStack.itemMeta = metaBuilder?.build(type)
        return itemStack
    }

    private fun ItemStack.addEnchantment(node: EnchantmentNode) = with (node) {
        if (unsafe) {
            addUnsafeEnchantment(enchantment, level)
        } else {
            addEnchantment(enchantment, level)
        }
    }

    companion object {
        const val STACK = 64
    }
}

class EnchantmentNode internal constructor(val unsafe: Boolean) {

    lateinit var enchantment: Enchantment
    var level: Int = 1

    infix fun with(enchantment: Enchantment): EnchantmentNode {
        this.enchantment = enchantment
        return this
    }

    infix fun level(level: Int): EnchantmentNode {
        this.level = level
        return this
    }

}

class ItemMetaBuilder {

    var unbreakable: Boolean = false
    var lore: String? = null
    var displayName: String? = null
    var localizedName: String? = null
    var customModelData: Int? = null
    var flags: Iterable<ItemFlag> = emptyList()

    private var attributes: Attributes? = null

    fun build(material: Material): ItemMeta? {
        val meta = Bukkit.getItemFactory().getItemMeta(material) ?: return null
        meta.isUnbreakable = unbreakable
        meta.lore = lore?.split(splitRegex)?.toList()
        meta.setDisplayName(displayName)
        meta.setLocalizedName(localizedName)
        meta.setCustomModelData(customModelData)
        flags.forEach { meta.addItemFlags(it) }
        return meta
    }

    fun attributes(body: Attributes.() -> Unit) {
        if (attributes == null)
            attributes = Attributes()
        attributes!!.body()
    }

    companion object {
        private val splitRegex = "\n".toRegex()
    }
}

class Attributes internal constructor() {

    internal val modifiers: Multimap<Attribute, AttributeModifier> =
        Multimaps.newListMultimap(emptyMap()) { mutableListOf() }

    infix fun modify(attribute: Attribute) = ModifierNode(attribute)

    inner class ModifierNode internal constructor(private val attribute: Attribute) {

        infix fun with(modifier: AttributeModifier) {
            modifiers.put(attribute, modifier)
        }

        infix fun with(modifiers: Iterable<AttributeModifier>) {
            this@Attributes.modifiers.putAll(attribute, modifiers)
        }

        fun allOf(vararg modifiers: AttributeModifier) = modifiers.toList()
    }
}
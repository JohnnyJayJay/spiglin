package com.github.johnnyjayjay.spiglin

import com.google.common.collect.Multimap
import com.google.common.collect.Multimaps
import org.apache.commons.lang.Validate
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

val LORE_SPLIT_REGEX = "\n".toRegex()

const val STACK = 64

inline fun item(body: ItemStackBuilder.() -> Unit) =
    ItemStackBuilder().apply(body).build()

inline fun <reified T : ItemMeta> itemMeta(material: Material, body: T.() -> Unit): T {
    val meta = Bukkit.getItemFactory().getItemMeta(material)
    Validate.isTrue(meta is T, "ItemMeta for provided material does not match actual type parameter")
    meta as T
    meta.body()
    return meta
}

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

var ItemMeta.stringLore: String?
    get() = lore?.joinToString("\n")
    set(value) {
        lore = value?.split(LORE_SPLIT_REGEX)
    }

var ItemMeta.displayName: String?
    get() = displayName
    set(value) {
        setDisplayName(value)
    }

var ItemMeta.customModelData: Int?
    get() = customModelData
    set(value) {
        setCustomModelData(value)
    }

fun ItemMeta.flags(vararg flags: ItemFlag) = addItemFlags(*flags)

fun ItemMeta.flag(flag: ItemFlag) = addItemFlags(flag)

fun ItemMeta.attributes(body: Attributes.() -> Unit) {
    if (attributeModifiers == null)
        attributeModifiers = Multimaps.newListMultimap(mutableMapOf()) { mutableListOf() }
    attributeModifiers!!.putAll(Attributes().apply(body).modifiers)
}

fun ItemMeta.enchantments(body: EnchantmentNode.() -> Unit) {
    EnchantmentNode().apply(body).set.forEach {
        addEnchant(it.enchantment, it.level, it.unrestricted)
    }
}

class Attributes internal constructor() {

    internal val modifiers: Multimap<Attribute, AttributeModifier> =
        Multimaps.newListMultimap(emptyMap()) { mutableListOf() }

    fun modify(attribute: Attribute) = ModifierNode(attribute)

    inner class ModifierNode internal constructor(private val attribute: Attribute) {

        infix fun with(modifier: AttributeModifier) {
            modifiers.put(attribute, modifier)
        }

        infix fun with(modifiers: Iterable<AttributeModifier>) {
            this@Attributes.modifiers.putAll(attribute, modifiers)
        }
    }
}
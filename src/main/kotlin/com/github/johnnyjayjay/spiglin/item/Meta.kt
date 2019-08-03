package com.github.johnnyjayjay.spiglin.item

import com.google.common.collect.Multimap
import com.google.common.collect.Multimaps
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.ItemMeta
import java.lang.IllegalArgumentException

inline fun <reified T : ItemMeta> itemMeta(material: Material, body: T.() -> Unit) =
    Bukkit.getItemFactory().getItemMeta(material)
        .let { it as? T }
        ?.apply(body)
        ?: throw IllegalArgumentException("ItemMeta for provided material does not match actual type parameter")


var ItemMeta.stringLore: String?
    get() = lore?.joinToString("\n")
    set(value) {
        lore = value?.split(NEW_LINE_SPLIT)
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

inline fun ItemMeta.enchant(ignoringRestrictions: Boolean = false, body: EnchantmentNode.() -> Unit) {
    EnchantmentNode().apply(body).set.forEach {
        addEnchant(it.enchantment, it.level, ignoringRestrictions)
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
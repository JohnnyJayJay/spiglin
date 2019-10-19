package com.github.johnnyjayjay.spiglin.item

import com.google.common.collect.*
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.ItemMeta
import java.lang.IllegalArgumentException

/**
 * Creates a new ItemMeta based on a [Material] and applies the given body to it.
 *
 * @param T The ItemMeta type to be created.
 */
inline fun <reified T : ItemMeta> itemMeta(material: Material, body: T.() -> Unit) =
    Bukkit.getItemFactory().getItemMeta(material)
        .let { it as? T }
        ?.apply(body)
        ?: throw IllegalArgumentException("ItemMeta for provided material does not match actual type parameter")

/**
 * Returns the lore joined to a String with new lines, sets the lore by splitting the given String at \n.
 */
var ItemMeta.stringLore: String?
    get() = lore?.joinToString("\n")
    set(value) {
        lore = value?.split(NEW_LINE_SPLIT)
    }

/**
 * @see ItemMeta.getDisplayName
 * @see ItemMeta.setDisplayName
 */
var ItemMeta.name: String?
    get() = if (hasDisplayName()) displayName else null
    set(value) {
        setDisplayName(value)
    }

/**
 * @see ItemMeta.getCustomModelData
 * @see ItemMeta.setCustomModelData
 */
var ItemMeta.modelData: Int?
    get() = if (hasCustomModelData()) customModelData else null
    set(value) {
        setCustomModelData(value)
    }

/** Adds the given [ItemFlag]s to this ItemMeta. */
fun ItemMeta.flags(vararg flags: ItemFlag) = addItemFlags(*flags)

/** Adds the given [ItemFlag] to this ItemMeta. */
fun ItemMeta.flag(flag: ItemFlag) = addItemFlags(flag)

/**
 * Creates a new [Attributes] scope, applies the given body to it and adds the
 * configured attribute modifiers to this ItemMeta.
 */
inline fun ItemMeta.attributes(body: Attributes.() -> Unit) {
    val attributes = Attributes().apply(body)
    val modifiers = attributes.modifiers
    attributeModifiers = ArrayListMultimap.create(
        if (attributeModifiers == null) ArrayListMultimap.create() else attributeModifiers!!
    ).also { it.putAll(modifiers) }
}

/**
 * Creates an [EnchantmentNode] for this ItemMeta, applies the given
 * body and adds all enchantments configured to this ItemMeta.
 */
inline fun ItemMeta.enchant(ignoringRestrictions: Boolean = false, body: EnchantmentNode.() -> Unit) {
    EnchantmentNode().apply(body).set.forEach {
        addEnchant(it.enchantment, it.level, ignoringRestrictions)
    }
}

/**
 * A class used to add [AttributeModifier]s to ItemMetas.
 *
 * @see org.bukkit.inventory.meta.ItemMeta.attributes
 */
class Attributes {

    private val _modifiers: Multimap<Attribute, AttributeModifier> = ArrayListMultimap.create()

    /**
     * A copy of the modifiers linked to this instance.
     */
    val modifiers: Multimap<Attribute, AttributeModifier>
        get() = ArrayListMultimap.create(_modifiers)

    /**
     * Creates a [ModifierNode] for the given [Attribute] and returns it.
     */
    fun modify(attribute: Attribute) = ModifierNode(attribute)

    /**
     * A class as a fluent interface to add attribute modifiers.
     */
    inner class ModifierNode internal constructor(private val attribute: Attribute) {

        /**
         * Adds the given [AttributeModifier] to this Attribute.
         */
        infix fun with(modifier: AttributeModifier) {
            _modifiers.put(attribute, modifier)
        }

        /**
         * Adds the given [AttributeModifier]s to this Attribute.
         */
        infix fun with(modifiers: Iterable<AttributeModifier>) {
            this@Attributes._modifiers.putAll(attribute, modifiers)
        }
    }
}
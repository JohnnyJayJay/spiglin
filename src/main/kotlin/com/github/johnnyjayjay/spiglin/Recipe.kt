package com.github.johnnyjayjay.spiglin

import org.bukkit.NamespacedKey
import org.bukkit.inventory.*

inline fun shapedRecipe(result: ItemStack, body: ShapedRecipe.() -> Unit) =
    ShapedRecipe(result).apply(body)

inline fun shapelessRecipe(result: ItemStack, body: ShapelessRecipe.() -> Unit) =
    ShapelessRecipe(result).apply(body)
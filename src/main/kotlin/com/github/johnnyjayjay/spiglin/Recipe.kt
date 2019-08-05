package com.github.johnnyjayjay.spiglin

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe

inline fun shapedRecipe(result: ItemStack, body: ShapedRecipe.() -> Unit) =
    ShapedRecipe(result).apply(body)

inline fun shapelessRecipe(result: ItemStack, body: ShapelessRecipe.() -> Unit) =
    ShapelessRecipe(result).apply(body)
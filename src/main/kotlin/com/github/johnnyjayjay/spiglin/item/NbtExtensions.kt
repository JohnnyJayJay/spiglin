@file:NmsDependent

package com.github.johnnyjayjay.spiglin.item

import com.github.johnnyjayjay.compatre.NmsDependent
import net.minecraft.server.v1_14_R1.*
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import java.lang.invoke.MethodHandles

private val handleField =
    CraftItemStack::class.java.getDeclaredField("handle").also { it.isAccessible = true }

private val handleGetter = MethodHandles.lookup().unreflectGetter(handleField)

private val handleSetter = MethodHandles.lookup().unreflectSetter(handleField)

/**
 * The handle (nms ItemStack) of this bukkit ItemStack.
 */
var ItemStack.nms: net.minecraft.server.v1_14_R1.ItemStack
    get() = handleGetter.invoke(this) as net.minecraft.server.v1_14_R1.ItemStack
    set(value) {
        handleSetter.invoke(this, value)
    }

/**
 * Creates a new NBTTagCompound, applies the given body and returns it.
 */
inline fun nbtCompound(body: NBTTagCompound.() -> Unit) = NBTTagCompound().apply(body)

/**
 * Creates a new NBTTagList and adds the given elements to it.
 */
fun nbtList(vararg elements: NBTBase) = NBTTagList().apply { addAll(elements) }

/**
 * Returns this Boolean as a new NBTTagByte.
 */
fun Boolean.nbt() = NBTTagByte(if (this) 1 else 0)

/**
 * Returns this Byte as a new NBTTagByte.
 */
fun Byte.nbt() = NBTTagByte(this)

/**
 * Returns this ByteArray as a new NBTTagByteArray.
 */
fun ByteArray.nbt() = NBTTagByteArray(this)

/**
 * Returns this Double as a new NBTTagDouble.
 */
fun Double.nbt() = NBTTagDouble(this)

/**
 * Returns this Float as a new NBTTagFloat.
 */
fun Float.nbt() = NBTTagFloat(this)

/**
 * Returns this Int as a new NBTTagInt.
 */
fun Int.nbt() = NBTTagInt(this)

/**
 * Returns this IntArray as a new NBTTagIntArray.
 */
fun IntArray.nbt() = NBTTagIntArray(this)

/**
 * Returns this Long as a new NBTTagLong.
 */
fun Long.nbt() = NBTTagLong(this)

/**
 * Returns this Short as a new NBTTagShort.
 */
fun Short.nbt() = NBTTagShort(this)

/**
 * Returns this String as a new NBTTagString.
 */
fun String.nbt() = NBTTagString(this)

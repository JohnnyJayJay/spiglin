@file:NmsDependent

package com.github.johnnyjayjay.spiglin.item

import com.github.johnnyjayjay.compatre.NmsDependent
import net.minecraft.server.v1_14_R1.*
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack

fun ItemStack.withNbt(compound: NBTTagCompound?): ItemStack =
    CraftItemStack.asNMSCopy(this).let {
        it.tag = compound
        CraftItemStack.asBukkitCopy(it)
    }

inline fun nbtCompound(body: NBTTagCompound.() -> Unit) = NBTTagCompound().apply(body)

fun nbtList(vararg elements: NBTBase) = NBTTagList().apply { addAll(elements) }

fun Boolean.nbt() = NBTTagByte(if (this) 1 else 0)

fun Byte.nbt() = NBTTagByte(this)

fun ByteArray.nbt() = NBTTagByteArray(this)

fun Double.nbt() = NBTTagDouble(this)

fun Float.nbt() = NBTTagFloat(this)

fun Int.nbt() = NBTTagInt(this)

fun IntArray.nbt() = NBTTagIntArray(this)

fun Long.nbt() = NBTTagLong(this)

fun Short.nbt() = NBTTagShort(this)

fun String.nbt() = NBTTagString(this)
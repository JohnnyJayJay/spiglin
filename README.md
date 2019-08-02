# Spiglin
Spiglin is a collection of [Kotlin](https://kotlinlang.org/) extensions and utilities 
for the Minecraft server software [Spigot/Bukkit](https://www.spigotmc.org/).

## Features
Spiglin provides several different components that improve certain parts of interacting 
with Bukkit's API, including two very small EDSLs (Embedded Domain Specific Language).

### Item Building EDSL
This EDSL makes the creation of custom items very simple and concise. 
Most of it is self-explanatory:
```kotlin
val item: ItemStack = item {
    type = Material.GRASS_BLOCK // required
    amount = STACK // default is 1; "STACK" is a constant (=64)
    enchantments {
        enchant(unrestricted = true) with Enchantment.FIRE_ASPECT level 3 // adds fire aspect 3 as an enchantment
    }
    meta { // opens the ItemMeta scope in which properties of ItemMeta can be configured
        displayName = "Grass block of doom"
        lore = "A very\npowerful weapon" // instead of Lists, normal Strings with new lines are used for lore
        unbreakable = true
        flag(ItemFlag.HIDE_UNBREAKABLE)
        attributes {
            modify(Attribute.GENERIC_ATTACK_SPEED) with someModifier // both single modifiers and Lists of modifiers work here
        }   
    }   
}
```



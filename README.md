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
val item: ItemStack = item(Material.GRASS_BLOCK) {
    amount = maxStackSize // default is 1
    enchantments {
        enchant(unrestricted = true) with Enchantment.FIRE_ASPECT level 3 // adds fire aspect 3 as an enchantment
    }
    meta { // meta {} also has a type parameter that lets you work with more specific ItemMetas.
        displayName = "Grass block of doom"
        stringLore = "A very\npowerful weapon" // instead of Lists, normal Strings can be used with stringLore. This just delegates to the normal lore.
        unbreakable = true
        flag(ItemFlag.HIDE_UNBREAKABLE)
        attributes {
            modify(Attribute.GENERIC_ATTACK_SPEED) with someModifier // both single modifiers and Lists of modifiers work here
        }   
    }   
}
```
`ItemMeta`s can also be built without an item attached. See `<T> itemMeta(Material, T.() -> Unit)`.

Note that some things (like `attributes`) are exclusive to certain Spigot versions. Just remember that this EDSL 
is just a wrapper of what is possible anyway. If a certain feature does not exist in your version but does in Spiglin, 
I highly discourage you from using it. You will most likely get runtime errors.

If you are uncertain, check out the documentation for the individual methods and variables.

### Inventory Utilities
#### Builder EDSL
The inventory EDSL works similar to the ItemStack EDSL and is fully compatible with it.
```kotlin
val inventory: Inventory = inventory {
    rows = 3
    title = "Click the button"
    items {
        fill() with borderItem except (1 to 4) // fills every slot with the provided item, excluding the ones speficied in "except" (also works with linear IntRanges or Iterable<Pair<Int, Int>>)
        grid[1, 4] = buttonItem withAction { event -> event.player.sendMessage("Click!") } // sets the item in the middle to "buttonItem" and attaches an action that is triggered should it be clicked.
        forEachSlot { row, column -> println("$row - $column")} // you can also perform custom operations with forEachSlot
    }
}
```
In order for the interaction (`withAction`) to work, you need to register `ItemInteractionListener` as a listener 
via the `PluginManager`.

#### Items
You may assign the `items` variable differently, by...
```kotlin
items = Items.from(contents) // ...providing linear inventory contents
items = Items.from(grid) // ...providing a 2D array of ItemStacks (row-column)
items = Items.from(
    formatString = """
            |xxxxbxxxx
        """.trimMargin(), 
    bindings = mapOf('x' to borderItem, 'b' to buttonItem)
) // ...using a String format where every character represents an ItemStack!
```
#### Extensions
Also, there are a few extensions and new operators for `Inventory`:
```kotlin
val item: ItemStack = inventory[1, 4] // retrieves the ItemStack at the specified slot (Pair<Int, Int> or a linear index can be used, too)
inventory[1, 4] = newItemStack // sets the ItemStack at the specified slot (Pair<Int, Int> or a linear index can be used, too)
val firstRow: Array<ItemStack?> = inventory[0..8] // retrieves a specific range of items from the inventory
val items: Items = inventory.items // variable of type Items that can be retrieved...
inventory.items = items // ...or re-assigned
inventory.openTo(player) // player.openInventory(inventory)
```
There are some additional, independent utility functions:
```kotlin
val linearIndex = linearInventoryIndex(row = 1, column = 3) // converts a 2 dimensional index to a linear one (also works with Pair<Int, Int>)
val (row, column) = twoDimensionalInventoryIndex(16) // converts a linear index to a Pair<Int, Int> that represent row and column of that index
```

### Schedulers
In the `scheduler` package, you can find convenient wrappers and extensions for the 
scheduler actions available in Bukkit.
```kotlin
// .runTask
run(plugin = plugin) { } 
// .runTaskLater
delay(ticks = 20 * 5, plugin = plugin) { } 
// .runTaskTimer
schedule(delay = 20, period = 20 * 5, plugin = plugin) {} 

// repeat from the beginning to the end of the range
repeat(range = 1..5, delay = 20, period = 20 * 5, plugin = plugin) { current ->
    
}
```
All of these functions also have an `async` parameter that can be used to 
use them asynchronously.
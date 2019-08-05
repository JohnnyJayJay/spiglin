# Spiglin
Spiglin is a collection of [Kotlin](https://kotlinlang.org/) extensions and utilities 
for the Minecraft server software [Spigot/Bukkit](https://www.spigotmc.org/).

## Features
Spiglin provides several different components that improve certain parts of interacting 
with Bukkit's API, including two very small EDSLs (Embedded Domain Specific Language).

The following components of the Bukkit API are covered and extended by this collection:

- `ItemStack`
- `Inventory`
- `Player`
- `Vector & Location`

### Items
This EDSL makes the creation of custom items very simple and concise. 
Most of it is self-explanatory:
```kotlin
val item: ItemStack = item(Material.GRASS_BLOCK) {
    amount = maxStackSize // default is 1
    enchant(unsafe = true) {
        with(Enchantment.FIRE_ASPECT) level 3 // adds fire aspect 3 as an enchantment
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

### Interactive Items
Spiglin provides a special kind of `ItemStack` called `InteractiveItem`. It is used to attach 
actions to `ItemStack`s that are executed on specific events that involve this `ItemStack`.
To get an interactive version of an `ItemStack`, you can call `ItemStack#toInteractiveItem()` 
or `interactive(ItemStack)`.

Attaching an action to an `InteractiveItem` works like this:
```kotlin
val button = interactive(item) // creates a new, interactive version of the item
    .attach(PlayerInteractEvent::class) { // attaches an action
        it.player.kill() 
    } 
player.inventory += button // adds the item to the player's inventory
```
This action will be executed if a PlayerInteractEvent involves this item.

### Inventories
The inventory EDSL works similar to the ItemStack EDSL and is fully compatible with it.
```kotlin
val inventory: Inventory = inventory(rows = 3, title = "Click the button") {
    // you can assign the contents directly or use the get/set operators
    this[slot(1, 4)] = buttonItem
    this[all except slot(1, 4)] = borderItem // fills every slot with the provided item, excluding the ones speficied in "except" (also works with linear Iterable<Int>)
}
```
Note that the only supported inventory type is `InventoryType.CHEST`!

There are several other utility functions:
```kotlin
with (inventory) {
    val itemsInFirstRow = this[row(0)]
    val itemsInFirstColumn = this[column(0)]
    val itemsInSpecificSlots = this[slots(0 to 2, 1 to 3, 2 to 4)]
    val borderItems = this[border()]
    val cornerItems = this[corners]
    val itemsInRange = this[slot(1, 0)..slot(2, 3)]
    val allItems = this[all]
    val itemInFirstSlot = inventory[slot(0, 0)]
}
```
Of course, there are also operator overloads for setting items this way.

The inventory `get()/set()` operators are very flexible because they either generally 
accept `Int` or `Iterable<Int>`. Both represent linear indices, i.e. the same indices
that are used to access `inventory.contents`.

In these examples, utility functions like `slot(Int, Int)` were used to convert two 
dimensional indices (row - column) to linear ones. You may of course directly use 
linear indices, like so:
```kotlin
val item: ItemStack? = inventory[23]
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

// repeat from the beginning to the end of the provided progression
repeat(progression = 1..5, delay = 20, period = 20 * 5, plugin = plugin) { current ->
    
}
```
All of these functions also have an `async` parameter that can be used to 
use them asynchronously.

### Player
```kotlin
player.kill() // Applicable to any other Damageable
player.heal() // ""
player.feed()
player.hideFrom(otherPlayer) // hides this player from the provided player. Also takes varargs or Iterable<Player>
player.hideIf { !it.hasPermission("see") } // hides this player from all players matching the given predicate
player.hideFromAll() // Makes this player invisible for all players
player.play(effect = Effect.ANVIL_BREAK) // various utility functions with default parameters for #playSound, #playNote and #playEffect
```

### Vector & Location
This mainly adds operator overloading to these components.
```kotlin
val euclideanNorm = vector.abs
val negated = -vector // respectively: +vector
val isGreater = vector > otherVector // comparison based on the euclidean norm (also ==, !=, >=, <=, <)
val added = vector + otherVector
val subtracted = vector - otherVector
val dotProduct = vector * otherVector
val divided = vector / otherVector
val crossProduct = vector x otherVector
```
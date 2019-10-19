# Spiglin
Spiglin is a collection of [Kotlin](https://kotlinlang.org/) extensions and utilities 
for the Minecraft server software [Spigot/Bukkit](https://www.spigotmc.org/).

## Dependency
https://bintray.com/johnnyjayjay/spiglin/spiglin

## Features
Spiglin provides several different components that improve certain parts of interacting 
with Bukkit's API, including two very small EDSLs (Embedded Domain Specific Language).

The following components of the Bukkit API are covered and extended by this collection:

- `ItemStack`
- `Inventory`
- `Listener & Event`
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
        name = "Grass block of doom"
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

### Inventories
(Chest) Inventory creation:
```kotlin
val inventory: Inventory = inventory(rows = 3, title = "Click the button") {
    // you can assign the contents directly or use the get/set operators
    this[slot(1, 4)] = buttonItem
    this[all except slot(1, 4)] = borderItem // fills every slot with the provided item, excluding the ones speficied in "except" (also works with linear Iterable<Int>)
}
```

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

### Listeners & Events
Spiglin introduces 3 new and convenient ways to listen for events.

#### Ad hoc listeners
This allows you to listen to listen for events via generics and without making a 
listener class and method yourself.

Here's a simple listener that logs messages written to chat:
```kotlin
plugin.hear<AsyncPlayerChatEvent> {
    logger.log("Player ${it.player.name} sent a message in chat: ${it.message}")
}
```

#### Expectations
Expectations allow you to "wait" for specific events that meet specific conditions.
They unregister themselves once the expectation is met or timed out.

Here's a piece of code that teleports a player to a location, provided that they don't move:
```kotlin
plugin.expect<PlayerMoveEvent>(
    predicate = { it.player == player },
    timeout = 5,
    timeoutUnit = TimeUnit.SECONDS,
    timeoutAction = { player.teleport(location) },   
    action = { player.sendMessage("You moved!") }   
)
```

#### Subject specific listeners
With spiglin, several different types of object are considered to be "event subjects":

- `Block`
- `Chunk`
- `Entity`
- `Inventory`
- `ItemStack`
- `World`

You can attach subject specific listeners to instances of these types that will only be 
called if the event involves this instance.

For instance, take a look at this:
```kotlin
block.on<BlockBreakEvent> {
    it.player.sendMessage("You broke it :'(")
}
```
The code within the {} will only be called if this specific block is broken.

**Note that spiglin's list of subject related events is not guaranteed to be complete and does not
 derive from any Bukkit API,  i.e. some events may not work as expected.**
 
 **Feel free to expand the list of supported events for this feature. by submitting a Pull Request.**


### Schedulers
In the `scheduler` package, you can find convenient wrappers and extensions for the 
scheduler actions available in Bukkit.
```kotlin
// .runTask
plugin.run { } 
// .runTaskLater
plugin.delay(ticks = 20 * 5) { } 
// .runTaskTimer
plugin.schedule(delay = 20, period = 20 * 5) {} 

// repeat from the beginning to the end of the provided progression
plugin.repeat(progression = 1..5, delay = 20, period = 20 * 5) { current ->
    
}
```
All of these functions also have an `async` parameter that can be used to 
run them asynchronously.

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

### General
```kotlin
val players: Collection<Player> = onlinePlayers // shortcut for Bukkit.getOnlinePlayers()
broadcast("Hello, Minecraft!") // shortcut for Bukkit.broadcastMessage(String)
PluginManager.clearPlugins() // shortcut for Bukkit.getPluginManager()
Server.shutdown() // shortcut for Bukkit.getServer()
```

### Vector & Location
This mainly adds operator overloading to these components.
```kotlin
val euclideanNorm = vector.abs
val negated = -vector // respectively: +vector
val isGreater = vector > otherVector // comparison based on the euclidean norm (also >=, <=, <)
val added = vector + otherVector
val subtracted = vector - otherVector
val dotProduct = vector * otherVector
val crossProduct = vector x otherVector
```
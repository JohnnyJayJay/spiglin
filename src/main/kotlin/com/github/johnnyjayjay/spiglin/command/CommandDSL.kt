package com.github.johnnyjayjay.spiglin.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

/**
 * Registers a [CommandExecutor] with [name] defined by [convention] to this plugin.
 *
 * @throws IllegalArgumentException if [JavaPlugin.getCommand] returned null
 */
fun JavaPlugin.command(name: String, convention: CommandConvention.() -> Unit) {
    val command = getCommand(name)
    requireNotNull(command) { "Command is null, did you forget to register it in your plugin.yml?" }
    command.setExecutor(command(convention))
}

/**
 * Creates a new [CommandExecutor] of [command].
 *
 * @see CommandConvention
 */
fun command(command: CommandConvention.() -> Unit): CommandExecutor = CommandConvention().apply(command).build()

/**
 * Convention used in Command DSL.
 */
class CommandConvention internal constructor() {

    private var root: CommandExecutor = DelegatingCommand.invalidCommand
    private var children: MutableMap<String, CommandExecutor> = mutableMapOf()

    /**
     * Applies the [executor] as the root command at the current level.
     */
    fun rootCommand(executor: (CommandContext) -> Boolean) {
        root = commandExecutor(executor)
    }

    /**
     * Registers a single [executor] of [name] as a sub command at the current level.
     *
     * @see subCommand
     */
    fun subCommandExecutor(name: String, executor: (CommandContext) -> Boolean): Unit = subCommand(name) {
        rootCommand(executor)
    }

    /**
     * Registers another [CommandConvention] as a sub command at current level.
     */
    fun subCommand(name: String, command: CommandConvention.() -> Unit) {
        children[name] = CommandConvention().apply(command).build()
    }

    private fun commandExecutor(executor: (CommandContext) -> Boolean) =
        CommandExecutor { sender, command, label, args -> CommandContext(sender, command, label, args).run(executor) }

    internal fun build(): CommandExecutor = AutoCompletingCommand(root, children)
}

/**
 * Context of command execution used for DSL.
 *
 * @property sender Source of the command
 * @property command Command which was executed
 * @property label Alias of the command which was used
 * @property args Passed command arguments
 * @see CommandExecutor.onCommand
 */
data class CommandContext(val sender: CommandSender, val command: Command, val label: String, val args: Array<String>) {

    /**
     * The [Player] that executed this command.
     *
     * @throws IllegalStateException when this command was executed by the console
     */
    val player: Player
        get() = (sender as? Player) ?: error("Player is not available if command was executed in console.")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CommandContext) return false

        if (sender != other.sender) return false
        if (command != other.command) return false
        if (label != other.label) return false
        if (!args.contentEquals(other.args)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sender.hashCode()
        result = 31 * result + command.hashCode()
        result = 31 * result + label.hashCode()
        result = 31 * result + args.contentHashCode()
        return result
    }

}

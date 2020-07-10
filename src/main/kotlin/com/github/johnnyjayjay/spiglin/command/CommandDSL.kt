package com.github.johnnyjayjay.spiglin.command

import org.bukkit.command.*
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
     * Shortcut for command without custom tab completion
     *
     * @see rootCommand
     */
    fun rootCommand(
        consoleAllowed: Boolean = true,
        executor: (CommandContext) -> Boolean
    ): Unit = rootCommand(consoleAllowed, executor, null)

    /**
     * Applies the [executor] as the root command at the current level.
     *
     * @param consoleAllowed whether this command should be allowed to be executed in console or not
     * @param tabCompleter A lambda taking a [TabContext] returning a list of tab completions
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun rootCommand(
        consoleAllowed: Boolean = true,
        executor: (CommandContext) -> Boolean,
        tabCompleter: ((TabContext) -> MutableList<String>)?
    ) {
        root = if (tabCompleter != null) {
            tabCommandExecutor(executor, consoleAllowed, tabCompleter)
        } else {
            commandExecutor(executor)
        }
    }


    /**
     * Short cut to create a sub command executor without custom tab completion
     *
     * @see subCommandExecutor
     */
    fun subCommandExecutor(
        name: String, consoleAllowed: Boolean = true, executor: (CommandContext) -> Boolean
    ): Unit =
        subCommandExecutor(name, consoleAllowed, executor, null)

    /**
     * Registers a single [executor] of [name] as a sub command at the current level.
     *
     * @see subCommand
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun subCommandExecutor(
        name: String, consoleAllowed: Boolean = true,
        executor: (CommandContext) -> Boolean,
        tabCompleter: ((TabContext) -> MutableList<String>)?
    ): Unit = subCommand(name) {
        rootCommand(consoleAllowed, executor, tabCompleter)
    }

    /**
     * Registers another [CommandConvention] as a sub command at current level.
     */
    fun subCommand(name: String, command: CommandConvention.() -> Unit) {
        children[name] = CommandConvention().apply(command).build()
    }

    private fun commandExecutor(executor: (CommandContext) -> Boolean, consoleAllowed: Boolean = true) =
        CommandExecutor { sender, command, label, args ->
            if (!consoleAllowed && sender is ConsoleCommandSender) return@CommandExecutor sender.sendMessage("This command is not allowed to be executed in console!")
                .run { true }
            CommandContext(sender, command, label, args).run(executor)
        }

    private fun tabCommandExecutor(
        executor: (CommandContext) -> Boolean,
        consoleAllowed: Boolean = true,
        tabExecutor: (TabContext) -> MutableList<String>
    ) =
        object : CommandExecutor by commandExecutor(executor, consoleAllowed), TabExecutor {
            override fun onTabComplete(
                sender: CommandSender,
                command: Command,
                alias: String,
                args: Array<out String>
            ): MutableList<String> = tabExecutor(TabContext(sender, command, alias, args))

        }

    internal fun build(): CommandExecutor = AutoCompletingCommand(root, children)
}

/**
 * Context of a user pressing tab.
 *
 * @property sender the user who pressed tab
 * @property command command which the user is trying to execute
 * @property alias alias of the command the user is using
 * @property args arguments the user already provided
 */
data class TabContext(val sender: CommandSender, val command: Command, val alias: String, val args: Array<out String>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TabContext) return false

        if (sender != other.sender) return false
        if (command != other.command) return false
        if (alias != other.alias) return false
        if (!args.contentEquals(other.args)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sender.hashCode()
        result = 31 * result + command.hashCode()
        result = 31 * result + alias.hashCode()
        result = 31 * result + args.contentHashCode()
        return result
    }
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

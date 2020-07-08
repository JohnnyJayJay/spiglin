package com.github.johnnyjayjay.spiglin.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

/**
 * An implementation of [CommandExecutor] that can have child commands,
 * which are delegated to if the first argument of a command matches their label.
 *
 * @param default   The CommandExecutor to delegate to if none of the children match the arguments.
 *                  The default implementation for this parameter just returns false.
 * @param children  A Map of command label -> CommandExecutor representing the direct children of this command.
 */
open class DelegatingCommand(
    private val default: CommandExecutor = invalidCommand,
    protected val children: Map<String, CommandExecutor>
) : CommandExecutor {

    /**
     * Implementation of [CommandExecutor.onCommand] that tries to find a sub-command from [children] and defaults to [default] if none is found.
     *
     * @see CommandExecutor.onCommand
     */
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isNotEmpty()) {
            val childLabel = args[0]
            if (childLabel in children) {
                return children.getValue(childLabel).onCommand(sender, command, args[0], args.copyOfRange(1, args.size))
            }
        }
        return default.onCommand(sender, command, label, args)
    }

    companion object {
        internal val invalidCommand = CommandExecutor { _, _, _, _ -> false }
    }
}

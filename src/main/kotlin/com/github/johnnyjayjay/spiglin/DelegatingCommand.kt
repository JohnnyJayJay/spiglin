package com.github.johnnyjayjay.spiglin

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
class DelegatingCommand(
    private val default: CommandExecutor = invalidCommand,
    private val children: Map<String, CommandExecutor>
) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isNotEmpty()) {
            val childLabel = args[0]
            if (childLabel in children) {
                return children[childLabel]!!.onCommand(sender, command, args[0], args.copyOfRange(1, args.size))
            }
        }
        return default.onCommand(sender, command, label, args)
    }

    companion object {
        private val invalidCommand = CommandExecutor { _, _, _, _ -> false }
    }
}
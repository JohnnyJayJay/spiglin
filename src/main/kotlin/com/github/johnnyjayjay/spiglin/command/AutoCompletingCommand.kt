package com.github.johnnyjayjay.spiglin.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor

/**
 * Extension of [DelegatingCommand] that also implements [TabExecutor] to provide tab-complete for [children],
 *
 * @see DelegatingCommand
 * @see TabExecutor
 */
class AutoCompletingCommand(default: CommandExecutor = invalidCommand, children: Map<String, CommandExecutor>) :
    DelegatingCommand(default, children), TabExecutor {

    /**
     * Implementation of [TabExecutor.onTabComplete] that provides tab-complete for sub-commands from [children].
     *
     * @see TabExecutor.onTabComplete
     */
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String> {
        val invoke = args.firstOrNull()
        if (args.size > 1) {
            return (children.filterKeys {
                it.equals(
                    invoke,
                    ignoreCase = true
                )
            }.values.firstOrNull() as? AutoCompletingCommand)?.onTabComplete(
                sender,
                command,
                alias,
                args.copyOfRange(1, args.size)
            ) ?: mutableListOf()
        }

        return invoke?.let { notNullInvoke ->
            children.filterKeys {
                it.startsWith(
                    notNullInvoke,
                    ignoreCase = true
                )
            }.keys.toMutableList()
        } ?: children.keys.toMutableList()
    }
}

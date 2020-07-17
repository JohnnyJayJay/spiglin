package com.github.johnnyjayjay.spiglin

import com.github.johnnyjayjay.spiglin.command.DelegatingCommand as NewDelegatingCommand

/**
 * @see NewDelegatingCommand
 */
@Deprecated(
    "Commands have been moved to command package",
    ReplaceWith("DelegatingCommand", "com.github.johnnyjayjay.spiglin.command.DelegatingCommand")
)
typealias DelegatingCommand = NewDelegatingCommand

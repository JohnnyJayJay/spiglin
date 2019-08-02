package com.github.johnnyjayjay.spiglin.scheduler

import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

fun run(plugin: Plugin, async: Boolean = false, task: BukkitRunnable.() -> Unit): BukkitTask {
    val runnable = DelegatingRunnable(task)
    return if (async) {
        runnable.runTaskAsynchronously(plugin)
    } else {
        runnable.runTask(plugin)
    }
}

fun delay(ticks: Long, plugin: Plugin, async: Boolean = false, task: BukkitRunnable.() -> Unit): BukkitTask {
    val runnable = DelegatingRunnable(task)
    return if (async) {
        runnable.runTaskLaterAsynchronously(plugin, ticks)
    } else {
        runnable.runTaskLater(plugin, ticks)
    }
}

fun schedule(
    delay: Long = 0,
    period: Long = 20,
    plugin: Plugin,
    async: Boolean = false,
    task: BukkitRunnable.() -> Unit
): BukkitTask {
    val runnable = DelegatingRunnable(task)
    return if (async) {
        runnable.runTaskTimerAsynchronously(plugin, delay, period)
    } else {
        runnable.runTaskTimer(plugin, delay, period)
    }
}

inline fun repeat(
    range: IntRange,
    delay: Long = 0,
    period: Long = 20,
    plugin: Plugin,
    async: Boolean = false,
    crossinline task: BukkitRunnable.(Int) -> Unit
): BukkitTask {
    val runnable = object : BukkitRunnable() {
        private var current = range.first

        override fun run() {
            task(current)
            if (current == range.last) {
                cancel()
            }
            current += range.step
        }
    }

    return if (async) {
        runnable.runTaskTimerAsynchronously(plugin, delay, period)
    } else {
        runnable.runTaskTimer(plugin, delay, period)
    }
}

internal class DelegatingRunnable(private val task: BukkitRunnable.() -> Unit) : BukkitRunnable() {
    override fun run() = task()
}
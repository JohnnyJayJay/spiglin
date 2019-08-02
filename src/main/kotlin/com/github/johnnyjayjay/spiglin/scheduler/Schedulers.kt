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
    times: Int = -1,
    delay: Long = 0,
    period: Long = 20,
    plugin: Plugin,
    async: Boolean = false,
    crossinline task: BukkitRunnable.(Int) -> Unit
): BukkitTask {
    val runnable = object : BukkitRunnable() {
        private var timesRun = 0

        override fun run() {
            task(timesRun)
            if (timesRun == times) {
                cancel()
            }
            ++timesRun
        }
    }

    return if (async) {
        runnable.runTaskTimerAsynchronously(plugin, delay, period)
    } else {
        runnable.runTaskTimer(plugin, delay, period)
    }
}

inline fun countdown(
    from: Int,
    delay: Long = 0,
    period: Long = 20,
    plugin: Plugin,
    async: Boolean = false,
    crossinline task: BukkitRunnable.(Int) -> Unit
) = repeat(from, period, delay, plugin, async) { task(from - it) }

internal class DelegatingRunnable(private val task: BukkitRunnable.() -> Unit) : BukkitRunnable() {
    override fun run() = task()
}
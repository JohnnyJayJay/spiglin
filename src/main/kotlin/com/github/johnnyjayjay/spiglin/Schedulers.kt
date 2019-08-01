package com.github.johnnyjayjay.spiglin

import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable

fun run(plugin: Plugin, async: Boolean = false, task: BukkitRunnable.() -> Unit) {
    val runnable = DelegatingRunnable(task)
    if (async) {
        runnable.runTaskAsynchronously(plugin)
    } else {
        runnable.runTask(plugin)
    }
}

fun delay(ticks: Long, async: Boolean = false, plugin: Plugin, task: BukkitRunnable.() -> Unit) {
    val runnable = DelegatingRunnable(task)
    if (async) {
        runnable.runTaskLaterAsynchronously(plugin, ticks)
    } else {
        runnable.runTaskLater(plugin, ticks)
    }
}

fun schedule(
    plugin: Plugin,
    async: Boolean = false,
    delay: Long = 0,
    period: Long = 20,
    task: BukkitRunnable.() -> Unit
) {
    val runnable = DelegatingRunnable(task)
    if (async) {
        runnable.runTaskTimerAsynchronously(plugin, delay, period)
    } else {
        runnable.runTaskTimer(plugin, delay, period)
    }
}

inline fun repeat(
    plugin: Plugin,
    async: Boolean = false,
    delay: Long = 0,
    period: Long = 20,
    times: Int = -1,
    crossinline task: BukkitRunnable.(Int) -> Unit
) {
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

    if (async) {
        runnable.runTaskTimerAsynchronously(plugin, delay, period)
    } else {
        runnable.runTaskTimer(plugin, delay, period)
    }
}

inline fun countdown(
    plugin: Plugin,
    async: Boolean = false,
    delay: Long = 0,
    period: Long = 20,
    from: Int,
    crossinline task: BukkitRunnable.(Int) -> Unit
) {
    repeat(plugin, async, delay, period, from) {
        task(from - it)
    }
}

internal class DelegatingRunnable(private val task: BukkitRunnable.() -> Unit) : BukkitRunnable() {
    override fun run() = task()
}
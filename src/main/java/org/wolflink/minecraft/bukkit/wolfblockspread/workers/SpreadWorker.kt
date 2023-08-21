package org.wolflink.minecraft.bukkit.wolfblockspread.workers

import org.bukkit.Location
import org.wolflink.minecraft.bukkit.wolfblockspread.SpreadBlueprint

abstract class SpreadWorker(val workerId: Int,val blueprint: SpreadBlueprint,val args : List<String>) {
    var paused: Boolean = false
    var cancelled : Boolean = false
    abstract fun start(center : Location)
    fun pauseTask() {
        paused = true
    }
    fun continueTask() {
        paused = false
    }
    fun stop() {
        cancelled = true
    }
}
package org.wolflink.minecraft.bukkit.wolfblockspread

import org.bukkit.Location
import org.wolflink.minecraft.bukkit.wolfblockspread.workers.SpreadWorker
import java.util.concurrent.ConcurrentHashMap

object AICombatManager {
    private val siteMap = ConcurrentHashMap<Location, SpreadBlueprint>()
    val workerList = mutableListOf<SpreadWorker>()

    fun addSiteLoc(location: Location, blueprint: SpreadBlueprint) {
        siteMap[location] = blueprint
    }

    fun start(args: List<String>) {
        workerList.clear()
        for (entry in siteMap) {
            val id = SpreadManager.newWorker(entry.value, entry.key, args)
            val spreadWorker = SpreadManager.workerList.getOrNull(id) ?: continue
            workerList.add(spreadWorker)
        }
    }

    fun stop() {
        for (spreadWorker in workerList) {
            spreadWorker.stop()
        }
        workerList.clear()
    }

    fun pauseTask() {
        for (spreadWorker in workerList) {
            spreadWorker.pauseTask()
        }
    }

    fun continueTask() {
        for (spreadWorker in workerList) {
            spreadWorker.continueTask()
        }
    }
}
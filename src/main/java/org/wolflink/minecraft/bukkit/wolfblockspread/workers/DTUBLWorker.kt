package org.wolflink.minecraft.bukkit.wolfblockspread.workers

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.wolflink.minecraft.bukkit.wolfblockspread.SpreadBlueprint
import org.wolflink.minecraft.bukkit.wolfblockspread.SpreadManager
import org.wolflink.minecraft.bukkit.wolfblockspread.WolfBlockSpread
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class DTUBLWorker(workerId: Int,blueprint: SpreadBlueprint,args : List<String>) : SpreadWorker(workerId,blueprint,args) {
    private val map = ConcurrentHashMap<Pair<Int,Int>,Int>()

    private val nullBlockList = mutableListOf(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR)
    override fun start(center: Location) {

        val world = center.world ?: return
        val startTime = Calendar.getInstance().timeInMillis
        val blockData = blueprint.mat.createBlockData()
        val centerX = center.x.toInt()
        val centerZ = center.z.toInt()
        var maxHeight : Int = 192
        val minHeight = -64
        GlobalScope.launch {
            for (x in (centerX-blueprint.distance)..(centerX+blueprint.distance)) {
                for (z in (centerZ - blueprint.distance)..(centerZ+blueprint.distance)) {
                    map[Pair(x,z)] = minHeight
                }
            }
            // 每一层的时间
            val perLayerTime = blueprint.time / (maxHeight - minHeight).toDouble()

            loop@ while (map.isNotEmpty()) {
                for (entry in map.entries) {
                    if(cancelled)break@loop
                    if(paused)
                    {
                        delay(1000)
                        continue
                    }
                    val x = entry.key.first
                    val y = entry.value
                    val z = entry.key.second
                    if (world.getBlockAt(x, y, z).type !in nullBlockList) {
                        Bukkit.getScheduler().runTask(
                            WolfBlockSpread.INSTANCE,
                            Runnable { world.setBlockData(entry.key.first, entry.value, entry.key.second, blockData) })
                    }
                    if (entry.value + 1 > maxHeight) map.remove(entry.key)
                    else map[entry.key] = entry.value + 1
                }
                delay((perLayerTime * 1000L).toLong())
            }
            val stopTime = Calendar.getInstance().timeInMillis
            Bukkit.getLogger().info("DTUBLWorker 任务已完成，用时 ${(stopTime-startTime)/1000} 秒")
            SpreadManager.workerList.remove(workerId)
        }
    }
}
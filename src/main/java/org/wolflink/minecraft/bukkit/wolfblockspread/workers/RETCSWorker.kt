package org.wolflink.minecraft.bukkit.wolfblockspread.workers

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.wolflink.minecraft.bukkit.wolfblockspread.SpreadBlueprint
import org.wolflink.minecraft.bukkit.wolfblockspread.SpreadManager
import org.wolflink.minecraft.bukkit.wolfblockspread.WolfBlockSpread
import java.util.*

class RETCSWorker(blueprint: SpreadBlueprint,args : List<String>) : SpreadWorker(blueprint,args) {
    private val blockData = blueprint.mat.createBlockData()
    val minY = -64
    override fun start(center: Location) {
        val world = center.world ?: return
        val startTime = Calendar.getInstance().timeInMillis
        val centerX = center.x.toInt()
        val centerZ = center.z.toInt()
        val roundTime = blueprint.time / blueprint.distance.toDouble()

        var nowRadius = blueprint.distance

        GlobalScope.launch {

            while (nowRadius > 0)
            {
                if(cancelled)return@launch
                if(paused)
                {
                    delay(1000)
                    continue
                }
                setBlockInLineX(world,centerX - nowRadius,centerZ-nowRadius,centerZ+nowRadius)
                setBlockInLineX(world,centerX + nowRadius,centerZ-nowRadius,centerZ+nowRadius)
                setBlockInLineZ(world,centerZ - nowRadius,centerX-nowRadius,centerX+nowRadius)
                setBlockInLineZ(world,centerZ + nowRadius,centerX-nowRadius,centerX+nowRadius)
                nowRadius--
                delay((roundTime*1000).toLong())
            }
            val maxY = world.getHighestBlockYAt(centerX,centerZ)
            for(y in maxY downTo minY)
            Bukkit.getScheduler().runTask(WolfBlockSpread.INSTANCE, Runnable { world.setBlockData(centerX,y,centerZ,blockData) })
            val stopTime = Calendar.getInstance().timeInMillis
            Bukkit.getLogger().info("UTDBLWorker 任务已完成，用时 ${(stopTime-startTime)/1000} 秒")
            SpreadManager.workerList.remove(this@RETCSWorker)
        }
    }
    fun setBlockInLineX(world : World,x : Int,z1 : Int,z2 : Int)
    {
        val small : Int
        val big : Int
        if(z1 <= z2)
        {
            small = z1
            big = z2
        }
        else
        {
            small = z2
            big = z1
        }
        for (z in small..big)
        {
            val maxY = world.getHighestBlockYAt(x,z)
            for (y in maxY downTo minY)
            {
                Bukkit.getScheduler().runTask(WolfBlockSpread.INSTANCE, Runnable { world.setBlockData(x,y,z,blockData) })
            }
        }
    }
    fun setBlockInLineZ(world : World,z : Int,x1 : Int,x2 : Int)
    {
        val small : Int
        val big : Int
        if(x1 <= x2)
        {
            small = x1
            big = x2
        }
        else
        {
            small = x2
            big = x1
        }
        for (x in small..big)
        {
            val maxY = world.getHighestBlockYAt(x,z)
            for (y in maxY downTo minY)
            {
                Bukkit.getScheduler().runTask(WolfBlockSpread.INSTANCE, Runnable { world.setBlockData(x,y,z,blockData) })
            }
        }
    }
}
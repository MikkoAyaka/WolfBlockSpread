package org.wolflink.minecraft.bukkit.wolfblockspread.workers

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.wolflink.minecraft.bukkit.wolfblockspread.SpreadBlueprint
import org.wolflink.minecraft.bukkit.wolfblockspread.SpreadManager
import org.wolflink.minecraft.bukkit.wolfblockspread.WolfBlockSpread
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ThreadLocalRandom

class SSWorker(workerId: Int,blueprint: SpreadBlueprint,args : List<String>) : SpreadWorker(workerId,blueprint,args) {

    companion object{
        val nullBlockMats = mutableListOf(Material.AIR,Material.VOID_AIR,Material.CAVE_AIR,Material.GRASS,Material.WATER,Material.LAVA)
    }

    private val banDirection = blueprint.banDirection
    init {
        for(arg in args)
        {
            try {
                banDirection.add(BlockFace.valueOf(arg.uppercase()))
            }catch (ignore : Exception){}
        }
    }
    val random = ThreadLocalRandom.current()
    val spreadBlocks = ConcurrentLinkedQueue<Block>()



    override fun start(center: Location) {

        spreadBlocks.add(center.world?.getBlockAt(center) ?: return)
        center.block.type = blueprint.mat
        val startTime = Calendar.getInstance().timeInMillis
        val count = blueprint.distance
        val delayTime = blueprint.time.toDouble() / count
        var nowCount = 0
        GlobalScope.launch {
            while (nowCount < count && !cancelled) {
                if (paused)
                {
                    delay(1000)
                    continue
                }
                spread()
                nowCount++
                delay((delayTime * 1000).toLong())
            }
            val stopTime = Calendar.getInstance().timeInMillis
            Bukkit.getLogger().info("SSWorker 任务已完成，用时 ${(stopTime-startTime)/1000} 秒")
            SpreadManager.workerList.remove(workerId)
        }
    }
    private fun spread()
    {
        val tempList = mutableListOf<Block>()
        for (block in spreadBlocks.toList())
        {
            if(block.type != blueprint.mat)
            {
                spreadBlocks.remove(block)
                continue
            }
            //判断6个方向是否同类，是则不再扩散，删除该方块，否则开始扩散
            val northBlock = block.getRelative(BlockFace.NORTH)
            val southBlock = block.getRelative(BlockFace.SOUTH)
            val westBlock = block.getRelative(BlockFace.WEST)
            val eastBlock = block.getRelative(BlockFace.EAST)
            val upBlock = block.getRelative(BlockFace.UP)
            val downBlock = block.getRelative(BlockFace.DOWN)

            //6个方向分别是否可用
            var north = false
            if(BlockFace.NORTH !in banDirection)
            {
                north = (northBlock.type != blueprint.mat) && (northBlock.type !in nullBlockMats)
                if(north && random.nextBoolean() && random.nextBoolean())
                    Bukkit.getScheduler().runTask(WolfBlockSpread.INSTANCE, Runnable {
                        if(northBlock.getRelative(BlockFace.UP).type != blueprint.mat) tempList.add(northBlock)
                        northBlock.type = blueprint.mat })
            }
            var south = false
            if(BlockFace.SOUTH !in banDirection)
            {
                south = (southBlock.type != blueprint.mat) && (southBlock.type !in nullBlockMats)
                if(south && random.nextBoolean() && random.nextBoolean())
                    Bukkit.getScheduler().runTask(WolfBlockSpread.INSTANCE, Runnable {
                        if(southBlock.getRelative(BlockFace.UP).type != blueprint.mat) tempList.add(southBlock)
                        southBlock.type = blueprint.mat })
            }
            var west = false
            if(BlockFace.WEST !in banDirection)
            {
                west = (westBlock.type != blueprint.mat) && (westBlock.type !in nullBlockMats)
                if(west && random.nextBoolean() && random.nextBoolean())
                    Bukkit.getScheduler().runTask(WolfBlockSpread.INSTANCE, Runnable {
                        if(westBlock.getRelative(BlockFace.UP).type != blueprint.mat) tempList.add(westBlock)
                        westBlock.type = blueprint.mat })
            }
            var east = false
            if(BlockFace.EAST !in banDirection)
            {
                east = (eastBlock.type != blueprint.mat) && (eastBlock.type !in nullBlockMats)
                if(east && random.nextBoolean() && random.nextBoolean())
                    Bukkit.getScheduler().runTask(WolfBlockSpread.INSTANCE, Runnable {
                        if(eastBlock.getRelative(BlockFace.UP).type != blueprint.mat) tempList.add(eastBlock)
                        eastBlock.type = blueprint.mat })
            }
            var up = false
            if(BlockFace.UP !in banDirection)
            {
                up = (upBlock.type != blueprint.mat) && (upBlock.type !in nullBlockMats)
                if(up && random.nextBoolean() && random.nextBoolean())
                    Bukkit.getScheduler().runTask(WolfBlockSpread.INSTANCE, Runnable {
                        tempList.add(upBlock)
                        upBlock.type = blueprint.mat })
            }
            var down = false
            if(BlockFace.DOWN !in banDirection)
            {
                down = (downBlock.type != blueprint.mat) && (downBlock.type !in nullBlockMats)
                if(down && random.nextBoolean() && random.nextBoolean())
                    Bukkit.getScheduler().runTask(WolfBlockSpread.INSTANCE, Runnable {
                        tempList.add(downBlock)
                        downBlock.type = blueprint.mat })
            }
            //都不可用，无法繁殖，死亡
            if(!north && !south && !west && !east && !up && !down)
            {
                spreadBlocks.remove(block)
            }
        }
        Bukkit.getScheduler().runTask(WolfBlockSpread.INSTANCE, Runnable { spreadBlocks.addAll(tempList) })
    }
}
package org.wolflink.minecraft.bukkit.wolfblockspread

import org.bukkit.Location
import org.bukkit.Material
import org.wolflink.minecraft.bukkit.wolfblockspread.workers.*

object SpreadManager {

    val blueprintList = mutableListOf<SpreadBlueprint>()
    val workerList = mutableListOf<SpreadWorker>()

    fun newBlueprint(mat : Material, type : SpreadType, distance : Int, time : Int) : Int
    {
        val blueprint = SpreadBlueprint(mat,type,distance,time)
        blueprintList.add(blueprint)
        return blueprintList.size-1
    }

    fun newWorker(blueprint: SpreadBlueprint,center: Location,args : List<String>) : Int
    {
        val worker : SpreadWorker
        when(blueprint.spreadType)
        {
            SpreadType.DOWN_TO_UP_BY_LAYER -> {
                worker = DTUBLWorker(blueprint,args)
            }
            SpreadType.UP_TO_DOWN_BY_LAYER -> {
                worker = UTDBLWorker(blueprint,args)
            }
            SpreadType.SINGLE_SPREAD -> {
                worker = SSWorker(blueprint,args)
            }
            SpreadType.RADIUS_CENTER_TO_EDGE_SPREAD -> {
                worker = RCTESWorker(blueprint,args)
            }
            SpreadType.RADIUS_EDGE_TO_CENTER_SPREAD -> {
                worker = RETCSWorker(blueprint,args)
            }
            SpreadType.SMART_SINGLE_SPREAD -> {
                worker = SmartSSWorker(blueprint,args)
            }
        }
        workerList.add(worker)
        worker.start(center)
        return workerList.size-1
    }

    fun newWorker(id : Int,center : Location,args : List<String>) : Int
    {
        val blueprint = blueprintList.getOrNull(id) ?: return -1
        return newWorker(blueprint,center,args)
    }
}
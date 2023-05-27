package org.wolflink.minecraft.bukkit.wolfblockspread

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.wolflink.minecraft.bukkit.wolfblockspread.temp.PotionListener
import org.wolflink.minecraft.bukkit.wolfblockspread.workers.SSWorker
import org.wolflink.minecraft.bukkit.wolfblockspread.workers.SmartSSWorker
import java.util.*

object CommandManager : CommandExecutor,TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        if(!sender.isOp)return false
        val arg1 = args.getOrNull(0) ?: return false

        when(arg1)
        {
            "create" -> {
                if(sender is Player)
                {
                    val type = SpreadType.valueOf(args.getOrNull(1)?:return false)
                    val distance = args.getOrNull(2)?.toIntOrNull() ?: return false
                    val time = args.getOrNull(3)?.toIntOrNull() ?: return false
                    val id =SpreadManager.newBlueprint(sender.inventory.itemInMainHand.type,type,distance,time)
                    sender.sendMessage("§7[ §bWolfBlockSpread §7] §a新的蓝图已创建 §fID $id")
                    return true
                }else return false
            }
            "banface" -> {
                val id = args.getOrNull(1)?.toIntOrNull() ?: return false
                val faceStr = args.getOrNull(2) ?: return false
                try {
                    val face = BlockFace.valueOf(faceStr.uppercase())
                    SpreadManager.blueprintList[id].banDirection.add(face)
                }catch (e : Exception){
                    sender.sendMessage("参数错误！")
                    return false
                }
                sender.sendMessage("禁用面添加成功。")
                return true
            }

            "start" -> {
                val id = args.getOrNull(1)?.toIntOrNull() ?: return false
                if(sender !is Player)return false
                val arguments = args.toCollection(mutableListOf())
                SpreadManager.newWorker(id,sender.location.add(0.0,-1.0,0.0),arguments)
                sender.sendMessage("§7[ §bWolfBlockSpread §7] §a新的任务已开始 §fID $id")
                return true
            }
            "addai" -> {
                val id = args.getOrNull(1)?.toIntOrNull() ?: return false
                if(sender !is Player)return false
                AICombatManager.addSiteLoc(sender.location.add(0.0,-1.0,0.0), SpreadManager.blueprintList.getOrNull(id)?:return false)
                sender.sendMessage("§7[ §bWolfBlockSpread §7] §a添加AI出生点成功")
                return true
            }
            "startai" -> {
                AICombatManager.start(args.drop(2).toCollection(mutableListOf()))
                sender.sendMessage("§7[ §bWolfBlockSpread §7] §aAI对抗已开始")
                return true
            }
            "stop" -> {
                val id = args.getOrNull(1)?.toIntOrNull() ?: return false
                if(sender !is Player)return false
                SpreadManager.workerList[id].stop()
                sender.sendMessage("§7[ §bWolfBlockSpread §7] §c任务已终止 §fID $id")
                return true
            }
            "stopai" -> {
                AICombatManager.stop()
                sender.sendMessage("§7[ §bWolfBlockSpread §7] §aAI对抗已终止")
                return true
            }
            "pause" -> {
                val id = args.getOrNull(1)?.toIntOrNull() ?: return false
                if(sender !is Player)return false
                SpreadManager.workerList[id].pauseTask()
                sender.sendMessage("§7[ §bWolfBlockSpread §7] §e任务已暂停 §fID $id")
                return true
            }
            "pauseai" -> {
                AICombatManager.pauseTask()
                sender.sendMessage("§7[ §bWolfBlockSpread §7] §aAI对抗已暂停")
                return true
            }
            "continue" -> {
                val id = args.getOrNull(1)?.toIntOrNull() ?: return false
                if(sender !is Player)return false
                SpreadManager.workerList[id].continueTask()
                sender.sendMessage("§7[ §bWolfBlockSpread §7] §a任务已继续 §fID $id")
                return true
            }
            "continueai" -> {
                AICombatManager.continueTask()
                sender.sendMessage("§7[ §bWolfBlockSpread §7] §aAI对抗已继续")
                return true
            }
            "list" -> {
                sender.sendMessage("§7[ §a蓝图 §7]")
                var index = 0
                for (blueprint in SpreadManager.blueprintList)
                {
                    val banDirStrList = blueprint.banDirection.joinToString("|") { it.name }
                    sender.sendMessage("${index++} ${blueprint.spreadType.name} ${blueprint.mat.name} ${blueprint.distance}格 ${blueprint.time}秒 $banDirStrList")
                }
                sender.sendMessage("§7[ §a任务 §7]")
                index = 0
                for (worker in SpreadManager.workerList)
                {
                    sender.sendMessage("${index++} ${worker::class.simpleName} P-${worker.paused} C-${worker.cancelled}")
                }
                return true
            }
            "aiinfo" -> {
                sender.sendMessage("§7[ §aAI对抗局势 §7]")
                var index = 0
                for (worker in AICombatManager.workerList)
                {
                    if(worker is SmartSSWorker)
                    sender.sendMessage("§7${index++} §f${worker.aiData.count}§7个 §f${worker.aiData.survivalPoints.toInt()}§e资源点")
                }
                return true
            }
            "potionblock" -> {
                if(sender !is Player)return false
                PotionListener.potionBlockMat = sender.inventory.itemInMainHand.type
                sender.sendMessage("设置成功")
                return true
            }
            "chatclear" -> {
                for (p in Bukkit.getOnlinePlayers())
                {
                    for (i in 1..30) p.sendMessage(" ")
                }
                return true
            }
            "addwhitelistblock" -> {
                if(sender !is Player)return false
                SSWorker.nullBlockMats.add(sender.inventory.itemInMainHand.type)
            }
            "autoai" -> {
                if(sender !is Player)return false
                val world = sender.world
                GlobalScope.launch {

                    val blockData = Material.WHITE_WOOL.createBlockData()
                    for (x in -50..50)
                        for(z in -50..50)
                            Bukkit.getScheduler().runTask(WolfBlockSpread.INSTANCE, Runnable { world.setBlockData(x,150,z,blockData) })
                    delay(500)
                    Bukkit.getScheduler().runTask(WolfBlockSpread.INSTANCE, Runnable {
                        AICombatManager.addSiteLoc(Location(world,-50.0,150.0,-50.0), SpreadBlueprint(Material.RED_WOOL,SpreadType.SMART_SINGLE_SPREAD,3000,500))
                        AICombatManager.addSiteLoc(Location(world,-50.0,150.0,50.0), SpreadBlueprint(Material.GREEN_WOOL,SpreadType.SMART_SINGLE_SPREAD,3000,500))
                        AICombatManager.addSiteLoc(Location(world,50.0,150.0,-50.0), SpreadBlueprint(Material.YELLOW_WOOL,SpreadType.SMART_SINGLE_SPREAD,3000,500))
                        AICombatManager.addSiteLoc(Location(world,50.0,150.0,50.0), SpreadBlueprint(Material.BLUE_WOOL,SpreadType.SMART_SINGLE_SPREAD,3000,500))
                        AICombatManager.start(args.drop(2).toCollection(mutableListOf())) })

                    sender.sendMessage("§7[ §bWolfBlockSpread §7] §aAI对抗已开始")
                }
                return true
            }
        }
        return false
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): MutableList<String> {
        return when(args.size)
        {
            1 -> {
                mutableListOf("banface","addwhitelistblock","chatclear","potionblock","create","start","addai","startai","stop","stopai","pause","pauseai","continue","continueai","list","aiinfo","autoai")
            }
            2 -> {
                SpreadType.values().map { it.name }.toCollection(mutableListOf())
            }
            else -> mutableListOf()
        }
    }
}
package org.wolflink.minecraft.bukkit.wolfblockspread.temp

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.wolflink.minecraft.bukkit.wolfblockspread.WolfBlockSpread

object PotionListener{

    var potionBlockMat : Material = Material.CRYING_OBSIDIAN

    fun init()
    {
        Bukkit.getScheduler().runTaskTimer(WolfBlockSpread.INSTANCE,
            Runnable {
                     for (p in Bukkit.getOnlinePlayers())
                     {
                         if(p.gameMode == GameMode.SURVIVAL)
                         {
                             if(p.location.add(0.0,-1.0,0.0).block.type == potionBlockMat)
                             {
                                 p.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS,60,1))
                                 p.addPotionEffect(PotionEffect(PotionEffectType.SLOW,60,1))
                                 p.addPotionEffect(PotionEffect(PotionEffectType.SLOW_DIGGING,60,9))
                                 p.addPotionEffect(PotionEffect(PotionEffectType.WITHER,30,1))
                             }
                         }
                     }
        },20,20)
    }
}
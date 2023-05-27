package org.wolflink.minecraft.bukkit.wolfblockspread

import java.util.Calendar

data class AICombatData(var count : Int = 0,val startTime : Long = Calendar.getInstance().timeInMillis,var survivalPoints : Double = 500.0)

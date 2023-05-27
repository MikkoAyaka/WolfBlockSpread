package org.wolflink.minecraft.bukkit.wolfblockspread

import org.bukkit.Material
import org.bukkit.block.BlockFace

data class SpreadBlueprint(val mat : Material, val spreadType: SpreadType, val distance : Int, val time : Int,val banDirection : MutableList<BlockFace> = mutableListOf()) {
}
package org.wolflink.minecraft.bukkit.wolfblockspread;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;

public class WolfBlockSpreadAPI {


    /**
     * 创建扩散蓝图
     * @param material      扩散材质
     * @param spreadType    扩散类型
     * @param distance      扩散距离
     * @param time          扩散轮次
     * @return              蓝图ID
     */
    public static int create(Material material,SpreadType spreadType,int distance,int time) {
        return SpreadManager.INSTANCE.newBlueprint(material,spreadType,distance,time);
    }
    /**
     * 在指定坐标开始一个扩散任务
     * @param blueprintId   蓝图ID
     * @return              任务ID
     */
    public static int start(int blueprintId,Location location) {
        return SpreadManager.INSTANCE.newWorker(blueprintId,location,new ArrayList<>());
    }
}

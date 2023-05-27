package org.wolflink.minecraft.bukkit.wolfblockspread;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.wolflink.minecraft.bukkit.wolfblockspread.temp.PotionListener;

import java.util.Objects;

public final class WolfBlockSpread extends JavaPlugin {

    public static WolfBlockSpread INSTANCE;
    @Override
    public void onEnable() {
        // Plugin startup logic
        INSTANCE = this;

        Objects.requireNonNull(Bukkit.getPluginCommand("wolfblockspread")).setExecutor(CommandManager.INSTANCE);
        Objects.requireNonNull(Bukkit.getPluginCommand("wolfblockspread")).setTabCompleter(CommandManager.INSTANCE);

        PotionListener.INSTANCE.init();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

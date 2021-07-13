package me.tedwoodworth.grenades;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
public class RealisticGrenades extends JavaPlugin {
    private static RealisticGrenades instance;

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        ConfigManager.getInstance().loadConfig();

        int pluginId = 12043;
        Metrics metrics = new Metrics(this, pluginId);

        // Optional: Add custom charts
        metrics.addCustomChart(new Metrics.SimplePie("chart_id", () -> "RealisticGrenades"));
    }

    @Override
    public void onDisable() {
    }

    public static RealisticGrenades getInstance() {
        return instance;
    }


}

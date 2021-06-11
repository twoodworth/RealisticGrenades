package me.tedwoodworth.grenades;

import org.bukkit.plugin.java.JavaPlugin;

public class RealisticGrenades extends JavaPlugin {
    private static RealisticGrenades instance;

    @Override
    public void onEnable() {
      instance = this;
    }

    @Override
    public void onDisable() {
    }

    public static RealisticGrenades getInstance() {
        return instance;
    }



}

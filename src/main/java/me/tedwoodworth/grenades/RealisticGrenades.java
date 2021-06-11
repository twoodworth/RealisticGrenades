package me.tedwoodworth.grenades;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class RealisticGrenades extends JavaPlugin {
    private static RealisticGrenades instance;
    private EventListener listener;

    @Override
    public void onEnable() {
        instance = this;
        listener = new EventListener();
        Bukkit.getPluginManager().registerEvents(listener, this);
        Recipes.getInstance().loadRecipes();
        loadThrownGrenades();
    }

    @Override
    public void onDisable() {
        saveThrownGrenades();
    }

    private void loadThrownGrenades() {
        var thrownGrenadesFile = new File(RealisticGrenades.getInstance().getDataFolder(), "thrown.yml");
        var config = YamlConfiguration.loadConfiguration(thrownGrenadesFile);
        var list = config.getStringList("g");

        var thrownSet = listener.grenadeSet;
        for (var strUUID : list) {
            var uuid = UUID.fromString(strUUID);
            var grenade = Bukkit.getEntity(uuid);
            if (!(grenade instanceof Item)) continue;
            thrownSet.add((Item) grenade);
            listener.grenadeTick((Item) grenade);
        }
        config.set("g", null);
    }

    private void saveThrownGrenades() {
        var thrownGrenadesFile = new File(RealisticGrenades.getInstance().getDataFolder(), "thrown.yml");
        var config = YamlConfiguration.loadConfiguration(thrownGrenadesFile);

        var list = new ArrayList<>(listener.grenadeSet);
        config.set("g", list);

        try {
            config.save(thrownGrenadesFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static RealisticGrenades getInstance() {
        return instance;
    }


}

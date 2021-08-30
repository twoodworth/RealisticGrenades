package me.tedwoodworth.grenades;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RealisticGrenades extends JavaPlugin {
    private static RealisticGrenades instance;
    public static UpdateChecker checker;

    @Override
    public void onEnable() {
        instance = this;
        checker = new UpdateChecker(this);
        // register events
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);

        // load config
        ConfigManager.getInstance().loadConfig();

        // load gui
        GrenadeGui.getGrenadeGui();

        // register commands
        Commands.register(Objects.requireNonNull(getCommand("realisticGrenades")));
        Commands.register(Objects.requireNonNull(getCommand("grenades")));

        // check for updates
        updateCheck();


        // bStats
        int pluginId = 12043;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new Metrics.SimplePie("chart_id", () -> "RealisticGrenades"));
    }

    @Override
    public void onDisable() {
    }

    public static RealisticGrenades getInstance() {
        return instance;
    }

    private void updateCheck() {
        var result = checker.getUpdateCheckResult();
        switch (result) {
            case OUT_DATED -> {
                System.out.println("[RealisticGrenades] New update is available.");
                System.out.println("[RealisticGrenades] Click Here to upgrade to version v" + checker.getLatestVersionString() + " ----------> " + checker.getResourceURL());
            }
            case UP_TO_DATE -> System.out.println("[RealisticGrenades] Version is up to date.");
            case UNRELEASED -> System.out.println("[RealisticGrenades] Version is unreleased. Thanks for testing this upcoming release!");
        }
    }

    public void updateCheck(Player player) {
        if (player.isOp() || player.hasPermission("realisticgrenades.admin.*")) {
            var result = checker.getUpdateCheckResult();
            if (result == UpdateChecker.UpdateCheckResult.OUT_DATED) {
                var message = new ComponentBuilder()
                        .append("[RealisticGrenades] New update is available.\n")
                        .color(ChatColor.GREEN)
                        .append("[RealisticGrenades] Click here to update to " + checker.getLatestVersionString())
                        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, checker.getResourceURL()))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(new ComponentBuilder().append(checker.getResourceURL()).color(ChatColor.GREEN).create())))
                        .create();

                player.spigot().sendMessage(message);
            }
        }
    }


}

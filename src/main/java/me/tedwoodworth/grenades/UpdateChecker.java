package me.tedwoodworth.grenades;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Checks if the plugin is outdated.
 */
public class UpdateChecker {

    private int resourceId;
    private URL resourceURL;
    private String currentVersionString;
    private String latestVersionString;
    private UpdateCheckResult updateCheckResult;

    UpdateChecker(RealisticGrenades plugin) {
        try {
            this.resourceId = 94240;
            this.resourceURL = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId);
        } catch (Exception exception) {
            return;
        }

        currentVersionString = plugin.getDescription().getVersion();
        latestVersionString = getLatestVersion();

        if (latestVersionString == null) {
            updateCheckResult = UpdateCheckResult.NO_RESULT;
            return;
        }


        var currentVersionSplit = currentVersionString.replace("v", "").split("\\.");
        var latestVersionSplit = getLatestVersion().replace("v", "").split("\\.");

        updateCheckResult = UpdateCheckResult.UP_TO_DATE;
        for (int i = 0; i < Math.max(currentVersionSplit.length, latestVersionSplit.length); i++) {
            try {
                var current = Integer.parseInt(currentVersionSplit[i]);
                var latest = Integer.parseInt(latestVersionSplit[i]);
                if (current < latest) {
                    updateCheckResult = UpdateCheckResult.OUT_DATED;
                } else if (current > latest) updateCheckResult = UpdateCheckResult.UNRELEASED;
            } catch (NumberFormatException ignored) {
            }
        }
    }

    public enum UpdateCheckResult {
        NO_RESULT, OUT_DATED, UP_TO_DATE, UNRELEASED,
    }

    public int getResourceId() {
        return resourceId;
    }

    public String getResourceURL() {
        return "https://www.spigotmc.org/resources/" + resourceId;
    }

    public String getCurrentVersionString() {
        return currentVersionString;
    }

    public String getLatestVersionString() {
        return latestVersionString;
    }

    public UpdateCheckResult getUpdateCheckResult() {
        return updateCheckResult;
    }

    public String getLatestVersion() {
        try {
            var urlConnection = resourceURL.openConnection();
            return new BufferedReader(new InputStreamReader(urlConnection.getInputStream())).readLine();
        } catch (Exception exception) {
            return null;
        }
    }
}
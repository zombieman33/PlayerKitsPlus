package me.zombieman.playerkitsplus.utils;

import me.zombieman.playerkitsplus.PlayerKitsPlus;
import me.zombieman.playerkitsplus.manager.PlayerDataManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class TimerUtils {

    public static boolean hasCooldown(PlayerKitsPlus plugin, Player player, String kitName) {
        FileConfiguration data = PlayerDataManager.getPlayerDataConfig(plugin, player);

        return data.contains("cooldowns." + kitName);
    }

    public static long getCooldown(PlayerKitsPlus plugin, Player player, String kitName) {
        FileConfiguration data = PlayerDataManager.getPlayerDataConfig(plugin, player);

        if (data.contains("cooldowns." + kitName)) {
            return data.getLong("cooldowns." + kitName);
        }
        return 0;
    }

    public static void setCooldown(PlayerKitsPlus plugin, Player player, String kitName, int cooldownInSeconds) {
        FileConfiguration data = PlayerDataManager.getPlayerDataConfig(plugin, player);

        long currentTime = System.currentTimeMillis();
        long cooldownTime = currentTime + (cooldownInSeconds * 1000);
        data.set("cooldowns." + kitName, cooldownTime);

        PlayerDataManager.savePlayerData(plugin, player);
    }

    public static void removeCooldown(PlayerKitsPlus plugin, Player player, String kitName) {
        FileConfiguration data = PlayerDataManager.getPlayerDataConfig(plugin, player);

        if (data.contains("cooldowns." + kitName)) {
            data.set("cooldowns." + kitName, null);
            PlayerDataManager.savePlayerData(plugin, player);
        }
    }

    public static void changeTimer(String kitName, int oldTimer, int timer, PlayerKitsPlus plugin) {
        plugin.getKitConfig().set("kit." + kitName + ".oldTimer", oldTimer);
        plugin.getKitConfig().set("kit." + kitName + ".cooldown", timer);
        plugin.saveKitConfig();
    }

    public static int getKitCooldownInSeconds(FileConfiguration kitConfig, String kitName) {
        return kitConfig.getInt("kit." + kitName + ".cooldown", 0);
    }

    public static String formatRemainingTime(long millis) {
        String time = "";

        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        long weeks = days / 7;

        if (weeks > 0) {
            time += String.format("%d weeks ", weeks);
            days %= 7;
        }

        if (days > 0) {
            time += String.format("%d days ", days);
            hours %= 24;
        }

        if (hours > 0) {
            time += String.format("%d hours ", hours);
            minutes %= 60;
        }

        if (minutes > 0) {
            time += String.format("%d minutes ", minutes);
            seconds %= 60;
        }

        if (seconds > 0 || time.isEmpty()) {
            time += String.format("%d seconds", seconds);
        }

        return time.trim();
    }

}

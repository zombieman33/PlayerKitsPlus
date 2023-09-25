package me.zombieman.playerkitsplus.utils;

import me.zombieman.playerkitsplus.PlayerKitsPlus;
import me.zombieman.playerkitsplus.manager.PlayerData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimerUtils {
//    public static Map<UUID, Map<String, Long>> kitCooldowns = new HashMap<>();

    public static boolean hasCooldown(Player player, String kitName) {
        FileConfiguration data = PlayerData.getPlayerDataConfig(player);

        if (data.contains("cooldowns." + kitName)) {
            return true;
        }
        return false;
    }

    public static long getCooldown(Player player, String kitName) {
        FileConfiguration data = PlayerData.getPlayerDataConfig(player);

        if (data.contains("cooldowns." + kitName)) {
            return data.getLong("cooldowns." + kitName);
        }
        return 0;
    }

    public static void setCooldown(Player player, String kitName, int cooldownInSeconds) {
        FileConfiguration data = PlayerData.getPlayerDataConfig(player);

        long currentTime = System.currentTimeMillis();
        long cooldownTime = currentTime + (cooldownInSeconds * 1000);
        data.set("cooldowns." + kitName, cooldownTime);

        PlayerData.savePlayerData(player, data);
    }
    public static void removeCooldown(Player player, String kitName) {
        UUID playerUUID = player.getUniqueId();
        FileConfiguration data = PlayerData.getPlayerDataConfig(player);

        if (data.contains("cooldowns." + kitName)) {
            data.set("cooldowns." + kitName, null);
            PlayerData.savePlayerData(player, data);
        }
    }

//    public static boolean hasCooldown(UUID playerUUID, String kitName) {
//        if (kitCooldowns.containsKey(playerUUID)) {
//            Map<String, Long> cooldowns = kitCooldowns.get(playerUUID);
//            return cooldowns.containsKey(kitName);
//        }
//        return false;
//    }
//
//    public static long getCooldown(UUID playerUUID, String kitName) {
//        if (kitCooldowns.containsKey(playerUUID)) {
//            Map<String, Long> cooldowns = kitCooldowns.get(playerUUID);
//            if (cooldowns.containsKey(kitName)) {
//                return cooldowns.get(kitName);
//            }
//        }
//        return 0;
//    }

//    public static void setCooldown(UUID playerUUID, String kitName, int cooldownInSeconds) {
//        long currentTime = System.currentTimeMillis();
//        long cooldownTime = currentTime + (cooldownInSeconds * 1000);
//
//        kitCooldowns.computeIfAbsent(playerUUID, k -> new HashMap<>()).put(kitName, cooldownTime);
//    }




//    public static void removeCooldown(UUID playerUUID, String kitName) {
//        if (!kitCooldowns.containsKey(playerUUID)) return;
//
//        kitCooldowns.remove(playerUUID, kitName);
//    }


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

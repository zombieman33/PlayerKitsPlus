package me.zombieman.playerkitsplus.utils;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimerUtils {

    public static Map<UUID, Map<String, Long>> kitCooldowns = new HashMap<>();

    public static boolean hasCooldown(UUID playerUUID, String kitName) {
        if (kitCooldowns.containsKey(playerUUID)) {
            Map<String, Long> cooldowns = kitCooldowns.get(playerUUID);
            return cooldowns.containsKey(kitName);
        }
        return false;
    }

    public static long getCooldown(UUID playerUUID, String kitName) {
        if (kitCooldowns.containsKey(playerUUID)) {
            Map<String, Long> cooldowns = kitCooldowns.get(playerUUID);
            if (cooldowns.containsKey(kitName)) {
                return cooldowns.get(kitName);
            }
        }
        return 0;
    }

    public static void setCooldown(UUID playerUUID, String kitName, int cooldownInSeconds) {
        long currentTime = System.currentTimeMillis();
        long cooldownTime = currentTime + (cooldownInSeconds * 1000);

        kitCooldowns.computeIfAbsent(playerUUID, k -> new HashMap<>()).put(kitName, cooldownTime);
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
        long years = days / 365;

        if (years > 0) {
            time = "%d years ".formatted(years);
            days %= 365;
        }

        if (days > 0) {
            time += "%d days ".formatted(days);
            hours %= 24;
        }

        if (hours > 0) {
            time += "%d hours ".formatted(hours);
            minutes %= 60;
        }

        if (minutes > 0) {
            time += "%d minutes ".formatted(minutes);
            seconds %= 60;
        }

        if (seconds > 0 || time.isEmpty()) {
            time += "%d seconds".formatted(seconds);
        }

        return time.trim();
    }

}

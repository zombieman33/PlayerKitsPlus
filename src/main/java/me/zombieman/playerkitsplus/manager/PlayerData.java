package me.zombieman.playerkitsplus.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerData {

    public static FileConfiguration getPlayerDataConfig(Player player) {
        UUID playerUUID = player.getUniqueId();
        File playerFile = new File("plugins/PlayerKitsPlus/playerData/", playerUUID + ".yml");

        if (!playerFile.exists()) {
            createFile(player);
        }

        return YamlConfiguration.loadConfiguration(playerFile);
    }

    public static void createFile(Player player) {
        UUID playerUUID = player.getUniqueId();
        File playerFile = new File("plugins/PlayerKitsPlus/playerData/", playerUUID + ".yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(playerFile);

        if (!playerFile.exists()) {
            try {
                data.save(playerFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void savePlayerData(Player player, FileConfiguration data) {
        UUID playerUUID = player.getUniqueId();
        File playerFile = new File("plugins/PlayerKitsPlus/playerData/", playerUUID + ".yml");

        try {
            data.save(playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

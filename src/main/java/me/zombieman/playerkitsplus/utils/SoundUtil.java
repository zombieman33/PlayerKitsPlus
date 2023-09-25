package me.zombieman.playerkitsplus.utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundUtil {
    public static void sound(Player player, Sound sound) {
        player.playSound(player.getLocation(), sound, 1, 1);
    }
}

package me.zombieman.playerkitsplus.manager;

import me.zombieman.playerkitsplus.PlayerKitsPlus;
import me.zombieman.playerkitsplus.utils.TimerUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

public class KitManager {
    public static boolean checkKit(String kit, PlayerKitsPlus plugin) {
        List<String> kits = plugin.getKitConfig().getStringList("kits");
        if (kits.contains(kit)) return true;
        return false;
    }

    public static void savePlayerInventory(Player player, String kitName, int cooldown, PlayerKitsPlus plugin) {
        ItemStack[] inventoryContents = player.getInventory().getContents();
        List<ItemStack> validItems = new ArrayList<>();

        for (ItemStack item : inventoryContents) {
            if (item != null && !item.getType().isAir()) {
                validItems.add(item);
            }
        }

        List<String> kits = plugin.getKitConfig().getStringList("kits");
        kits.add(kitName);
        plugin.getKitConfig().set("kits", kits);

        plugin.getKitConfig().set("kit." + kitName + ".items", validItems);
        plugin.getKitConfig().set("kit." + kitName + ".cooldown", cooldown);
        plugin.saveKitConfig();
    }

    public static void removeKit(String kitName, PlayerKitsPlus plugin) {
        FileConfiguration kitConfig = plugin.getKitConfig();

        List<String> kits = kitConfig.getStringList("kits");
        kits.remove(kitName);
        kitConfig.set("kits", kits);

        kitConfig.set("kit." + kitName, null);
    }


    public static void givePlayerKit(Player player, String kitName, boolean cooldown, PlayerKitsPlus plugin) {
        FileConfiguration kitConfig = plugin.getKitConfig();
        if (!kitConfig.contains("kit." + kitName + ".items")) return;

        UUID playerUUID = player.getUniqueId();

        if (cooldown) {
            if (TimerUtils.hasCooldown(playerUUID, kitName)) {
                long cooldownTime = TimerUtils.getCooldown(playerUUID, kitName);
                long currentTime = System.currentTimeMillis();

                if (currentTime < cooldownTime) {
                    long remainingTimeMillis = cooldownTime - currentTime;
                    String remainingTimeFormatted = TimerUtils.formatRemainingTime(remainingTimeMillis);
                    player.sendMessage(ChatColor.RED + "You must wait " + remainingTimeFormatted + " before using this kit again.");
                    return;
                }
            }
        }

        List<?> itemList = kitConfig.getList("kit." + kitName + ".items");
        if (itemList != null) {
            ItemStack[] items = itemList.toArray(new ItemStack[0]);
            Inventory inventory = player.getInventory();

            for (ItemStack item : items) {
                if (item != null && !item.getType().isAir()) {
                    if (ArmorManager.isArmor(item)) {
                        ArmorManager.equipArmor(player, item);
                    } else {
                        inventory.addItem(item);
                    }
                }
            }

            player.sendActionBar(ChatColor.GREEN + "Equipped Kit '" + kitName + "'");
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1, 1);

            TimerUtils.setCooldown(playerUUID, kitName, TimerUtils.getKitCooldownInSeconds(kitConfig, kitName));
        } else {
            player.sendMessage(ChatColor.RED + "There aren't any items in this kit.");
        }
    }
}

package me.zombieman.playerkitsplus.manager;

import me.zombieman.playerkitsplus.PlayerKitsPlus;
import me.zombieman.playerkitsplus.utils.ItemUtil;
import me.zombieman.playerkitsplus.utils.SoundUtil;
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

        List<ItemStack> offhandItems = new ArrayList<>();
        ItemStack offhandItem = player.getInventory().getItemInOffHand();
        if (!offhandItem.getType().isAir()) {
            offhandItems.add(offhandItem);
        }


        List<String> kits = plugin.getKitConfig().getStringList("kits");
        kits.add(kitName);
        plugin.getKitConfig().set("kits", kits);

        plugin.getKitConfig().set("kit." + kitName + ".items", validItems);
        plugin.getKitConfig().set("kit." + kitName + ".offhand", offhandItems);
        plugin.getKitConfig().set("kit." + kitName + ".cooldown", cooldown);
        plugin.saveKitConfig();
    }

    public static void removeKit(String kitName, PlayerKitsPlus plugin) {
        FileConfiguration kitConfig = plugin.getKitConfig();

        List<String> kits = kitConfig.getStringList("kits");
        kits.remove(kitName);
        kitConfig.set("kits", kits);

        kitConfig.set("kit." + kitName, null);

        plugin.saveKitConfig();
    }

    public static void givePlayerKit(Player player, String kitName, boolean cooldown, PlayerKitsPlus plugin) {
        FileConfiguration kitConfig = plugin.getKitConfig();
        if (!kitConfig.contains("kit." + kitName + ".items")) return;

        if (cooldown) {
            if (TimerUtils.hasCooldown(player, kitName)) {
                long cooldownTime = TimerUtils.getCooldown(player, kitName);
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
        if (itemList.isEmpty()) {
            SoundUtil.sound(player, Sound.ENTITY_VILLAGER_NO);
            player.sendMessage(ChatColor.RED + "'%s' doesn't have any items in it.".formatted(kitName));
            return;
        }

        ItemStack[] items = itemList.toArray(new ItemStack[0]);
        Inventory inventory = player.getInventory();

        int emptySlots = 0;
        int maxSlotsToCount = items.length > 36 ? inventory.getSize() : 36;

        for (int i = 0; i < maxSlotsToCount; i++) {
            ItemStack item = inventory.getItem(i);

            if (item == null || item.getType() == Material.AIR) {
                emptySlots++;
            }
        }

        if (emptySlots < items.length) {
            player.sendMessage(ChatColor.YELLOW + "You don't have enough space in your inventory! Please empty out some items from your inventory.");
            SoundUtil.sound(player, Sound.ENTITY_VILLAGER_TRADE);
            return;
        }

        List<?> offhand = plugin.getKitConfig().getList("kit." + kitName + ".offhand");
        ItemStack itemInOffHand = player.getInventory().getItemInOffHand();

        if (offhand != null && !offhand.isEmpty()) {
            if (itemInOffHand.getType().isAir()) {
                ItemStack[] offhands = offhand.toArray(new ItemStack[0]);
                for (ItemStack item : offhands) {
                    player.getInventory().setItemInOffHand(item);
                }
            }
        }

        for (ItemStack item : items) {
            if (item != null && !item.getType().isAir()) {
                if (ArmorManager.isArmor(item)) {
                    ArmorManager.equipArmor(player, item);
                } else {
                    player.getInventory().addItem(item);
                }
            }
        }

        player.sendActionBar(ChatColor.GREEN + "Equipped Kit '" + kitName + "'");
        SoundUtil.sound(player, Sound.ENTITY_ENDER_DRAGON_FLAP);

        if (cooldown) {
            TimerUtils.setCooldown(player, kitName, TimerUtils.getKitCooldownInSeconds(kitConfig, kitName));
        }
    }
}

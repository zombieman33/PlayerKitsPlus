package me.zombieman.playerkitsplus.manager;

import com.google.common.collect.ImmutableList;
import me.zombieman.playerkitsplus.PlayerKitsPlus;
import me.zombieman.playerkitsplus.utils.ItemUtil;
import me.zombieman.playerkitsplus.utils.SoundUtil;
import me.zombieman.playerkitsplus.utils.TimerUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class KitManager {

    public static boolean checkKit(String kit, PlayerKitsPlus plugin) {
        List<String> kits = plugin.getKitConfig().getStringList("kits");
        if (kits.contains(kit)) return true;
        return false;
    }

    public static void savePlayerInventory(Player player, String kitName, int cooldown, PlayerKitsPlus plugin) {
        ImmutableList.Builder<Object> itemsListBuilder = ImmutableList.builder();

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) item = new ItemStack(Material.AIR);
            itemsListBuilder.add(item);
        }

        plugin.getKitConfig().set("kit." + kitName + ".items", itemsListBuilder.build());
        plugin.getKitConfig().set("kit." + kitName + ".cooldown", cooldown);
        plugin.saveKitConfig();
    }

    public static void removeKit(String kitName, PlayerKitsPlus plugin) {
        FileConfiguration kitConfig = plugin.getKitConfig();

        kitConfig.set("kit." + kitName, null);

        plugin.saveKitConfig();
    }

    public static void givePlayerKit(PlayerKitsPlus plugin, Player player, String kitName, boolean cooldown) {
        FileConfiguration kitConfig = plugin.getKitConfig();
        if (!kitConfig.contains("kit." + kitName + ".items")) return;

        if (cooldown) {
            if (TimerUtils.hasCooldown(plugin, player, kitName)) {
                long cooldownTime = TimerUtils.getCooldown(plugin, player, kitName);
                long currentTime = System.currentTimeMillis();

                if (currentTime < cooldownTime) {
                    long remainingTimeMillis = cooldownTime - currentTime;
                    String remainingTimeFormatted = TimerUtils.formatRemainingTime(remainingTimeMillis);
                    player.sendMessage(ChatColor.RED + "You must wait " + remainingTimeFormatted + " before using this kit again.");
                    return;
                }
            }
        }

        PlayerInventory inv = player.getInventory();
        // noinspection unchecked
        List<ItemStack> itemList = (List<ItemStack>) kitConfig.getList("kit." + kitName + ".items");
        if (itemList == null) return;

        List<ItemStack> couldNotBePlaced = new ArrayList<>();

        // Try to place the items where they should go
        for (int i = 0; i < itemList.size(); i++) {
            ItemStack currItem = inv.getItem(i);
            if (currItem == null || currItem.getType() == Material.AIR) {
                inv.setItem(i, itemList.get(i));
            } else {
                couldNotBePlaced.add(itemList.get(i));
            }
        }

        // Fallback for items that couldn't be placed in the correct slot
        for (ItemStack item : couldNotBePlaced) {
            inv.addItem(item);
        }

        player.sendActionBar(ChatColor.GREEN + "Equipped Kit '" + kitName + "'");
        SoundUtil.sound(player, Sound.ENTITY_ENDER_DRAGON_FLAP);

        if (cooldown) {
            TimerUtils.setCooldown(plugin, player, kitName, TimerUtils.getKitCooldownInSeconds(kitConfig, kitName));
        }
    }

    public static Collection<String> getKitNames(PlayerKitsPlus plugin) {
        ConfigurationSection kitSection = plugin.getKitConfig().getConfigurationSection("kit");
        if (kitSection == null) return new ArrayList<>();
        return kitSection.getKeys(false);
    }

}

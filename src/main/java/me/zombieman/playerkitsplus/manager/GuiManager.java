package me.zombieman.playerkitsplus.manager;

import me.zombieman.playerkitsplus.PlayerKitsPlus;
import me.zombieman.playerkitsplus.utils.ItemUtil;
import me.zombieman.playerkitsplus.utils.SoundUtil;
import me.zombieman.playerkitsplus.utils.TimerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class GuiManager {

    public static final int CLAIM_KIT_SLOT = 45;
    public static final int DELETE_KIT_SLOT = 50;
    public static final int OLD_TIMER_SLOT = 47;
    public static final int CHANGE_TIMER_SLOT = 48;

    public static final int DENY_DELETE = 9 + 3;
    public static final int CONFIRM_DELETE = 9 + 5;

    private static final int HELMET_SLOT = 36;
    private static final int CHESTPLATE_SLOT = 37;
    private static final int LEGGINGS_SLOT = 38;
    private static final int BOOTS_SLOT = 39;
    private static final int OFFHAND_SLOT = 40;

    private static final List<String> opened = new ArrayList<>();
    private static final List<String> deleteGuiOpened = new ArrayList<>();
    private static final Map<UUID, String> kits = new HashMap<>();

    public static void openKitGUI(Player player, String kitName, PlayerKitsPlus plugin) {
        List<?> kitItems = plugin.getKitConfig().getList("kit." + kitName + ".items");
        List<?> offhandItems = plugin.getKitConfig().getList("kit." + kitName + ".offhand");

        if (kitItems.isEmpty()) {
            player.sendMessage(ChatColor.RED + "There aren't any items in this kit!");
            SoundUtil.sound(player, Sound.ENTITY_VILLAGER_NO);
            return;
        }

        int guiSize = 6;
        Inventory kitInventory = Bukkit.createInventory(player, guiSize * 9, kitName + " Kit");

        ItemStack[] items = kitItems.toArray(new ItemStack[0]);
        for (ItemStack item : items) {
            if (item != null && !item.getType().equals(Material.AIR)) {
                kitInventory.addItem(item);
            }
        }

        for (int i = 0; i < kitInventory.getSize(); i++) {
            ItemStack item = kitInventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                kitInventory.setItem(i, ItemUtil.createItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), "Slot " + i, false));
            }
        }

        // Just to make sure that we don't have any more slots than the inventory itself.
        for (int i = 41; i < kitInventory.getSize(); i++) {
            ItemStack item = kitInventory.getItem(i);
            if (item != null && item.getType() == Material.BLACK_STAINED_GLASS_PANE) {
                kitInventory.setItem(i, ItemUtil.createItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), " ", false));
            }
        }

        ItemStack helmet = kitInventory.getItem(HELMET_SLOT);
        ItemStack chestplate = kitInventory.getItem(CHESTPLATE_SLOT);
        ItemStack leggings = kitInventory.getItem(LEGGINGS_SLOT);
        ItemStack boots = kitInventory.getItem(BOOTS_SLOT);
        ItemStack offhand = kitInventory.getItem(OFFHAND_SLOT);

        if (helmet != null && !ArmorManager.isArmor(helmet)) {
            kitInventory.setItem(HELMET_SLOT, ItemUtil.createItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), "Helmet Slot", false));
        }

        if (chestplate != null && !ArmorManager.isArmor(chestplate)) {
            kitInventory.setItem(CHESTPLATE_SLOT, ItemUtil.createItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), "Chestplate Slot", false));
        }

        if (leggings != null && !ArmorManager.isArmor(leggings)) {
            kitInventory.setItem(LEGGINGS_SLOT, ItemUtil.createItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), "Leggings Slot", false));
        }

        if (boots != null && !ArmorManager.isArmor(boots)) {
            kitInventory.setItem(BOOTS_SLOT, ItemUtil.createItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), "Boots Slot", false));
        }

        if (offhand != null) {
            if (!offhand.getType().equals(Material.GRAY_STAINED_GLASS_PANE)) {
                kitInventory.setItem(OFFHAND_SLOT, ItemUtil.createItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), "Offhand Slot", false));
            }
        }

        ItemStack[] offhands = offhandItems.toArray(new ItemStack[0]);
        for (ItemStack item : offhands) {
            if (item != null && !item.getType().equals(Material.AIR)) {
                kitInventory.setItem(OFFHAND_SLOT, item);
            }
        }



        long cooldownTime = TimerUtils.getCooldown(player, kitName);
        long currentTime = System.currentTimeMillis();
        long remainingTimeMillis = cooldownTime - currentTime;
        String remainingTimeFormatted = TimerUtils.formatRemainingTime(remainingTimeMillis);

        if (player.hasPermission("playerkitsplus.command.kit." + kitName)) {
            if (currentTime > cooldownTime) {
                kitInventory.setItem(CLAIM_KIT_SLOT, ItemUtil.createItem(new ItemStack(Material.LIME_CONCRETE), "<green>Click to claim the: <bold>" + kitName + "</bold> kit.", true, "<white>Ready to claim"));
            } else {
                kitInventory.setItem(CLAIM_KIT_SLOT, ItemUtil.createItem(new ItemStack(Material.YELLOW_CONCRETE), "<green>Click to claim the: <bold>" + kitName + "</bold> kit.", true, "<white>Cooldown: %s".formatted((remainingTimeFormatted))));
            }
        } else {
            kitInventory.setItem(CLAIM_KIT_SLOT, ItemUtil.createItem(new ItemStack(Material.BARRIER), "<red>Exit", true));
        }

        if (player.hasPermission("playerkitsplus.command.deletekit")) {
            kitInventory.setItem(DELETE_KIT_SLOT, ItemUtil.createItem(new ItemStack(Material.RED_CONCRETE), "<red>Click to delete the: <bold>" + kitName + "</bold> kit.", true, "<dark_red>WARNING: <red>You cannot undo this action!"));
        }

        if (player.hasPermission("playerkitsplus.command.changetimer")) {

            int oldCooldown =  plugin.getKitConfig().getInt("kit." + kitName + ".oldTimer");
            kitInventory.setItem(OLD_TIMER_SLOT, ItemUtil.createItem(new ItemStack(Material.OAK_SIGN), "<yellow>Old Timer: " + TimerUtils.formatRemainingTime(oldCooldown * 1000L), true));

            int cooldown = plugin.getKitConfig().getInt("kit." + kitName + ".cooldown");
            kitInventory.setItem(CHANGE_TIMER_SLOT, ItemUtil.createItem(new ItemStack(Material.DARK_OAK_SIGN), "<green>Click to change the timer for the " + kitName + " kit.", true, "<white>Current timer: " + TimerUtils.formatRemainingTime(cooldown * 1000L)));
        }



        setKit(player, kitName);

        SoundUtil.sound(player, Sound.ENTITY_ITEM_PICKUP);
        player.openInventory(kitInventory);
        savePlayerToKit(player);
    }

    public static String getKit(Player player) {

        String kit = kits.get(player.getUniqueId());

        return kit;
    }

    public static void setKit(Player player, String kit) {

        if (kits.containsKey(player.getUniqueId())) return;

        kits.put(player.getUniqueId(), kit);

    }

    public static void removeKit(Player player) {
        kits.remove(player.getUniqueId());
    }

    public static void savePlayerToKit(Player player) {
        String uuid = player.getUniqueId().toString();

        if (opened.contains(uuid)) {
            opened.remove(uuid);
            return;
        }

        opened.add(uuid);
    }

    public static void removePlayerFromKit(Player player) {
        String uuid = player.getUniqueId().toString();

        if (!opened.contains(uuid)) return;

        opened.remove(uuid);
    }

    public static boolean checkIfPlayerIsInKit(Player player) {
        String uuid = player.getUniqueId().toString();

        if (opened.contains(uuid)) return true;

        return false;
    }

    public static void removePlayerFromDeleteGui(Player player) {
        if (!deleteGuiOpened.contains(player.getUniqueId().toString())) return;

        deleteGuiOpened.remove(player.getUniqueId().toString());
    }

    public static boolean checkIfPlayerIsInDeleteGui(Player player) {
        if (deleteGuiOpened.contains(player.getUniqueId().toString())) return true;

        return false;
    }

    public static void openConfirmGui(Player player, String kit) {
        Inventory conformationInv = Bukkit.createInventory(player, 3 * 9, kit + " Delete Conformation");

        for (int i = 0; i < conformationInv.getSize(); i++) {
            conformationInv.setItem(i, ItemUtil.createItem(new ItemStack(Material.BLACK_STAINED_GLASS_PANE), " ", false));
        }

        conformationInv.setItem(DENY_DELETE, ItemUtil.createItem(new ItemStack(Material.RED_CONCRETE), "<red>Cancel", true));
        conformationInv.setItem(CONFIRM_DELETE, ItemUtil.createItem(new ItemStack(Material.GREEN_CONCRETE), "<green>Approve", true, "<white>If you press this button you", "<white>will permanently delete the %s kit.".formatted(kit)));

        deleteGuiOpened.add(player.getUniqueId().toString());

        player.openInventory(conformationInv);
    }
}

package me.zombieman.playerkitsplus.listeners;

import me.zombieman.playerkitsplus.PlayerKitsPlus;
import me.zombieman.playerkitsplus.manager.GuiManager;
import me.zombieman.playerkitsplus.manager.KitManager;
import me.zombieman.playerkitsplus.utils.SoundUtil;
import me.zombieman.playerkitsplus.utils.TimerUtils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class GuiListener implements Listener {

    private PlayerKitsPlus plugin;

    public GuiListener(PlayerKitsPlus plugin) {
        this.plugin = plugin;
    }

    private static final List<UUID> changeTimer = new ArrayList<>();
    private static final Map<UUID, String> kit = new HashMap<>();
    private static final Map<UUID, String> deleteKit = new HashMap<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getView().getPlayer();

        if (!GuiManager.checkIfPlayerIsInKit(player)) return;

        if (!event.getView().getTitle().endsWith(" Kit")) return;

        event.setCancelled(true);

        int slot = event.getSlot();
        if (slot == GuiManager.CLAIM_KIT_SLOT) {
            String kit = GuiManager.getKit(player);

            player.closeInventory();

            if (!player.hasPermission("playerkitsplus.command.kit." + kit)) return;

            KitManager.givePlayerKit(plugin, player, kit, true);

        } else if (slot == GuiManager.DELETE_KIT_SLOT) {
            if (!player.hasPermission("playerkitsplus.command.deletekit")) return;

            String kit = GuiManager.getKit(player);

            player.closeInventory();

            deleteKit.put(player.getUniqueId(), kit);

            GuiManager.openConfirmGui(player, kit);

        } else if (slot == GuiManager.CHANGE_TIMER_SLOT) {
            if (!player.hasPermission("playerkitsplus.command.changetimer")) return;

            if (changeTimer.contains(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You need to type a integer in chat: 0-9");
                SoundUtil.sound(player, Sound.ENTITY_VILLAGER_NO);
                player.closeInventory();
                return;
            }

            changeTimer.add(player.getUniqueId());

            player.sendMessage(ChatColor.GREEN + "Please type a integer in chat: 0-9");
            SoundUtil.sound(player, Sound.ENTITY_VILLAGER_TRADE);

            kit.put(player.getUniqueId(), GuiManager.getKit(player));
            player.closeInventory();

        } else if (slot == GuiManager.OLD_TIMER_SLOT) {
            if (!player.hasPermission("playerkitsplus.command.changetimer")) return;

            player.sendMessage(ChatColor.GREEN + "Old Timer: " + TimerUtils.formatRemainingTime(plugin.getKitConfig().getInt("kit." + GuiManager.getKit(player) + ".oldTimer") * 1000L));
            player.closeInventory();
        }
    }

    @EventHandler
    public void onInventoryClickDeleteConfirmGui(InventoryClickEvent event) {
        Player player = (Player) event.getView().getPlayer();

        if (!GuiManager.checkIfPlayerIsInDeleteGui(player)) return;

        event.setCancelled(true);

        int slot = event.getSlot();
        if (slot == GuiManager.DENY_DELETE) {
            player.closeInventory();
            GuiManager.removePlayerFromDeleteGui(player);
            deleteKit.remove(player.getUniqueId());
            SoundUtil.sound(player, Sound.ENTITY_VILLAGER_TRADE);
            player.sendActionBar(ChatColor.GREEN + "You cancelled this action."); // L
        } else if (slot == GuiManager.CONFIRM_DELETE) {
            String kit = deleteKit.get(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + String.format("You successfully deleted %s kit!", kit));
            player.sendActionBar(ChatColor.GREEN + String.format("You successfully deleted %s kit!", kit));
            SoundUtil.sound(player, Sound.ENTITY_VILLAGER_HURT);
            KitManager.removeKit(kit, plugin);
            player.closeInventory();
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getView().getPlayer();

        if (GuiManager.checkIfPlayerIsInKit(player)) {
            GuiManager.removePlayerFromKit(player);
            GuiManager.removeKit(player);
        }
        else if (GuiManager.checkIfPlayerIsInDeleteGui(player)) {
            GuiManager.removePlayerFromDeleteGui(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (GuiManager.checkIfPlayerIsInKit(player)) {
            GuiManager.removePlayerFromKit(player);
            GuiManager.removeKit(player);
        }
        else if (GuiManager.checkIfPlayerIsInDeleteGui(player)) {
            GuiManager.removePlayerFromDeleteGui(player);
        }

        if (kit.containsKey(player.getUniqueId())) {
            kit.remove(player.getUniqueId());
        }

        if (changeTimer.contains(player.getUniqueId())) {
            changeTimer.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        Player player = event.getPlayer();

        if (!changeTimer.contains(player.getUniqueId())) return;

        event.setCancelled(true);

        try {
            int intValue = Integer.parseInt(message);

            String kitName = kit.get(player.getUniqueId());

            int oldTimer = plugin.getKitConfig().getInt("kit." + kitName + ".cooldown");

            TimerUtils.changeTimer(kitName, oldTimer, intValue, plugin);

            player.sendMessage(ChatColor.AQUA + String.format("SUCCESSFULLY CHANGED THE TIMER OF THE %s KIT", kitName));
            player.sendMessage(ChatColor.GREEN + String.format("New timer: %s", TimerUtils.formatRemainingTime(intValue * 1000L)));
            player.sendMessage(ChatColor.YELLOW + String.format("Old timer: %s", TimerUtils.formatRemainingTime(oldTimer * 1000L)));

        } catch (NumberFormatException e) {

            player.sendMessage(ChatColor.RED + String.format("Error: This '%s' is not a integer, please try to change the timer again.", message));

        }

        changeTimer.remove(player.getUniqueId());
        kit.remove(player.getUniqueId());
    }
}

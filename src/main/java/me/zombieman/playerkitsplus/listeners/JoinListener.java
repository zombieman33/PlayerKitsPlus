package me.zombieman.playerkitsplus.listeners;

import me.zombieman.playerkitsplus.PlayerKitsPlus;
import me.zombieman.playerkitsplus.manager.KitManager;
import me.zombieman.playerkitsplus.manager.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinListener implements Listener {

    private PlayerKitsPlus plugin;

    public JoinListener(PlayerKitsPlus plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        // Preload the player data file async!
        PlayerDataManager.getPlayerDataConfig(plugin, event.getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Ensure we have a player data file
        PlayerDataManager.getPlayerDataConfig(plugin, player.getUniqueId());

        if (player.hasPlayedBefore()) return;
        if (!plugin.getConfig().getBoolean("shouldSpawnWithKit")) return;

        String starterKit = plugin.getConfig().getString("starterKit");

        if (!plugin.getKitConfig().getStringList("kits").contains(starterKit)) {
            plugin.getLogger().warning("");
            plugin.getLogger().warning(String.format("%s joined but did not get any kits because the kit '%s' doesn't exist!", player.getName(), starterKit));
            plugin.getLogger().warning("");
            return;
        }

        KitManager.givePlayerKit(plugin, player, starterKit, false);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> PlayerDataManager.cleanupCache(player));
    }

}

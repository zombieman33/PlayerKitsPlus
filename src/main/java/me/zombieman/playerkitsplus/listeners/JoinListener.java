package me.zombieman.playerkitsplus.listeners;

import me.zombieman.playerkitsplus.PlayerKitsPlus;
import me.zombieman.playerkitsplus.manager.KitManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {
    private PlayerKitsPlus plugin;
    public JoinListener(PlayerKitsPlus plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPlayedBefore()) return;
        if (!plugin.getConfig().getBoolean("shouldSpawnWithKit")) return;
        String starterKit = plugin.getConfig().getString("starterKit");
        if (!plugin.getKitConfig().getStringList("kits").contains(starterKit)) {
            plugin.getLogger().warning("                                                                ");
            plugin.getLogger().warning("%s joined but did not get any kits because the kit '%s' doesn't exist!".formatted(player.getName(), starterKit));
            plugin.getLogger().warning("                                                                ");
            return;
        }

        KitManager.givePlayerKit(player, starterKit, false, plugin);
    }
}

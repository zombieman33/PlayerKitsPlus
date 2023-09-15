package me.zombieman.playerkitsplus.commands;

import me.zombieman.playerkitsplus.PlayerKitsPlus;
import me.zombieman.playerkitsplus.manager.KitManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DeleteKitCmd implements CommandExecutor, TabCompleter {
    private final PlayerKitsPlus plugin;
    public DeleteKitCmd(PlayerKitsPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only a player can run this command.");
            return true;
        }

        if (args.length >= 1) {
            String kitName = args[0];
            if (!KitManager.checkKit(kitName, plugin)) {
                player.sendMessage(ChatColor.RED + "%s is not a valid kit.".formatted(kitName));
                return false;
            }
            KitManager.removeKit(kitName, plugin);
            plugin.saveKitConfig();
            player.sendMessage(ChatColor.GREEN + "Deleted a kit called '%s'".formatted(kitName));

        } else {
            player.sendMessage(ChatColor.YELLOW + "/deletekit <kit>");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        Player player = (Player) sender;

        if (args.length == 1) {
            if (player.hasPermission("playerkitsplus.command.deletekit")) {
                completions.addAll(plugin.getKitConfig().getStringList("kits"));
            }
        }
        return completions;
    }
}
package me.zombieman.playerkitsplus.commands;

import me.zombieman.playerkitsplus.PlayerKitsPlus;
import me.zombieman.playerkitsplus.manager.KitManager;
import me.zombieman.playerkitsplus.manager.PlayerDataManager;
import me.zombieman.playerkitsplus.utils.SoundUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ForceKitCmd implements CommandExecutor {
    private final PlayerKitsPlus plugin;
    public ForceKitCmd(PlayerKitsPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if ((sender instanceof Player)) {
            sender.sendMessage("Only console can run this command.");
            return true;
        }

        if (args.length >= 1) {
            String kitName = args[1];
            String targetName = args[0];

            if (!KitManager.checkKit(kitName, plugin)) {
                sender.sendMessage(ChatColor.RED + String.format("'%s' is not a valid kit.", kitName));
                return false;
            }

            Player target = Bukkit.getPlayer(targetName);

            if (target == null) {
                sender.sendMessage(ChatColor.RED + "This is not a valid player.");
                return false;
            }

            KitManager.givePlayerKit(plugin, target, kitName, true);

        } else {
            sender.sendMessage(ChatColor.YELLOW + "/forcekit <player> <kit>");
        }
        return true;
    }
}
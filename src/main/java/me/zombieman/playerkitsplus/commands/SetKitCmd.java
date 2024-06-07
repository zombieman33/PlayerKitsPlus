package me.zombieman.playerkitsplus.commands;

import me.zombieman.playerkitsplus.PlayerKitsPlus;
import me.zombieman.playerkitsplus.manager.KitManager;
import me.zombieman.playerkitsplus.utils.SoundUtil;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SetKitCmd implements CommandExecutor, TabCompleter {
    private final PlayerKitsPlus plugin;
    public SetKitCmd(PlayerKitsPlus plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only a player can run this command.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length >= 1) {
            String kitName = args[0];

            if (!KitManager.checkKit(kitName, plugin)) {
                player.sendMessage(ChatColor.RED + String.format("'%s' is not a valid kit.", kitName));
                return false;
            }

            KitManager.setKit(plugin, kitName, plugin.getKitConfig().getBoolean("kit." + kitName + ".isOneTimeUse"), player);

        } else {
            player.sendMessage(ChatColor.YELLOW + "/kit <kit>");
            SoundUtil.sound(player, Sound.ENTITY_VILLAGER_TRADE);
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        Player player = (Player) sender;

        if (args.length == 1) {
            if (player.hasPermission("playerkitsplus.command.kit")) {
                Collection<String> kits = KitManager.getKitNames(plugin);
                for (String kit : kits) {
                    if (player.hasPermission("playerkitsplus.command.kit." + kit)) {
                        completions.add(kit);
                    }
                }
            }
        }

        String lastArg = args[args.length - 1];
        return completions.stream().filter(s -> s.startsWith(lastArg)).collect(Collectors.toList());
    }
}
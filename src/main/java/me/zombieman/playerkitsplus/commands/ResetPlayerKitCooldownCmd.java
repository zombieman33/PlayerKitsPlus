package me.zombieman.playerkitsplus.commands;

import me.zombieman.playerkitsplus.PlayerKitsPlus;
import me.zombieman.playerkitsplus.manager.KitManager;
import me.zombieman.playerkitsplus.utils.SoundUtil;
import me.zombieman.playerkitsplus.utils.TimerUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ResetPlayerKitCooldownCmd implements CommandExecutor, TabCompleter {
    private final PlayerKitsPlus plugin;
    public ResetPlayerKitCooldownCmd(PlayerKitsPlus plugin) {
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

            if (args.length >= 2) {
                String targetName = args[1];
                Player target = Bukkit.getPlayerExact(targetName);

                if (target == null) {
                    player.sendMessage(ChatColor.RED + "This player doesn't exist.");
                    return false;
                }

                TimerUtils.removeCooldown(plugin, player, kitName);
                player.sendMessage(ChatColor.GREEN + String.format("You successfully reset %s's %s kit cooldown.", targetName, kitName));
                player.sendMessage(ChatColor.GREEN + String.format("Your %s kit got reset!", kitName));

            } else {
                player.sendMessage(ChatColor.YELLOW + "/resetkitcooldown <kit> <player>");
                SoundUtil.sound(player, Sound.ENTITY_VILLAGER_TRADE);
            }

        } else {
            player.sendMessage(ChatColor.YELLOW + "/resetkitcooldown <kit> <player>");
            SoundUtil.sound(player, Sound.ENTITY_VILLAGER_TRADE);
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        Player player = (Player) sender;

        if (player.hasPermission("playerkitsplus.command.resetcooldown")) {
            if (args.length == 1) {
                List<String> kits = plugin.getKitConfig().getStringList("kits");
                for (String kit : kits) {
                    completions.add(kit);
                }
            }
            if (args.length == 2) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    completions.add(p.getName());
                }
            }
        }
        return completions;
    }
}

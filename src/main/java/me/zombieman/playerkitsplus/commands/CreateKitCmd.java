package me.zombieman.playerkitsplus.commands;

import me.zombieman.playerkitsplus.PlayerKitsPlus;
import me.zombieman.playerkitsplus.manager.KitManager;
import me.zombieman.playerkitsplus.utils.SoundUtil;
import me.zombieman.playerkitsplus.utils.TimerUtils;
import net.md_5.bungee.api.chat.*;
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
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class CreateKitCmd implements CommandExecutor, TabCompleter {
    private final PlayerKitsPlus plugin;
    public CreateKitCmd(PlayerKitsPlus plugin) {
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
            if (KitManager.checkKit(kitName, plugin)) {
                player.sendMessage(ChatColor.RED + "%s is already a kit.".formatted(kitName));
                return false;
            }

            if (args.length != 2) {
                player.sendMessage(ChatColor.RED + "You need to specify a time for the kit.");
                return false;
            }
            String timeStr = args[1];

            int time = 0;

            try {
                time = Integer.parseInt(timeStr);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "'%s' is not a valid number.".formatted(timeStr));
                return false;
            }

            if (time < 0) {
                player.sendMessage(ChatColor.RED +  "The timer can't be below 0.");
                return false;
            }

            KitManager.savePlayerInventory(player, kitName, time, plugin);
            player.sendMessage(ChatColor.GREEN + "Created a new kit called '%s'".formatted(kitName));
            player.sendMessage(ChatColor.GREEN + "Timer %s".formatted(TimerUtils.formatRemainingTime(time * 1000L)));

        } else {
            player.sendMessage(ChatColor.YELLOW + "/createkit <kit>");
            SoundUtil.sound(player, Sound.ENTITY_VILLAGER_TRADE);
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        Player player = (Player) sender;

        if (player.hasPermission("playerkitsplus.command.createkit")) {
            if (args.length == 1) {
                completions.add("<kit name>");
            } else if (args.length == 2) {
                completions.add("<time in seconds>");
            }
        }
        return completions;
    }
}
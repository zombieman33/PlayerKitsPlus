package me.zombieman.playerkitsplus.commands;

import me.zombieman.playerkitsplus.PlayerKitsPlus;
import me.zombieman.playerkitsplus.manager.KitManager;
import me.zombieman.playerkitsplus.utils.SoundUtil;
import me.zombieman.playerkitsplus.utils.TimerUtils;
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
import java.util.stream.Collectors;

public class CreateKitCmd implements CommandExecutor, TabCompleter {
    private final PlayerKitsPlus plugin;
    public CreateKitCmd(PlayerKitsPlus plugin) {
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
            if (KitManager.checkKit(kitName, plugin)) {
                player.sendMessage(ChatColor.RED + String.format("%s is already a kit.", kitName));
                return false;
            }

            boolean isOneTimeUse = false;

            if (!args[1].isEmpty()) {
                try {
                    isOneTimeUse = Boolean.parseBoolean(args[1]);
                } catch (IllegalArgumentException e) {
                    player.sendMessage(ChatColor.RED + "This is not a valid boolean. Usage: true/false");
                    SoundUtil.sound(player, Sound.ENTITY_VILLAGER_NO);
                    return false;
                }
            } else {
                player.sendMessage(ChatColor.RED + "This is not a valid boolean. Usage: true/false");
                SoundUtil.sound(player, Sound.ENTITY_VILLAGER_NO);
                return false;
            }

            if (args.length != 3) {
                player.sendMessage(ChatColor.RED + "You need to specify a time for the kit.");
                return false;
            }


            String timeStr = args[2];

            int time = 0;

            try {
                time = Integer.parseInt(timeStr);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + String.format("'%s' is not a valid number.", timeStr));
                return false;
            }

            if (time < 0) {
                player.sendMessage(ChatColor.RED + "The timer can't be below 0.");
                SoundUtil.sound(player, Sound.ENTITY_VILLAGER_NO);
                return false;
            }


            KitManager.savePlayerInventory(player, kitName, time, isOneTimeUse, plugin);

            if (player.getInventory().isEmpty()) return false;

            player.sendMessage(ChatColor.GREEN + String.format("Created a new kit called '%s'", kitName));
            player.sendMessage(ChatColor.GREEN + String.format("Timer %s", TimerUtils.formatRemainingTime(time * 1000L)));
            player.sendMessage(ChatColor.GREEN + "Is One Time Use: " + isOneTimeUse);

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
                completions.add("<is one time use>");
            } else if (args.length == 3) {
                completions.add("<time in seconds>");
            }
        }

        String lastArg = args[args.length - 1];
        return completions.stream().filter(s -> s.startsWith(lastArg.toLowerCase())).collect(Collectors.toList());
    }
}
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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ChangeTimerCmd implements CommandExecutor, TabCompleter {
    private final PlayerKitsPlus plugin;
    public ChangeTimerCmd(PlayerKitsPlus plugin) {
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

            if (args.length != 2) {
                player.sendMessage(ChatColor.RED + "You need to specify a time for this kit.");
                return false;
            }

            String timeStr = args[1];

            int time = 0;

            try {
                time = Integer.parseInt(timeStr);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + String.format("'%s' is not a valid number.", timeStr));
                SoundUtil.sound(player, Sound.ENTITY_VILLAGER_NO);
                return false;
            }

            if (time < 0) {
                player.sendMessage(ChatColor.RED +  "The timer can't be below 0.");
                SoundUtil.sound(player, Sound.ENTITY_VILLAGER_NO);
                return false;
            }

            int oldTimer = plugin.getKitConfig().getInt("kit." + kitName + ".cooldown");

            TimerUtils.changeTimer(kitName, oldTimer, time, plugin);

            player.sendMessage(ChatColor.AQUA + String.format("SUCCESSFULLY CHANGED THE TIMER OF THE %s KIT", kitName));
            player.sendMessage(ChatColor.GREEN + String.format("New timer: %s", TimerUtils.formatRemainingTime(time * 1000L)));
            player.sendMessage(ChatColor.YELLOW + String.format("Old timer: %s", TimerUtils.formatRemainingTime(oldTimer * 1000L)));

        } else {
            player.sendMessage(ChatColor.YELLOW + "/changetimer <kit> <int>");
            SoundUtil.sound(player, Sound.ENTITY_VILLAGER_TRADE);
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        Player player = (Player) sender;

        if (player.hasPermission("playerkitsplus.command.changetimer")) {
            if (args.length == 1) {
                Collection<String> kits = KitManager.getKitNames(plugin);
                completions.addAll(kits);
            }
            if (args.length == 2) {
                completions.add("<time in seconds>");
            }
        }

        String lastArg = args[args.length - 1];
        return completions.stream().filter(s -> s.startsWith(lastArg)).collect(Collectors.toList());
    }
}
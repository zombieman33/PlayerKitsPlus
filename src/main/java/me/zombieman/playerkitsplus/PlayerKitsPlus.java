package me.zombieman.playerkitsplus;

import me.zombieman.playerkitsplus.commands.CreateKitCmd;
import me.zombieman.playerkitsplus.commands.DeleteKitCmd;
import me.zombieman.playerkitsplus.commands.KitCmd;
import me.zombieman.playerkitsplus.listeners.JoinListener;
import me.zombieman.playerkitsplus.manager.ArmorManager;
import me.zombieman.playerkitsplus.manager.KitManager;
import me.zombieman.playerkitsplus.utils.TimerUtils;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public final class PlayerKitsPlus extends JavaPlugin {
    private File kitDataFile;
    private FileConfiguration kitConfig;

    // Commands
    private CreateKitCmd createKitCmd;
    private DeleteKitCmd deleteKitCmd;
    private KitCmd kitCmd;

    // Listeners
    private JoinListener joinListener;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.saveDefaultConfig();

        String kitConfigFileName = "kit.yml";
        this.kitDataFile = new File(this.getDataFolder(), kitConfigFileName);
        if (!this.kitDataFile.exists()) this.saveResource(kitConfigFileName, false);
        this.kitConfig = YamlConfiguration.loadConfiguration(this.kitDataFile);

        // Commands
        PluginCommand plCreateKitCmd = this.getCommand("createkit");
        this.createKitCmd = new CreateKitCmd(this);
        if (plCreateKitCmd != null) plCreateKitCmd.setExecutor(this.createKitCmd);

        PluginCommand plDeleteKitCmd = this.getCommand("deletekit");
        this.deleteKitCmd = new DeleteKitCmd(this);
        if (plDeleteKitCmd != null) plDeleteKitCmd.setExecutor(this.deleteKitCmd);

        PluginCommand plKitCmd = this.getCommand("kit");
        this.kitCmd = new KitCmd(this);
        if (plKitCmd != null) plKitCmd.setExecutor(this.kitCmd);

        // Managers
        new KitManager();
        new ArmorManager();

        // Utils
        new TimerUtils();

        // Listeners
        this.joinListener = new JoinListener(this);
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(joinListener, this);
    }
    public FileConfiguration getKitConfig() {
        return kitConfig;
    }
    public CreateKitCmd getCreateKitCmd() {
        return this.createKitCmd;
    }
    public DeleteKitCmd getDeleteKitCmd() {
        return this.deleteKitCmd;
    }
    public KitCmd getKidCmd() {
        return this.kitCmd;
    }
    public void reloadKitConfig() {
        kitConfig = YamlConfiguration.loadConfiguration(kitDataFile);

        if (!kitDataFile.exists()) {
            saveResource("kit.yml", false);
        }
    }

    public void saveKitConfig() {
        try {
            kitConfig.save(kitDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

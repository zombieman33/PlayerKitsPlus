package me.zombieman.playerkitsplus;

import me.zombieman.playerkitsplus.commands.*;
import me.zombieman.playerkitsplus.listeners.GuiListener;
import me.zombieman.playerkitsplus.listeners.JoinListener;
import me.zombieman.playerkitsplus.manager.ArmorManager;
import me.zombieman.playerkitsplus.manager.GuiManager;
import me.zombieman.playerkitsplus.manager.KitManager;
import me.zombieman.playerkitsplus.manager.PlayerDataManager;
import me.zombieman.playerkitsplus.utils.ItemUtil;
import me.zombieman.playerkitsplus.utils.SoundUtil;
import me.zombieman.playerkitsplus.utils.TimerUtils;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class PlayerKitsPlus extends JavaPlugin {
    private File kitDataFile;
    private FileConfiguration kitConfig;

    // Commands
    private CreateKitCmd createKitCmd;
    private DeleteKitCmd deleteKitCmd;
    private KitCmd kitCmd;
    private ShowKitsCmd showKidCmd;
    private ResetPlayerKitCooldownCmd resetPlayerKitCooldownCmd;
    private ChangeTimerCmd changeTimerCmd;
    private SetKitCmd setKitCmd;
    private ForceKitCmd forceKitCmd;

    // Listeners
    private JoinListener joinListener;
    private GuiListener guiListener;

    public static final boolean DEBUGMODE = false;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.saveDefaultConfig();


        String kitConfigFileName = "kit.yml";
        this.kitDataFile = new File(this.getDataFolder(), kitConfigFileName);
        if (!this.kitDataFile.exists()) this.saveResource(kitConfigFileName, false);
        this.kitConfig = YamlConfiguration.loadConfiguration(this.kitDataFile);

        // Player data folder setup
        PlayerDataManager.initDataFolder(this);

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

        PluginCommand plShowKitCmd = this.getCommand("showkit");
        this.showKidCmd = new ShowKitsCmd(this);
        if (plShowKitCmd != null) plShowKitCmd.setExecutor(this.showKidCmd);

        PluginCommand plResetPlayerCooldownCmd = this.getCommand("resetcooldown");
        this.resetPlayerKitCooldownCmd = new ResetPlayerKitCooldownCmd(this);
        if (plResetPlayerCooldownCmd != null) plResetPlayerCooldownCmd.setExecutor(this.resetPlayerKitCooldownCmd);

        PluginCommand plChangeTimerCmd = this.getCommand("changetimer");
        this.changeTimerCmd = new ChangeTimerCmd(this);
        if (plChangeTimerCmd != null) plChangeTimerCmd.setExecutor(this.changeTimerCmd);

        PluginCommand plSetKitCmd = this.getCommand("setkit");
        this.setKitCmd = new SetKitCmd(this);
        if (plSetKitCmd != null) plSetKitCmd.setExecutor(this.setKitCmd);

        PluginCommand plForceKitCmd = this.getCommand("forcekit");
        this.forceKitCmd = new ForceKitCmd(this);
        if (plForceKitCmd != null) plForceKitCmd.setExecutor(this.forceKitCmd);

        // Managers
        new KitManager();
        new ArmorManager();
        new GuiManager();
        new PlayerDataManager();

        // Utils
        new TimerUtils();
        new SoundUtil();
        new ItemUtil();

        // Listeners
        this.joinListener = new JoinListener(this);
        this.guiListener = new GuiListener(this);

        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(joinListener, this);
        pluginManager.registerEvents(guiListener, this);
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

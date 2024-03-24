package co.xenocraft;

import co.xenocraft.commands.*;
import co.xenocraft.events.ClickEvent;
import co.xenocraft.listeners.PlayerJoinLeaveListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Level;

public class QuickWarp extends JavaPlugin implements Listener {
    public static boolean welcomeMessageEnable = false;
    public static String welocmeMessageString = "";
    public static boolean warpCooldownEnable = true;
    public static int warpCooldown = 10;

    @Override
    public void onEnable() {
        //Checks for necessary files and directories.
        String currentDir = System.getProperty("user.dir") + "\\plugins\\QuickWarp";
        try {
            File dataDir = new File(currentDir);
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            File playerDir = new File(currentDir + "\\playerData");
            if (!playerDir.exists()) {
                playerDir.mkdirs();
            }
            File worldDir = new File(currentDir + "\\worldData");
            if (!worldDir.exists()) {
                worldDir.mkdirs();
            }
            File configFile = new File(currentDir + "\\config.yml");
            if (!configFile.exists()) {
                createConfigFile(configFile);
            } else {
                loadConfigFile(configFile);
            }

        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }

        //Sets up commands and listeners
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new PlayerJoinLeaveListener(), this);
        getServer().getPluginManager().registerEvents(new ClickEvent(), this);

        //Misc
        Objects.requireNonNull(getCommand("god")).setExecutor(new GodCommand());

        //Creation
        Objects.requireNonNull(getCommand("createWarp")).setExecutor(new CreateWarpCommand());
        Objects.requireNonNull(getCommand("createWorldName")).setExecutor(new CreateWorldNameCommand());
        Objects.requireNonNull(getCommand("view")).setExecutor(new ViewCommand());

        //Editing
        Objects.requireNonNull(getCommand("editWorld")).setExecutor(new EditWorldCommand());
        Objects.requireNonNull(getCommand("editWarp")).setExecutor(new EditWarpCommand());

        //Discovery
        Objects.requireNonNull(getCommand("discoverWarp")).setExecutor(new DiscoverWarpCommand());
        Objects.requireNonNull(getCommand("unlockWarp")).setExecutor(new UnlockWarpCommand());

        //Player
        Objects.requireNonNull(getCommand("warp")).setExecutor(new WarpCommand());

        //Means the plugin work somehow.
        getLogger().log(Level.INFO, "The QuickWarp Plugin works somehow???");
    }

    private void createConfigFile(File configFile) {
        String data = """
                # QuickWarp config file
                # Welcome Message when a player joins.
                Welcome-Message-Enabled:false
                # Use @player for player names.
                Welcome-Message:Welcome to the server.
                # Cooldown on opening the warp menu.
                Warp-Cooldown-Enabled:true
                Warp-Cooldown:10
                """;
        try {
            FileWriter configWriter = new FileWriter(configFile);
            configWriter.write(data);
            configWriter.close();
        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }
    }

    private void loadConfigFile(File configFile) {
        try {
            System.out.println("Found config file");
            Scanner configReader = new Scanner(configFile).useDelimiter("\n");
            List<String> configData = new ArrayList<>();
            while (configReader.hasNext()) {
                configData.add(configReader.next());
            }
            configReader.close();
            welcomeMessageEnable = Boolean.parseBoolean(configData.get(2).substring(24));
            welocmeMessageString = configData.get(3).substring(16);
            warpCooldownEnable = Boolean.parseBoolean(configData.get(5).substring(22));
            warpCooldown = Integer.parseInt(configData.get(6).substring(14));
        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }

    }

}

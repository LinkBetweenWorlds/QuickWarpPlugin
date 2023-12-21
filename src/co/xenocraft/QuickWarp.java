package co.xenocraft;

import co.xenocraft.commands.*;
import co.xenocraft.events.ClickEvent;
import co.xenocraft.listeners.PlayerJoinLeaveListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public class QuickWarp extends JavaPlugin implements Listener {
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
            if(!playerDir.exists()){
                playerDir.mkdirs();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //Sets up commands and listeners
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new PlayerJoinLeaveListener(), this);
        getServer().getPluginManager().registerEvents(new ClickEvent(), this);

        //Misc
        getCommand("god").setExecutor(new GodCommand());

        //Creation
        getCommand("createWarp").setExecutor(new CreateWarpCommand());
        getCommand("createWorldName").setExecutor(new CreateWorldNameCommand());

        //Player
        getCommand("warp").setExecutor(new WarpCommand());

        //Command Block
        getCommand("discoverWarp").setExecutor(new DiscoverWarpCommand());


        //Means the plugin work somehow.
        getLogger().log(Level.CONFIG, "The QuickWarp Plugin works somehow???");
    }
}

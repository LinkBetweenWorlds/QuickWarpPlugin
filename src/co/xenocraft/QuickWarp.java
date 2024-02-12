package co.xenocraft;

import co.xenocraft.commands.*;
import co.xenocraft.events.ClickEvent;
import co.xenocraft.listeners.PlayerJoinLeaveListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;
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
            if (!playerDir.exists()) {
                playerDir.mkdirs();
            }
            File worldDir = new File(currentDir + "\\worldData");
            if(!worldDir.exists()){
                worldDir.mkdirs();
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

        //Player
        Objects.requireNonNull(getCommand("warp")).setExecutor(new WarpCommand());

        //Command Block
        Objects.requireNonNull(getCommand("discoverWarp")).setExecutor(new DiscoverWarpCommand());


        //Means the plugin work somehow.
        getLogger().log(Level.INFO, "The QuickWarp Plugin works somehow???");
    }

}

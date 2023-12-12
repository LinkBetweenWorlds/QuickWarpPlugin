package co.xenocraft;

import co.xenocraft.commands.*;
import co.xenocraft.events.ClickEvent;
import co.xenocraft.listeners.PlayerJoinLeaveListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class QuickWarp extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        //Checks for necessary files and directories.
        String currentDir = System.getProperty("user.dir") + "\\plugins\\QuickWarp";
        try {
            File file = new File(currentDir);
            if (!file.exists()) {
                file.mkdirs();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //Sets up commands and listeners
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new PlayerJoinLeaveListener(), this);
        getServer().getPluginManager().registerEvents(new ClickEvent(), this);

        getCommand("god").setExecutor(new GodCommand());
        getCommand("createWarp").setExecutor(new CreateWarpCommand());
        getCommand("warp").setExecutor(new WarpCommand());
        getCommand("discoverWarp").setExecutor(new DiscoverWarpCommand());
        getCommand("createWorldName").setExecutor(new CreateWorldNameCommand());

        //Means the plugin work somehow.
        System.out.println("My QuickWarp plugin works somehow?");
    }
}

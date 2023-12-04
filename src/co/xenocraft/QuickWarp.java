package co.xenocraft;

import co.xenocraft.commands.*;
import co.xenocraft.events.ClickEvent;
import co.xenocraft.listeners.PlayerJoinLeaveListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class QuickWarp extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        String currentDir = System.getProperty("user.dir") + "\\plugins\\QuickWarp";
        try {
            File file = new File(currentDir + "\\warp.yml");
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            File file = new File(currentDir + "\\UUID.yml");
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("My QuickWarp plugin works somehow?");

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new PlayerJoinLeaveListener(), this);
        getServer().getPluginManager().registerEvents(new ClickEvent(), this);

        getCommand("god").setExecutor(new GodCommand());
        getCommand("createWarp").setExecutor(new CreateWarpCommand());
        getCommand("warp").setExecutor(new WarpCommand());
        getCommand("discoverWarp").setExecutor(new DiscoverWarpCommand());
        getCommand("createWorldName").setExecutor(new CreateWorldNameCommand());
    }
}

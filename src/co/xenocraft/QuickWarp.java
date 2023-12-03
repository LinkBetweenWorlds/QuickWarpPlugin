package co.xenocraft;

import co.xenocraft.commands.*;
import co.xenocraft.events.ClickEvent;
import co.xenocraft.listeners.PlayerJoinLeaveListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class QuickWarp extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
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

package co.xenocraft.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinLeaveListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        //TODO Make it so when a player join it creates/checks if they have a data folder.
        System.out.println("A player has joined the server.");
        event.setJoinMessage("Welcome " + event.getPlayer().getDisplayName() + " to §4Xenocraft!");
    }
}

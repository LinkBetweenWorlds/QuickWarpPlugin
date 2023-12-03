package co.xenocraft.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinLeaveListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        System.out.println("A player has joined the server.");
        event.setJoinMessage("Welcome " + event.getPlayer().getDisplayName() + " to" + "\u00A74 Xenocraft!");
    }
}

package org.xenocraft.collectopaedia.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.xenocraft.collectopaedia.Collectopaedia;
import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

public class PlayerJoinLeaveListener implements Listener {
    private final Collectopaedia collectopaedia;

    public PlayerJoinLeaveListener(Collectopaedia collectopaedia) {
        this.collectopaedia = collectopaedia;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!collectopaedia.playerFileExists(player)) {
            Bukkit.getLogger().log(Level.WARNING, "Player " + player.getName() + " does not exist.");
            collectopaedia.createPlayerFile(player);
        }
    }
}

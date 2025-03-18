package org.xenocraft.collectopaedia.events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.xenocraft.collectopaedia.Collectopaedia;
import org.xenocraft.collectopaedia.commands.OpenMenuCommand;

import java.util.logging.Level;

public class PlayerInvClickEvent implements Listener {

    private final OpenMenuCommand openMenuCommand; // Reuse this object instead of creating it every time

    public PlayerInvClickEvent(Collectopaedia collectopaedia) {
        this.openMenuCommand = new OpenMenuCommand(collectopaedia); // Instantiate only once
    }

    @EventHandler
    public void InvClickEvent(InventoryClickEvent event) {
        if (event.getCurrentItem() != null) {
            if (event.getView().getTitle().contains("Collectopaedia")) {
                event.setCancelled(true);
                Bukkit.getLogger().log(Level.INFO, "Inventory Click Event");
                openMenuCommand.menuClick(event);
            }
        }
    }
}

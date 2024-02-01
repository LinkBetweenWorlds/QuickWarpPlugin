package co.xenocraft.events;

import co.xenocraft.menus.WarpMenu;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClickEvent implements Listener {
    @EventHandler
    public void InvClickEvent(InventoryClickEvent event) {
        event.getWhoClicked();
        if (event.getView().getTitle().equalsIgnoreCase(ChatColor.AQUA + "Warp Menu")) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null){
                WarpMenu.menuClick(event.getCurrentItem().getType());
            }
        }

    }
}

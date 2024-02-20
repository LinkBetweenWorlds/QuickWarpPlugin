package co.xenocraft.events;

import co.xenocraft.menus.WarpMenu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClickEvent implements Listener {
    @EventHandler
    public void InvClickEvent(InventoryClickEvent event) {
        if (event.getCurrentItem() != null) {
            Player p = (Player) event.getWhoClicked();
            String guiName = event.getView().getTitle();
            if (guiName.contains("Warp Menu:")) {
                event.setCancelled(true);
                WarpMenu.menuClick(p, event.getCurrentItem(), guiName);
            }
        }


    }
}

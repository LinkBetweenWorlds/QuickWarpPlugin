package co.xenocraft.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClickEvent implements Listener {
    @EventHandler
    public void InvClickEvent(InventoryClickEvent event){
        Player p = (Player) event.getWhoClicked();
        if (event.getView().getTitle().equalsIgnoreCase(ChatColor.AQUA + "Warp Menu")){
            event.setCancelled(true);
            if (event.getCurrentItem() == null){
                return;
            }else if (event.getCurrentItem().getType().equals(Material.SANDSTONE)){
                p.sendMessage("Warping to " + ChatColor.GOLD + "Colony 9");
                p.closeInventory();
            }else if (event.getCurrentItem().getType().equals(Material.COBBLESTONE)){
                p.sendMessage("Warping to " + ChatColor.GRAY + "Tephra Cave");
                p.closeInventory();
            }else if (event.getCurrentItem().getType().equals(Material.GRASS_BLOCK)){
                p.sendMessage("Warping to " + ChatColor.GREEN + "Bionis' Leg");
                p.closeInventory();
            }
        }

    }
}

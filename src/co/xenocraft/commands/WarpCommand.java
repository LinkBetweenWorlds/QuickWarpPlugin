package co.xenocraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class WarpCommand implements TabExecutor {

    //key = uuid of the player
    //long = the epoch time of when they run the command
    private final HashMap<UUID, Long> cooldown;

    public WarpCommand(){
        this.cooldown = new HashMap<>();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        boolean result = true;
        if (sender instanceof Player){
            Player p = (Player) sender;

            if (!this.cooldown.containsKey(p.getUniqueId())){
                this.cooldown.put(p.getUniqueId(), System.currentTimeMillis());

                result = warpMenu(sender, command, s, args);
            }else{
                //Difference in milliseconds
                long timeElapsed = System.currentTimeMillis() - cooldown.get(p.getUniqueId());

                //Convert to seconds
                int seconds = (int)((timeElapsed / 1000) % 60);
                if (seconds >= 10){
                    this.cooldown.put(p.getUniqueId(), System.currentTimeMillis());
                    result = warpMenu(sender, command, s, args);
                }else{
                    p.sendMessage(ChatColor.YELLOW +  "Please wait " + (10 - seconds) + " seconds before trying to warp again.");
                }
            }
        }
        return result;
    }

    private boolean warpMenu(CommandSender sender, Command command, String s, String[] args){
        Player p = (Player) sender;
        p.sendMessage("Opening Warp Menu...");

        //TODO Finish fill out warp gui

        Inventory gui = Bukkit.createInventory(p, (4*9), ChatColor.AQUA + "Warp Menu");
        ItemStack infill = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemStack colony9 = new ItemStack(Material.SANDSTONE);
        ItemStack tephraCave = new ItemStack(Material.COBBLESTONE);
        ItemStack bionisLeg = new ItemStack(Material.GRASS_BLOCK);

        ItemMeta infillMeta = infill.getItemMeta();
        infillMeta.setDisplayName(" ");
        infill.setItemMeta(infillMeta);

        ItemMeta colony9Meta = colony9.getItemMeta();
        colony9Meta.setDisplayName(ChatColor.YELLOW + "Colony 9");
        ArrayList<String> colony9Lore = new ArrayList<>();
        colony9Lore.add(ChatColor.GOLD + "The start of your adventure.");
        colony9Lore.add(ChatColor.GOLD + "Shops, Gemshop, quests");
        colony9Meta.setLore(colony9Lore);
        colony9.setItemMeta(colony9Meta);

        ItemMeta tephraCaveMeta = tephraCave.getItemMeta();
        tephraCaveMeta.setDisplayName(ChatColor.YELLOW + "Tephra Cave");
        ArrayList<String> tephraCaveLore = new ArrayList<>();
        tephraCaveLore.add(ChatColor.GOLD + "A cave system inside the bionis' knee.");
        tephraCaveMeta.setLore(tephraCaveLore);
        tephraCave.setItemMeta(tephraCaveMeta);

        ItemMeta bionisLegMeta = bionisLeg.getItemMeta();
        bionisLegMeta.setDisplayName(ChatColor.YELLOW + "Bionis' Leg");
        ArrayList<String> bionisLegLore = new ArrayList<>();
        bionisLegLore.add(ChatColor.GOLD + "A vast open area for one to explore.");
        bionisLegMeta.setLore(bionisLegLore);
        bionisLeg.setItemMeta(bionisLegMeta);


        ItemStack[] menuItems = {infill, infill, colony9, infill, tephraCave, infill, bionisLeg, infill, infill};
        gui.setContents(menuItems);

        p.openInventory(gui);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        return null;
    }
}

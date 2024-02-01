package co.xenocraft.commands;

import co.xenocraft.QuickWarp;
import co.xenocraft.menus.WarpMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;

public class WarpCommand implements TabExecutor {

    //key = uuid of the player
    //long = the epoch time of when they run the command
    private final HashMap<UUID, Long> coolDown;
    private final String worldDir = System.getProperty("user.dir") + "\\plugins\\QuickWarp\\worldData\\";
    private final String playerDir = System.getProperty("user.dir") + "\\plugins\\QuickWarp\\playerData\\";

    public WarpCommand() {
        this.coolDown = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        boolean result = true;
        if (sender instanceof Player p) {
            if (!this.coolDown.containsKey(p.getUniqueId())) {
                this.coolDown.put(p.getUniqueId(), System.currentTimeMillis());
                WarpMenu.open(p);
                //warpMenu(p);
            } else {
                //Difference in milliseconds
                long timeElapsed = System.currentTimeMillis() - coolDown.get(p.getUniqueId());
                //Convert to seconds
                int seconds = (int) ((timeElapsed / 1000) % 60);
                if (seconds >= 10) {
                    this.coolDown.put(p.getUniqueId(), System.currentTimeMillis());
                    WarpMenu.open(p);
                    //warpMenu(p);
                } else {
                    p.sendMessage(ChatColor.YELLOW + "Please wait " + (10 - seconds) + " seconds before trying to warp again.");
                }
            }
        }
        return true;
    }

    private void warpMenu(Player p) {
        //p.sendMessage("Opening Warp Menu...");

        //TODO Finish fill out warp gui
        Inventory gui = Bukkit.createInventory(p, (6 * 9), ChatColor.AQUA + "Warp Menu");
        ItemStack infill = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta infillMeta = infill.getItemMeta();
        infillMeta.setDisplayName(" ");
        infill.setItemMeta(infillMeta);

        List<String> worldNames = new ArrayList<>();
        List<Material> worldMaterials = new ArrayList<>();
        List<String> worldsDesc = new ArrayList<>();
        List<String> playerWarpLocations = new ArrayList<>();

        try{
            File playerWarpList = new File(playerDir + "\\" + p.getUniqueId() + ".yml");
            Scanner fileReader = new Scanner(playerWarpList);
            List<String> dataList = new ArrayList<>();
            while (fileReader.hasNext()) {
                dataList.add(fileReader.next());
            }
            fileReader.close();

        }catch(Exception e){
            getLogger().log(Level.WARNING, e.toString());
        }

        try{
            File[] worldList = new File(worldDir).listFiles();
            if (worldList != null){
                for(File f : worldList){
                    String[] fileName = f.getName().split("=");
                    String worldName = fileName[0].trim();
                    UUID worldUUID = UUID.fromString(fileName[1].trim());

                }
            }else{
                p.sendMessage("There are currently no world to travel to.");
                p.sendMessage("Please use /createWorldName first.");
            }

        }catch(Exception e){
            getLogger().log(Level.WARNING, e.toString());
        }

        ItemStack colony9 = new ItemStack(Material.SANDSTONE);
        ItemStack tephraCave = new ItemStack(Material.COBBLESTONE);
        ItemStack bionisLeg = new ItemStack(Material.GRASS_BLOCK);

        ItemMeta colony9Meta = colony9.getItemMeta();
        colony9Meta.setDisplayName(ChatColor.YELLOW + "Colony 9");
        ArrayList<String> colony9Lore = new ArrayList<>();
        colony9Lore.add(ChatColor.GOLD + "The start of your adventure.");
        colony9Lore.add(ChatColor.GOLD + "Shops, Gemshop");
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

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        return null;
    }
}

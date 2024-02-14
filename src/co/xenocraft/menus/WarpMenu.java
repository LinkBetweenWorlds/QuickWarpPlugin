package co.xenocraft.menus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;

public class WarpMenu {

    private static final String currentDir = System.getProperty("user.dir");
    private static final String playerDir = currentDir + "\\plugins\\QuickWarp\\playerData\\";
    private static final String worldDir = currentDir + "\\plugins\\QuickWarp\\worldData\\";

    public static List<String> warpWorldNames = new ArrayList<>();
    public static List<Material> warpWorldMaterials = new ArrayList<>();
    public static List<String> warpWorldDescs = new ArrayList<>();
    public static List<Integer> warpWorldOrder = new ArrayList<>();

    //Loads the data from the world folder.
    private static void loadData() {
        try {
            File worldFiles = new File(worldDir);
            String[] worldNames = worldFiles.list();
            System.out.println(Arrays.toString(worldNames));

            //Loops through the worlds and adds the data to local arrays.
            for (int i = 0; i < Objects.requireNonNull(worldNames).length; i++) {
                System.out.println("Looping thru worlds...");
                String[] nameParts = worldNames[i].split("=");
                String name = nameParts[0].trim();
                warpWorldNames.add(name);

                try {
                    File file = new File(worldDir + worldNames[i] + "\\worldData.dat");
                    Scanner fileReader = new Scanner(file).useDelimiter(",");
                    List<String> dataList = new ArrayList<>();
                    while (fileReader.hasNext()) {
                        dataList.add(fileReader.next());
                    }
                    fileReader.close();
                    String[] data = dataList.toArray(new String[0]);
                    Material material = Material.matchMaterial(data[0]);
                    warpWorldMaterials.add(Objects.requireNonNullElse(material, Material.GRASS_BLOCK));
                    warpWorldDescs.add(data[1]);
                    warpWorldOrder.add(Integer.parseInt(data[2]));
                    fileReader.close();
                } catch (Exception e) {
                    getLogger().log(Level.WARNING, e.toString());
                }
            }
        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }
    }

    //Opens the main warp menu to the player.
    public static void open(Player p) {
        //Updates the warp points.
        loadData();

        try {
            File playerFile = new File(playerDir + p.getUniqueId() + ".yml");
            Scanner playerReader = new Scanner(playerFile).useDelimiter(",");
            List<String> warpList = new ArrayList<>();
            while (playerReader.hasNext()) {
                warpList.add(playerReader.next());
            }
            playerReader.close();
            String[] PlayerWarps = warpList.toArray(new String[0]);
        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }

        //Initialize the inventory.
        int invSize = (6 * 9);
        int currentInvSquare = 0;
        Inventory gui = Bukkit.createInventory(p, invSize, ChatColor.AQUA + "Warp Menu:");

        //Create infill and back items.
        ItemStack infill = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta infillMeta = infill.getItemMeta();
        Objects.requireNonNull(infillMeta).setDisplayName(" ");
        infill.setItemMeta(infillMeta);

        ItemStack backButton = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = backButton.getItemMeta();
        Objects.requireNonNull(backMeta).setDisplayName(ChatColor.RED + "Back");
        backButton.setItemMeta(backMeta);

        System.out.println("Order Size: " + warpWorldOrder.size());
        //Goes through the arrays starting with the lowest order number.
        for (int i = 0; i < warpWorldOrder.size() + 1; i++) {
            //TODO Add check to only show worlds that player has been to.
            int lowestIndex = getLowestIndex();
            ItemStack item = new ItemStack(warpWorldMaterials.get(lowestIndex));
            ItemMeta itemMeta = item.getItemMeta();
            Objects.requireNonNull(itemMeta).setDisplayName(warpWorldNames.get(lowestIndex));
            itemMeta.setLore(Collections.singletonList(warpWorldDescs.get(lowestIndex)));
            item.setItemMeta(itemMeta);
            gui.setItem(currentInvSquare, item);
            warpWorldOrder.remove(lowestIndex);
            warpWorldNames.remove(lowestIndex);
            warpWorldDescs.remove(lowestIndex);
            warpWorldMaterials.remove(lowestIndex);
            currentInvSquare += 1;

            if (currentInvSquare > invSize) {
                break;
            }
        }
        //Fill in the rest of the squares with infill.
        if (currentInvSquare < invSize) {
            for (int i = currentInvSquare; i < invSize; i++) {
                if (currentInvSquare == (invSize - 1)) {
                    gui.setItem(currentInvSquare, backButton);
                } else {
                    gui.setItem(currentInvSquare, infill);
                    currentInvSquare++;
                }

            }
        }

        p.openInventory(gui);

    }

    public static void openSubMenu(Player p, String worldName) {
        //TODO Display the discovered warp points in selected world.
        for (String warpWorldName : warpWorldNames) {
            if (worldName.equals(warpWorldName)) {
                //TODO Open new GUI with all discovered warp points.
                //Initialize the new inventory.
                int invSize = 6 * 9;
                int currentInvSquare = 0;
                Inventory subGui = Bukkit.createInventory(p, invSize, ChatColor.AQUA + worldName + " Warp Menu:");

                //Create infill and back items.
                ItemStack infill = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                ItemMeta infillMeta = infill.getItemMeta();
                Objects.requireNonNull(infillMeta).setDisplayName(" ");
                infill.setItemMeta(infillMeta);

                ItemStack backButton = new ItemStack(Material.BARRIER);
                ItemMeta backMeta = backButton.getItemMeta();
                Objects.requireNonNull(backMeta).setDisplayName(ChatColor.RED + "Back");
                backButton.setItemMeta(backMeta);
            }
        }
    }

    //Get the itemmeta from gui click
    public static void menuClick(Player p, ItemMeta itemMeta) {
        if (itemMeta != null) {
            String itemName = itemMeta.getDisplayName();
            if (itemName.equals("Back")) {
                p.closeInventory();
            } else if (!itemName.equals(" ")) {
                openSubMenu(p, itemName);
            }
        }
    }

    //Returns the lowest value in an array.
    public static int getLowestIndex() {
        int lowestValues = warpWorldOrder.get(0);
        int lowestIndex = 0;
        for (int i = 0; i < warpWorldOrder.size(); i++) {
            int current = warpWorldOrder.get(i);
            if (current < lowestValues) {
                lowestValues = current;
                lowestIndex = i;
            }
        }
        return lowestIndex;
    }
}

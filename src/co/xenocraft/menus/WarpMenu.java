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


    private static void loadData() {
        //Loads the data from the world folder.
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

    public static void open(Player p) {
        System.out.println("Starting warp menu...");
        //Opens the main warp menu to the player.

        //Updates the warp points.
        loadData();
        System.out.println("Loaded data!");

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

        System.out.println("Got Player data!");
        //Initialize the inventory.
        int invSize = 6 * 9;
        int currentInvSquare = 0;
        Inventory gui = Bukkit.createInventory(p, invSize, ChatColor.AQUA + "Warp Menu:");
        ItemStack infill = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta infillMeta = infill.getItemMeta();
        Objects.requireNonNull(infillMeta).setDisplayName(" ");
        infill.setItemMeta(infillMeta);

        System.out.println("Created Infill Item");
        //Goes through the arrays starting with the lowest order number.
        for (int i = 0; i < warpWorldOrder.size(); i++) {
            System.out.println("Loop: " + i);
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
            currentInvSquare++;
            if (currentInvSquare > invSize) {
                break;
            }
        }
        if (currentInvSquare < invSize) {
            for (int i = currentInvSquare; i < invSize; i++) {
                gui.setItem(currentInvSquare, infill);
                currentInvSquare ++;
            }
        }

        p.openInventory(gui);

    }

    public static void openSubMenu(Player p, Material material) {
        //TODO Display the discovered warp points in selected world.

    }

    public static void menuClick(Player p, ItemMeta itemMeta) {
        if (itemMeta != null) {
            String itemName = itemMeta.getDisplayName();
            if (!itemName.equals(" ")) {
                //TODO Check if the material matches a world. If so open sub menu.
                System.out.println(itemName);
            } else {

            }
        }
    }

    public static int getLowestIndex() {
        //Returns the lowest value in an array.
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

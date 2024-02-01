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

    public static List<String> warpWorldNames;
    public static List<Material> warpWorldMaterials;
    public static List<String> warpWorldDescs;
    public static List<Integer> warpWorldOrder;


    private static void loadData() {
        try {
            File worldFiles = new File(worldDir);
            String[] worldNames = worldFiles.list();
            for (int i = 0; i < Objects.requireNonNull(worldNames).length - 1; i++) {
                String[] nameParts = worldNames[i].split("=");
                String name = nameParts[0].trim();
                warpWorldNames.set(i, name);
                try {
                    File file = new File(worldNames[i] + "\\worldData.dat");
                    Scanner fileReader = new Scanner(file).useDelimiter(",");
                    List<String> dataList = new ArrayList<>();
                    while (fileReader.hasNext()) {
                        dataList.add(fileReader.next());
                    }
                    fileReader.close();
                    String[] data = dataList.toArray(new String[0]);
                    Material material = Material.matchMaterial(data[0]);
                    warpWorldMaterials.set(i, Objects.requireNonNullElse(material, Material.GRASS_BLOCK));
                    warpWorldDescs.set(i, data[1]);
                    warpWorldOrder.set(i, Integer.parseInt(data[2]));
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

        int invSize = 6 * 9;
        int currentInvSquare = 0;
        Inventory gui = Bukkit.createInventory(p, invSize, ChatColor.AQUA + "Warp Menu:");
        ItemStack infill = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta infillMeta = infill.getItemMeta();
        infillMeta.setDisplayName(" ");
        infill.setItemMeta(infillMeta);


        for (int i = 0; i < warpWorldOrder.size(); i++) {
            int lowestIndex = getLowestIndex();
            ItemStack item = new ItemStack(warpWorldMaterials.get(lowestIndex));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(warpWorldNames.get(lowestIndex));
            itemMeta.setLore(Collections.singletonList(warpWorldDescs.get(lowestIndex)));
            gui.setItem(currentInvSquare, item);
            warpWorldOrder.remove(lowestIndex);
            warpWorldNames.remove(lowestIndex);
            warpWorldDescs.remove(lowestIndex);
            warpWorldMaterials.remove(lowestIndex);
            currentInvSquare ++;
            if (currentInvSquare > invSize){
                break;
            }
        }
        if (currentInvSquare < invSize){
            for(int i = currentInvSquare; i < invSize; i ++){
                gui.setItem(currentInvSquare, infill);
            }
        }

        p.openInventory(gui);

    }


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


    public static void menuClick(Material material) {
        if (material != null) {
            if (!material.equals(Material.BLACK_STAINED_GLASS_PANE)) {


            } else {

            }
        }
    }
}

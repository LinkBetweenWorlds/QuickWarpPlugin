package co.xenocraft.menus;

import co.xenocraft.QuickWarp;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;

public class WarpMenu {
    // TODO Change warp menu to read unlock list and show it to players.

    private static final String currentDir = QuickWarp.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " ").split("QuickWarp.jar")[0];
    private static final String playerDir = currentDir + "/QuickWarp/playerData/";
    private static final String worldDir = currentDir + "/QuickWarp/worldData/";
    public static List<String> warpWorldNames = new ArrayList<>();
    public static List<Material> warpWorldMaterials = new ArrayList<>();
    public static List<String> warpWorldDescs = new ArrayList<>();
    public static List<Integer> warpWorldOrder = new ArrayList<>();
    public static List<String> warpMenuNames = new ArrayList<>();
    public static List<String> warpSubNames = new ArrayList<>();
    public static List<Material> warpSubMaterials = new ArrayList<>();
    public static List<Integer> warpSubOrder = new ArrayList<>();
    public static List<String> warpSubMenuNames = new ArrayList<>();
    public static List<String> playerWarps = new ArrayList<>();

    //Loads the data from the world folder.
    private static void loadData() {
        warpMenuNames.clear();
        warpWorldNames.clear();
        warpWorldMaterials.clear();
        warpWorldOrder.clear();
        warpWorldDescs.clear();
        try {
            File worldFiles = new File(worldDir);
            String[] worldNames = worldFiles.list();

            //Loops through the worlds and adds the data to local arrays.
            for (int i = 0; i < Objects.requireNonNull(worldNames).length; i++) {
                String[] nameParts = worldNames[i].split("=");
                String name = nameParts[0].trim();
                warpWorldNames.add(name);
                warpMenuNames.add(name);

                try {
                    File file = new File(worldDir + worldNames[i] + "/worldData.dat");
                    Scanner fileReader = new Scanner(file).useDelimiter(",");
                    List<String> dataList = new ArrayList<>();
                    while (fileReader.hasNext()) {
                        dataList.add(fileReader.next());
                    }
                    fileReader.close();
                    Material material = Material.matchMaterial(dataList.get(0));
                    warpWorldMaterials.add(Objects.requireNonNullElse(material, Material.GRASS_BLOCK));
                    warpWorldDescs.add(dataList.get(1));
                    warpWorldOrder.add(Integer.parseInt(dataList.get(2)));
                    fileReader.close();
                } catch (Exception e) {
                    getLogger().log(Level.WARNING, e.toString());
                }
            }
        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }
    }

    public static void loadSubData(String worldName) {
        warpSubMenuNames.clear();
        warpSubNames.clear();
        warpSubMaterials.clear();
        warpSubOrder.clear();
        try {
            File worldDirFile = new File(worldDir);
            List<File> worldDirList;
            worldDirList = List.of(Objects.requireNonNull(worldDirFile.listFiles()));
            for (File f : worldDirList) {
                String[] nameParts = f.getName().split("=");
                if (nameParts[0].equals(worldName)) {
                    File warpDirFile = new File(worldDir + "/" + f.getName());
                    List<File> warpDirlist = List.of(Objects.requireNonNull(warpDirFile.listFiles()));
                    for (File wf : warpDirlist) {
                        if (!wf.getName().equals("worldData.dat")) {
                            String[] warpNameParts = wf.getName().split("\\.");
                            String name = warpNameParts[0].trim();
                            warpSubNames.add(name);
                            warpSubMenuNames.add(name);
                            try {
                                Scanner warpReader = new Scanner(wf).useDelimiter(",");
                                List<String> warpData = new ArrayList<>();
                                while (warpReader.hasNext()) {
                                    warpData.add(warpReader.next());
                                }
                                warpReader.close();
                                Material material = Material.matchMaterial(warpData.get(5));
                                warpSubMaterials.add(Objects.requireNonNullElse(material, Material.GRASS_BLOCK));
                                warpSubOrder.add(Integer.parseInt(warpData.get(6)));
                            } catch (Exception e) {
                                getLogger().log(Level.WARNING, e.toString());
                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }
    }

    public static void loadPlayerWarpData(Player p) {
        try {
            File playerFile = new File(playerDir + p.getUniqueId() + ".yml");
            Scanner playerReader = new Scanner(playerFile).useDelimiter(",");
            List<String> warpList = new ArrayList<>();
            while (playerReader.hasNext()) {
                warpList.add(playerReader.next());
            }
            playerReader.close();
            playerWarps = warpList;
        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }
    }

    public static List<String> warpsInWorld(String worldName) {
        List<String> warps = new ArrayList<>();
        try {
            File worldDirFile = new File(worldDir);
            List<File> worldDirList;
            worldDirList = List.of(Objects.requireNonNull(worldDirFile.listFiles()));
            for (File f : worldDirList) {
                String[] nameParts = f.getName().split("=");
                if (nameParts[0].equals(worldName)) {
                    File warpDirFile = new File(worldDir + "/" + f.getName());
                    List<File> warpDirlist = List.of(Objects.requireNonNull(warpDirFile.listFiles()));
                    for (File wf : warpDirlist) {
                        if (!wf.getName().equals("worldData.dat")) {
                            String[] warpNameParts = wf.getName().split("\\.");
                            String name = warpNameParts[0].trim();
                            warps.add(name);
                        }
                    }
                }
            }
        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }
        return warps;
    }

    //Opens the main warp menu to the player.
    public static void open(Player p) {
        //Updates the warp points.
        loadData();
        loadPlayerWarpData(p);

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

        int listSize = warpWorldOrder.size();
        //Goes through the arrays starting with the lowest order number.
        for (int i = 1; i <= listSize; i++) {
            int lowestIndex = getLowestIndex();
            boolean playerHasWarp = false;
            List<String> worldWarps = warpsInWorld(warpWorldNames.get(lowestIndex));
            //Loops through all players and world warp in check if player has found it.
            for (String ww : worldWarps) {
                for (String pw : playerWarps) {
                    if (pw.equals(ww)) {
                        playerHasWarp = true;
                        break;
                    }
                }
            }
            //If a player has discovered warp point in that world, display world.
            if (playerHasWarp) {
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
            } else {
                warpWorldOrder.remove(lowestIndex);
                warpWorldNames.remove(lowestIndex);
                warpWorldDescs.remove(lowestIndex);
                warpWorldMaterials.remove(lowestIndex);
            }

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

    //Opens the world's sub-menu that contains all discovered warp points.
    public static void openSubMenu(Player p, String worldName) {
        for (String warpWorldName : warpMenuNames) {
            if (warpWorldName.equals(worldName)) {
                loadSubData(worldName);

                //Initialize the new inventory.
                int invSize = 6 * 9;
                int currentInvSquare = 0;
                Inventory subGui = Bukkit.createInventory(p, invSize, ChatColor.GREEN + worldName + " Warp Menu:");

                //Create infill and back items.
                ItemStack infill = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
                ItemMeta infillMeta = infill.getItemMeta();
                Objects.requireNonNull(infillMeta).setDisplayName(" ");
                infill.setItemMeta(infillMeta);

                ItemStack backButton = new ItemStack(Material.BARRIER);
                ItemMeta backMeta = backButton.getItemMeta();
                Objects.requireNonNull(backMeta).setDisplayName(ChatColor.RED + "Back");
                backButton.setItemMeta(backMeta);

                int listSize = warpSubOrder.size();
                //Goes through the arrays starting with the lowest order number.
                for (int i = 1; i <= listSize; i++) {
                    if (playerWarps != null) {
                        int lowestIndex = getSubLowestIndex();
                        //If the player has discovered a warp in that world, display it.
                        if (playerWarps.contains(warpSubNames.get(lowestIndex))) {
                            ItemStack item = new ItemStack(warpSubMaterials.get(lowestIndex));
                            ItemMeta itemMeta = item.getItemMeta();
                            Objects.requireNonNull(itemMeta).setDisplayName(warpSubNames.get(lowestIndex));
                            item.setItemMeta(itemMeta);
                            subGui.setItem(currentInvSquare, item);
                            warpSubOrder.remove(lowestIndex);
                            warpSubNames.remove(lowestIndex);
                            warpSubMaterials.remove(lowestIndex);
                            currentInvSquare += 1;
                        } else {
                            warpSubOrder.remove(lowestIndex);
                            warpSubNames.remove(lowestIndex);
                            warpSubMaterials.remove(lowestIndex);
                        }
                    }
                    if (currentInvSquare > invSize) {
                        break;
                    }
                }

                //Fill in the rest of the squares with infill.
                if (currentInvSquare < invSize) {
                    for (int i = currentInvSquare; i < invSize; i++) {
                        if (currentInvSquare == (invSize - 1)) {
                            subGui.setItem(currentInvSquare, backButton);
                        } else {
                            subGui.setItem(currentInvSquare, infill);
                            currentInvSquare++;
                        }
                    }
                }
                p.openInventory(subGui);
            }
        }
    }

    //Warps the player to the selected warp point
    public static void teleportPlayer(Player p, String worldName, String warpName) {
        String cleanWorldName = worldName.replace(ChatColor.GREEN + "", "");
        String[] cleanNameParts = cleanWorldName.split("Warp Menu");
        cleanWorldName = cleanNameParts[0].trim();
        int blockX = 0;
        int blockY = 0;
        int blockZ = 0;
        int pitch = 0;
        int yaw = 0;
        UUID worldUUID = null;

        File worldDirFile = new File(worldDir);
        List<String> worldDirList = List.of(Objects.requireNonNull(worldDirFile.list()));
        for (String s : worldDirList) {
            String[] nameParts = s.split("=");
            if (nameParts[0].equalsIgnoreCase(cleanWorldName)) {
                worldUUID = UUID.fromString(nameParts[1].trim());
                File warpDirFile = new File(worldDir + s);
                List<File> warpDirList = List.of(Objects.requireNonNull(warpDirFile.listFiles()));
                for (File f : warpDirList) {
                    String[] warpNameParts = f.getName().split("\\.");
                    if (warpNameParts[0].equalsIgnoreCase(warpName)) {
                        try {
                            Scanner warpReader = new Scanner(f).useDelimiter(",");
                            List<String> dataList = new ArrayList<>();
                            while (warpReader.hasNext()) {
                                dataList.add(warpReader.next());
                            }
                            warpReader.close();
                            blockX = Integer.parseInt(dataList.get(0));
                            blockY = Integer.parseInt(dataList.get(1));
                            blockZ = Integer.parseInt(dataList.get(2));
                            pitch = Integer.parseInt(dataList.get(3));
                            yaw = Integer.parseInt(dataList.get(4));
                        } catch (Exception e) {
                            getLogger().log(Level.WARNING, e.toString());
                        }
                    }
                }
            }
        }
        Location teleportLocation = new Location(Bukkit.getWorld(Objects.requireNonNull(worldUUID)), blockX + 0.5, blockY + 0.3, blockZ + 0.5, yaw, pitch);
        p.closeInventory();
        p.teleport(teleportLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    //Get the itemMeta from gui click
    public static void menuClick(Player p, ItemStack item, String guiName) {
        if (item != null) {
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta != null) {
                String itemName = itemMeta.getDisplayName();
                if (itemName.equals(ChatColor.RED + "Back")) {
                    if (guiName.equals(ChatColor.AQUA + "Warp Menu:")) {
                        p.closeInventory();
                    } else {
                        open(p);
                    }
                } else if (warpMenuNames.contains(itemName)) {
                    openSubMenu(p, itemName);
                } else if (warpSubMenuNames.contains(itemName)) {
                    p.closeInventory();
                    teleportPlayer(p, guiName, itemName);
                }
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

    public static int getSubLowestIndex() {
        int lowestValues = warpSubOrder.get(0);
        int lowestIndex = 0;
        for (int i = 0; i < warpSubOrder.size(); i++) {
            int current = warpSubOrder.get(i);
            if (current < lowestValues) {
                lowestValues = current;
                lowestIndex = i;
            }
        }
        return lowestIndex;
    }
}

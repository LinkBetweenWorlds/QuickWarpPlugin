package co.xenocraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;

public class EditWarpCommand implements TabExecutor {
    private static final String currentDir = System.getProperty("user.dir");
    private static final String playerDir = currentDir + "\\plugins\\QuickWarp\\playerData\\";
    private static final String worldDir = currentDir + "\\plugins\\QuickWarp\\worldData\\";
    public static int[] blockLocation;
    public static int[] blockRot;
    public static Material blockMaterial;
    public static int blockOrder;
    public static String currentWorldUUID;

    public static void updateName(Player p, String warp, String newName) {
        try {
            File f;
            if ((f = getWarpFile(warp)) != null) {
                FileWriter fw = new FileWriter(f.getParent() + "\\" + newName + ".yml");
                String data = blockLocation[0] + "," + blockLocation[1] + "," + blockLocation[2] + "," + blockRot[0] + "," + blockRot[1] + "," + blockMaterial + "," + blockOrder;
                fw.write(data);
                fw.close();
                Location blockLoc = new Location(Bukkit.getWorld(currentWorldUUID), blockLocation[0], blockLocation[1], blockLocation[2]);
                CommandBlock commandBlock = (CommandBlock) blockLoc.getBlock();
                String oldName = commandBlock.getName();
                String[] oldNameParts = oldName.split("=");
                String blockNewName = oldNameParts[0] + "=" + newName + "=" + oldNameParts[2];
                commandBlock.setName(blockNewName);
                File playerFile = new File(playerDir);
                File[] playerFileList = playerFile.listFiles();
                if (playerFileList != null) {
                    for (File pf : playerFileList) {
                        Scanner pfReader = new Scanner(pf).useDelimiter(",");
                        List<String> pfData = new ArrayList<>();
                        while (pfReader.hasNext()) {
                            pfData.add(pfReader.next());
                        }
                        for (int i = 0; i < pfData.size(); i++) {
                            if (pfData.get(i).equalsIgnoreCase(oldNameParts[1])) {
                                pfData.set(i, newName);
                            }
                        }
                        try {
                            FileWriter pfWriter = new FileWriter(pf);
                            StringBuilder pfNewData = new StringBuilder(pfData.getFirst());
                            for (int j = 1; j < pfData.size(); j++) {
                                pfNewData.append(",");
                                pfNewData.append(pfData.get(j));
                            }
                            pfWriter.write(pfNewData.toString());
                            pfWriter.close();
                        } catch (Exception e) {
                            getLogger().log(Level.WARNING, e.toString());
                        }

                    }
                }

            } else {
                p.sendMessage(ChatColor.YELLOW + "That warp point does not exist.");
            }
        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }
    }

    public static void updateBlock(Player p, String warp, String materialString) {
        try {
            File f;
            if ((f = getWarpFile(warp)) != null) {
                Material material = Material.matchMaterial(materialString);
                String data = blockLocation[0] + "," + blockLocation[1] + "," + blockLocation[2] + "," + blockRot[0] + "," + blockRot[1] + "," + material + "," + blockOrder;
                FileWriter fw = new FileWriter(f);
                fw.write(data);
                fw.close();
            } else {
                p.sendMessage(ChatColor.YELLOW + "That warp point does not exist.");
            }
        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }
    }

    public static void updateOrder(Player p, String warp, int order) {
        try {
            File f;
            if ((f = getWarpFile(warp)) != null) {
                if (order > 0) {
                    File warpDirFile = new File(f.getParent());
                    File[] warpDirList = warpDirFile.listFiles();
                    if (warpDirList != null) {
                        for (File file : warpDirList) {
                            if (!file.getName().equalsIgnoreCase(f.getName())) {
                                Scanner fileReader = new Scanner(file).useDelimiter(",");
                                List<String> dataList = new ArrayList<>();
                                while (fileReader.hasNext()) {
                                    dataList.add(fileReader.next());
                                }
                                fileReader.close();
                                int orderNum = Integer.parseInt(dataList.get(6));
                                if (orderNum >= order) {
                                    try {
                                        FileWriter fw = new FileWriter(file);
                                        dataList.set(6, String.valueOf(order + 1));
                                        StringBuilder newData = new StringBuilder(dataList.getFirst());
                                        for (int i = 1; i < dataList.size(); i++) {
                                            newData.append(",");
                                            newData.append(dataList.get(i));
                                        }
                                        fw.write(newData.toString());
                                        fw.close();
                                    } catch (Exception e) {
                                        getLogger().log(Level.WARNING, e.toString());
                                    }
                                }
                            }
                        }
                    }
                } else {
                    p.sendMessage(ChatColor.YELLOW + "The order number must be greater than 0.");
                }
            } else {
                p.sendMessage(ChatColor.YELLOW + "That warp point does not exist.");
            }
        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }

    }

    public static void deleteWarp(Player p, String warp) {
        try {
            File f;
            if ((f = getWarpFile(warp)) != null) {
                // Starts a thread to deal with remove warp from player files.
                Thread playerFileObject = new Thread(new MultiThreadPlayerWarpRemove(p, warp));
                playerFileObject.start();

                // Removes the command block and replace them with air blocks.
                Location repeatingBlockLoc = new Location(Bukkit.getWorld(currentWorldUUID),
                        blockLocation[0], blockLocation[1] - 2, blockLocation[2]);
                repeatingBlockLoc.getBlock().setType(Material.AIR);

                Location chainBlockLoc = new Location(Bukkit.getWorld(currentWorldUUID),
                        blockLocation[0], blockLocation[1] - 2, blockLocation[2] - 1);
                chainBlockLoc.getBlock().setType(Material.AIR);

                Location redstoneBlockLoc = new Location(Bukkit.getWorld(currentWorldUUID),
                        blockLocation[0], blockLocation[1] - 3, blockLocation[2]);
                redstoneBlockLoc.getBlock().setType(Material.AIR);

                // Removes the warp files from the world directory
                f.delete();

                p.sendMessage(ChatColor.GREEN + "The warp point has been removed.");
            } else {
                p.sendMessage(ChatColor.YELLOW + "That warp point does not exist.");
            }
        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }
    }

    private static File getWarpFile(String warpName) {
        try {
            File worldDirFile = new File(worldDir);
            String[] worldDirList = Objects.requireNonNull(worldDirFile.list());
            for (String s : worldDirList) {
                String[] worldNameParts = s.split("=");
                currentWorldUUID = worldNameParts[0].trim();
                File warpDirFile = new File(worldDir + "\\" + s);
                File[] warpDirList = Objects.requireNonNull(warpDirFile.listFiles());
                for (File f : warpDirList) {
                    if (f.getName().equalsIgnoreCase(warpName + ".yml")) {
                        Scanner fileReader = new Scanner(f).useDelimiter(",");
                        List<String> dataList = new ArrayList<>();
                        while (fileReader.hasNext()) {
                            dataList.add(fileReader.next());
                        }
                        fileReader.close();
                        blockLocation[0] = Integer.parseInt(dataList.get(0));
                        blockLocation[1] = Integer.parseInt(dataList.get(1));
                        blockLocation[2] = Integer.parseInt(dataList.get(2));
                        blockRot[0] = Integer.parseInt(dataList.get(3));
                        blockRot[1] = Integer.parseInt(dataList.get(4));
                        blockMaterial = Material.matchMaterial(dataList.get(5));
                        blockOrder = Integer.parseInt(dataList.get(6));

                        return f;
                    }
                }
            }
        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }

        return null;
    }

    private static String[] getWarpNames(String worldUUID) {
        File worldDirFile = new File(worldDir);
        String[] worldDirList = Objects.requireNonNull(worldDirFile.list());
        String[] currentWorldFileList = new String[0];
        for (String s : worldDirList) {
            if (s.endsWith(worldUUID)) {
                File currentWorldDir = new File(worldDir + "\\" + s);
                currentWorldFileList = currentWorldDir.list();
            }
        }
        return currentWorldFileList;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player p) {
            if (args.length == 3) {
                switch (args[1]) {
                    case "block" -> {
                        if (args[2].equalsIgnoreCase("help")) {
                            p.sendMessage("Warp Block Change");
                            p.sendMessage("Changes the block that appears on the warp menu.");
                            p.sendMessage("Please use underscore for spaces. Ex: GRASS_BLOCK");
                        } else {
                            updateBlock(p, args[0], args[2]);
                        }
                    }
                    case "delete" -> {
                        if (args[2].equalsIgnoreCase("help")) {
                            p.sendMessage("Warp Deletion");
                            p.sendMessage("Deletes the warp point from the worlds.");
                            p.sendMessage("Please use carefully, deleting warp point will remove it from player files as well.");
                        } else if (args[2].equalsIgnoreCase("confirm")) {
                            p.sendMessage(ChatColor.RED + "Deleting warp point...");
                            deleteWarp(p, args[0]);
                            p.sendMessage(ChatColor.GREEN + "The warp point has been deleted.");
                        } else {
                            p.sendMessage(ChatColor.RED + "Please type /editWarp delete confirm");
                            p.sendMessage(ChatColor.RED + "To delete the world name and the warp point within it.");
                            p.sendMessage(ChatColor.RED + "PLEASE NOTE, THERE IS NO GOING BACK AFTER YOU TYPE THIS COMMAND!");

                        }
                    }
                    case "name" -> {
                        if (args[2].equalsIgnoreCase("help")) {
                            p.sendMessage("Warp Point Name Change");
                            p.sendMessage("Changes the name of all the warp points.");
                        } else {
                            updateName(p, args[0], args[2]);
                        }
                    }
                    case "order" -> {
                        if (args[2].equalsIgnoreCase("help")) {
                            p.sendMessage("Warp Point Order Change");
                            p.sendMessage("Changes the order that world appear in world menu.");
                            p.sendMessage("This will push down all other world after it.");
                        } else {
                            updateOrder(p, args[0], Integer.parseInt(args[2]));
                        }
                    }
                }
            } else {
                p.sendMessage(ChatColor.YELLOW + "Please provide arguments. " + "/editWarp <warpName> <block/name/order/delete> <options>");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player p) {
            List<String> options = new ArrayList<>();
            if (args.length == 1) {
                String worldUUID = p.getWorld().getUID().toString();
                try {
                    String[] currentWorldFileList = getWarpNames(worldUUID);
                    if (currentWorldFileList != null) {
                        for (String w : currentWorldFileList) {
                            String[] warpNameParts = w.split("\\.");
                            if (!warpNameParts[0].equalsIgnoreCase("worldData")) {
                                options.add(warpNameParts[0]);
                            }
                        }
                    }
                } catch (Exception e) {
                    getLogger().log(Level.WARNING, e.toString());
                }
                return options;
            }
            if (args.length == 2) {
                options.add("block");
                options.add("order");
                options.add("name");
                options.add("delete");
                return options;
            }
            if (args.length == 3) {
                if (args[1].contains("block")) {
                    options = getContainedMaterials(args[2]);
                } else {
                    options.add("help");
                }
                return options;
            }
        }
        return null;
    }

    private List<String> getAllMaterials() {
        List<String> list = new ArrayList<>();
        for (Material mat : Material.values()) {
            list.add(mat.name());
        }
        return list;
    }

    private List<String> getContainedMaterials(String material) {
        List<String> list = new ArrayList<>();
        for (String mat : getAllMaterials()) {
            if (mat.startsWith(material.toUpperCase())) {
                list.add(mat);
            }
        }
        return list;
    }
}

// TODO Remove the warp from player files
class MultiThreadPlayerWarpRemove implements Runnable {
    Player p;
    String warp;

    MultiThreadPlayerWarpRemove(Player p, String warp) {
        this.p = p;
        this.warp = warp;
    }

    @Override
    public void run() {
        try {
            System.out.println("Thread: " + Thread.currentThread().threadId());
            System.out.println(p.getDisplayName());
            System.out.println(warp);
        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }
    }
}
package co.xenocraft.commands;

import co.xenocraft.QuickWarp;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;

public class EditWorldCommand implements TabExecutor {
    private static final String currentDir = QuickWarp.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " ").split("QuickWarp.jar")[0];


    private final String fileDir = currentDir + "/QuickWarp/worldData/";
    private String worldName = null;
    private Material worldBlock = null;
    private String worldDesc = null;
    private int worldOrder = -1;
    private String worldDir = "";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player p) {
            UUID worldUUID = p.getWorld().getUID();
            if (checkWorldFolder(worldUUID)) {
                if (args.length > 0) {
                    switch (args[0]) {
                        case "block" -> {
                            if (args.length > 1) {
                                if (args[1].equals("help")) {
                                    p.sendMessage("World Block Change");
                                    p.sendMessage("Changes the block that appears on the warp menu.");
                                    p.sendMessage("Please use underscore for spaces. Ex: GRASS_BLOCK");
                                } else {
                                    Material mat = Material.getMaterial(args[1].toUpperCase());
                                    if (mat != null) {
                                        updateMaterialData(mat, p);
                                        p.sendMessage("Set the world block to " + mat.name());
                                    } else {
                                        p.sendMessage(ChatColor.YELLOW + "Please check the block that you gave.");
                                    }

                                }
                            } else {
                                p.sendMessage(ChatColor.YELLOW + "Please check your arguments.");
                                p.sendMessage("/editWorld block help");
                            }
                        }
                        case "delete" -> {
                            if (args.length > 1) {
                                if (args[1].equals("help")) {
                                    p.sendMessage("World Deletion");
                                    p.sendMessage("Deletes the world name and all the warp points within it.");
                                    p.sendMessage(ChatColor.RED + "This is a very dangerous action there is no going back.");
                                } else if (args[1].equals("confirm")) {
                                    p.sendMessage(ChatColor.RED + "Deleting world...");
                                    deleteWorld(p);
                                    p.sendMessage(ChatColor.GREEN + "The world has been deleted.");
                                }
                            } else {
                                p.sendMessage(ChatColor.YELLOW + "Please type /editWorld delete confirm");
                                p.sendMessage(ChatColor.RED + "To delete the world name and the warp point within it.");
                                p.sendMessage(ChatColor.DARK_RED + "PLEASE NOTE, THERE IS NO GOING BACK AFTER YOU TYPE THIS COMMAND!");
                            }
                        }
                        case "desc" -> {
                            if (args.length > 1) {
                                if (args[1].equals("help")) {
                                    p.sendMessage("World Description Change");
                                    p.sendMessage("Change the description of the world.");
                                } else {
                                    StringBuilder desc = new StringBuilder(args[1]);
                                    for (int i = 2; i <= args.length - 1; i++) {
                                        desc.append(" ");
                                        desc.append(args[i]);
                                    }
                                    updateDescData(String.valueOf(desc), p);
                                }
                            } else {
                                p.sendMessage(ChatColor.YELLOW + "Please check your arguments.");
                                p.sendMessage("/editWorld desc help");
                            }
                        }
                        case "order" -> {
                            if (args.length > 1) {
                                if (args[1].equals("help")) {
                                    p.sendMessage("World Order Change");
                                    p.sendMessage("Changes the order that world appear in world menu.");
                                    p.sendMessage("This will push down all other world after it.");
                                } else {
                                    int orderNum = Math.abs(Integer.parseInt(args[1]));
                                    updateOrderData(orderNum, p);
                                    p.sendMessage("Change world order to:" + orderNum);
                                }
                            } else {
                                p.sendMessage(ChatColor.YELLOW + "Please check your arguments.");
                                p.sendMessage("/editWorld order help");
                            }
                        }
                        case "name" -> {
                            if (args.length > 1) {
                                if (args[1].equals("help")) {
                                    p.sendMessage("World Name Change");
                                    p.sendMessage("Changes the name of the world.");
                                } else {
                                    StringBuilder name = new StringBuilder(args[1]);
                                    for (int i = 2; i <= args.length - 1; i++) {
                                        name.append(" ").append(args[i]);
                                    }
                                    updateNameData(name.toString(), worldUUID, p);
                                }
                            } else {
                                p.sendMessage(ChatColor.YELLOW + "Please check your arguments.");
                                p.sendMessage("/editWorld name help");
                            }
                        }
                        case "info" -> {
                            p.sendMessage("World Info:");
                            p.sendMessage("Name: " + worldName);
                            p.sendMessage("Block: " + worldBlock);
                            p.sendMessage("Desc: " + worldDesc);
                            p.sendMessage("Order: " + worldOrder);
                        }
                        default -> {
                            p.sendMessage(ChatColor.YELLOW + "Please check your arguments.");
                            return false;
                        }
                    }
                } else {
                    p.sendMessage(ChatColor.YELLOW + "Please check your arguments.");
                    return false;
                }

            } else {
                p.sendMessage("Please use /createWorldName first.");
            }

        }
        return true;
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> options = new ArrayList<>();
        if (args.length == 1) {
            options.add("block");
            options.add("delete");
            options.add("desc");
            options.add("order");
            options.add("name");
            options.add("info");
            return options;
        }
        if (args.length == 2) {
            if (args[0].contains("block")) {
                options = getContainedMaterials(args[1]);
            } else {
                options.add("help");
            }
            return options;
        }
        return null;
    }

    public boolean checkWorldFolder(UUID worldUUID) {
        String UUIDString = worldUUID.toString();
        try {
            File dirList = new File(fileDir);
            String[] worldDirList = dirList.list();
            if (Objects.requireNonNull(worldDirList).length != 0) {
                for (String s : worldDirList) {
                    String[] nameParts = s.split("=");
                    String UUIDPart = nameParts[1].trim();
                    if (UUIDPart.contains(UUIDString)) {
                        worldName = nameParts[0].trim();
                        worldDir = fileDir + worldName + "=" + UUIDPart;
                        getWorldData();
                        return true;
                    }
                }
            } else {
                return false;
            }

        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }
        return false;
    }

    public void getWorldData() {
        try {
            File file = new File(worldDir + "/worldData.dat");
            Scanner fileReader = new Scanner(file).useDelimiter(",");
            List<String> dataList = new ArrayList<>();
            while (fileReader.hasNext()) {
                dataList.add(fileReader.next());
            }
            fileReader.close();
            String[] data = dataList.toArray(new String[0]);
            Material material = Material.matchMaterial(data[0]);
            worldBlock = Objects.requireNonNullElse(material, Material.GRASS_BLOCK);
            worldDesc = data[1];
            worldOrder = Integer.parseInt(data[2]);
            fileReader.close();
        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }

    }

    private void updateMaterialData(Material block, Player p) {
        try {
            FileWriter file = new FileWriter(worldDir + "/worldData.dat");
            String data = block.toString() + "," + worldDesc + "," + worldOrder;
            file.write(data);
            file.close();
            p.sendMessage(ChatColor.GREEN + "Updated World Material.");
        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }
    }

    private void updateDescData(String desc, Player p) {
        try {
            String newDesc = desc.replace(",", "");
            FileWriter file = new FileWriter(worldDir + "/worldData.dat");
            String data = worldBlock + "," + newDesc + "," + worldOrder;
            file.write(data);
            file.close();
            p.sendMessage(ChatColor.GREEN + "Updated World Description.");
        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }
    }

    private void updateOrderData(int num, Player p) {
        try {
            try {
                File[] fileList = new File(fileDir).listFiles();
                if (fileList != null) {
                    for (File value : fileList) {
                        try {
                            File file = new File(value.getAbsolutePath() + "/worldData.dat");
                            Scanner fileReader = new Scanner(file).useDelimiter(",");
                            List<String> dataList = new ArrayList<>();
                            while (fileReader.hasNext()) {
                                dataList.add(fileReader.next());
                            }
                            fileReader.close();
                            int orderNum = Integer.parseInt(dataList.get(2));
                            if (orderNum >= num) {
                                try {
                                    FileWriter fw = new FileWriter(file);
                                    String newData = dataList.get(0) + "," + dataList.get(1) + "," + (orderNum + 1);
                                    fw.write(newData);
                                    fw.close();
                                } catch (Exception e) {
                                    getLogger().log(Level.WARNING, e.toString());
                                }
                            }
                            p.sendMessage(ChatColor.GREEN + "Updated World Order.");
                        } catch (Exception e) {
                            getLogger().log(Level.WARNING, e.toString());
                        }

                    }
                }
            } catch (Exception e) {
                getLogger().log(Level.WARNING, e.toString());
            }
            FileWriter file = new FileWriter(worldDir + "/worldData.dat");
            String data = worldBlock + "," + worldDesc + "," + num;
            file.write(data);
            file.close();
        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }
    }

    private void updateNameData(String newName, UUID worldUUID, Player p) {
        File newWorldDir = new File(fileDir + newName + "=" + worldUUID);
        File oldWorldDir = new File(worldDir);
        try {
            if(newWorldDir.mkdirs()){
                //Files.move(oldWorldDir.toPath(), newWorldDir.toPath());
                File[] contents = oldWorldDir.listFiles();
                if (contents != null) {
                    for (File f : contents) {
                        f.renameTo(new File(newWorldDir + "/" +  f.getName()));
                        f.delete();
                    }
                }
                oldWorldDir.delete();
                p.sendMessage(ChatColor.GREEN + "Updated World Name.");
            }

        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }
    }

    private void deleteWorld(Player p) {
        File worldDirToDelete = new File(worldDir);
        try {
            File[] contents = worldDirToDelete.listFiles();
            if (contents != null) {
                for (File f : contents) {
                    String[] warpSplit = f.getName().split("\\.");
                    String warp = warpSplit[0].trim();
                    EditWarpCommand.deleteWarp(p, warp);
                }
            }
            worldDirToDelete.delete();
        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }
    }
}

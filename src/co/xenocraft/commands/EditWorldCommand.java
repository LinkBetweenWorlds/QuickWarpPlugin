package co.xenocraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;

public class EditWorldCommand implements TabExecutor {

    private String worldName = null;
    private Material worldBlock = null;
    private String worldDesc = null;
    private int worldOrder = -1;

    private final String fileDir = System.getProperty("user.dir") + "\\plugins\\QuickWarp\\worldData\\";
    private String worldDir = "";

    //TODO Allows the user to change the name of the world, or delete it.
    // /<command> <block> material
    // /<command> <delete> confirm
    // /<command> <desc> description
    // /<command> <order> number
    // /<command> <name> newName
    // /<command> <info>
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
                                    //TODO Update the block for the warp menu.
                                    Material mat = Material.getMaterial(args[1].toUpperCase());
                                    if (mat != null) {
                                        updateMaterialData(mat);
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
                                } else if (args[1].equals("confirm")) {
                                    //TODO Deleting both the warp points and the world folder.
                                    p.sendMessage(ChatColor.RED + "Deleting world...");
                                    p.sendMessage(ChatColor.GREEN + "The world has been deleted.");
                                }
                            } else {
                                p.sendMessage(ChatColor.RED + "Please type /editWorld delete confirm");
                                p.sendMessage(ChatColor.RED + "To delete the world name and the warp point within it.");
                                p.sendMessage(ChatColor.RED + "PLEASE NOTE, THERE IS NO GOING BACK AFTER YOU TYPE THAT COMMAND!");
                            }
                        }
                        case "desc" -> {
                            //TODO Update the description in the warp menu.
                            if (args.length > 1) {
                                if (args[1].equals("help")) {
                                    p.sendMessage("World Description Change");
                                    p.sendMessage("Change the description of the world.");
                                } else {
                                    StringBuilder desc = new StringBuilder(args[1]);
                                    for (int i = 2; i <= args.length; i++) {
                                        desc.append(" ");
                                        desc.append(args[i]);
                                    }
                                    updateDescData(String.valueOf(desc));
                                }
                            } else {
                                p.sendMessage(ChatColor.YELLOW + "Please check your arguments.");
                                p.sendMessage("/editWorld desc help");
                            }
                        }
                        case "order" -> {
                            //TODO Change the order of the worlds in the warp menu.
                            if (args.length > 1) {
                                if (args[1].equals("help")) {
                                    p.sendMessage("World Order Change");
                                    p.sendMessage("Changes the order that world appear in world menu.");
                                    p.sendMessage("This will push down all other world after it.");
                                } else {
                                    int orderNum = Math.abs(Integer.parseInt(args[2]));
                                    System.out.println("Change world order to: " + orderNum);
                                    updateOrderData(orderNum);
                                }
                            } else {
                                p.sendMessage(ChatColor.YELLOW + "Please check your arguments.");
                                p.sendMessage("/editWorld order help");
                            }
                        }
                        case "name" -> {
                            //TODO Change the name of the current world.
                            if (args.length > 1) {
                                if (args[1].equals("help")) {
                                    p.sendMessage("World Name Change");
                                    p.sendMessage("Changes the name of the world.");
                                } else {
                                    StringBuilder name = new StringBuilder(args[1]);
                                    for (int i = 2; i <= args.length; i++) {
                                        name.append(" ");
                                        name.append(args[i]);
                                    }
                                    name.toString();
                                }
                            } else {
                                p.sendMessage(ChatColor.YELLOW + "Please check your arguments.");
                                p.sendMessage("/editWorld name help");
                            }
                        }
                        case "info" -> {
                            //TODO Display the current info of the world.
                            // World Name, block, lore, order
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
        //TODO Add autofill for all options.
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
                    if (UUIDPart.equals(UUIDString)) {
                        worldName = nameParts[0].trim();
                        worldDir = fileDir + worldName + "=" + UUIDPart;
                        getWorldData();
                        return true;
                    } else {
                        return false;
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
            File file = new File(worldDir + "\\worldData.dat");
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

    private void updateMaterialData(Material block) {
        try {
            FileWriter file = new FileWriter(worldDir + "\\worldData.dat");
            String data = block.toString() + "," + worldDesc + "," + worldOrder;
            file.write(data);
            file.close();
        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }
    }

    private void updateDescData(String desc) {
        try {
            FileWriter file = new FileWriter(worldDir + "\\worldData.dat");
            String data = worldBlock + "," + desc + "," + worldOrder;
            file.write(data);
            file.close();
        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }
    }

    private void updateOrderData(int num) {
        try {
            try {
                File[] fileList = new File(fileDir).listFiles();
                if (fileList != null) {
                    for (File value : fileList) {
                        try {
                            System.out.println("File List");
                            System.out.println(value.getAbsolutePath());
                            File file = new File(value.getAbsolutePath() + "\\worldData.dat");
                            Scanner fileReader = new Scanner(file).useDelimiter(",");
                            List<String> dataList = new ArrayList<>();
                            while (fileReader.hasNext()) {
                                dataList.add(fileReader.next());
                            }
                            fileReader.close();
                            String[] data = dataList.toArray(new String[0]);
                            int orderNum = Integer.parseInt(data[2]);
                            if (orderNum >= num) {
                                try {
                                    FileWriter fw = new FileWriter(file);
                                    String newData = worldBlock + "," + worldDesc + "," + orderNum + 1;
                                    fw.write(newData);
                                    fw.close();
                                } catch (Exception e) {
                                    getLogger().log(Level.WARNING, e.toString());
                                }
                            }
                        } catch (Exception e) {
                            getLogger().log(Level.WARNING, e.toString());
                        }

                    }
                }
            } catch (Exception e) {
                getLogger().log(Level.WARNING, e.toString());
            }
            FileWriter file = new FileWriter(worldDir + "\\worldData.dat");
            String data = worldBlock + "," + worldDesc + "," + num;
            file.write(data);
            file.close();
        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }
    }

    //TODO Add ability to change world name.
    private void updateNameData(String name) {

    }

    //TODO Add ability to delete world.
    private void deleteWorld() {

    }
}

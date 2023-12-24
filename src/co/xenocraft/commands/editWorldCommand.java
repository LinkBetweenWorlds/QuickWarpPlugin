package co.xenocraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileReader;
import java.util.*;

public class editWorldCommand implements TabExecutor {

    public String worldName = null;
    public Material worldBlock = null;
    public String worldDesc = null;
    public int worldOrder = -1;

    //TODO Allows the user to change the name of the world, or delete it.
    // /<command> <block> material
    // /<command> <delete> confirm
    // /<command> <desc> description
    // /<command> <order> number
    // /<command> <name> newName
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player p) {
            UUID worldUUID = p.getWorld().getUID();
            String fileDir = System.getProperty("user.dir") + "\\plugins\\QuickWarp\\worldData\\";
            if (checkWorldFolder(worldUUID)) {
                if (args[0].equals("block")) {
                    if (args[1].equals("help")) {
                        p.sendMessage("Changes the block that appears on the warp menu.");
                    } else {
                        //TODO Update the block for the warp menu.
                    }
                } else if (args[0].equals("delete")) {
                    if (args[1].equals("help")) {
                        p.sendMessage("Deletes the world name and all the warp points within it.");
                    } else if (args[1].equals("confirm")) {
                        //TODO Deleting both the warp points and the world folder.
                        p.sendMessage(ChatColor.RED + "Deleting world...");
                        p.sendMessage(ChatColor.GREEN + "The world has been deleted.");
                    } else {
                        p.sendMessage(ChatColor.RED + "Please type /editWorld delete confirm");
                        p.sendMessage(ChatColor.RED + "To delete the world name and the warp point within it.");
                        p.sendMessage(ChatColor.RED + "PLEASE NOTE, THERE IS NO GOING BACK AFTER YOU TYPE THAT COMMAND!");
                    }
                } else if (args[0].equals("desc")) {
                    //TODO Update the description in the warp menu.
                    if (args[1].equals("help")) {
                        p.sendMessage("Change the description of the world.");
                    } else {
                        StringBuilder desc = new StringBuilder(args[1]);
                        for (int i = 2; i <= args.length; i++) {
                            desc.append(" ");
                            desc.append(args[i]);
                        }
                        desc.toString();
                    }
                } else if (args[0].equals("order")) {
                    //TODO Change the order of the worlds in the warp menu.
                    if (args[1].equals("help")) {
                        p.sendMessage("Changes the order that world appear in world menu.");
                        p.sendMessage("This will push down all other world after it.");
                    } else {
                        int orderNum = Math.abs(Integer.parseInt(args[2]));

                    }

                } else if (args[0].equals("name")) {
                    //TODO Change the name of the current world.
                    if (args[1].equals("help")) {
                        p.sendMessage("Changes the name of the world.");
                    } else {
                        StringBuilder name = new StringBuilder(args[1]);
                        for(int i = 2; i <= args.length; i++){
                            name.append(" ");
                            name.append(args[i]);
                        }
                        name.toString();
                    }
                }else if(args[0].equals("info")){
                    //TODO Display the current info of the world.
                    // World Name, block, lore, order
                    p.sendMessage("World Info:");
                    p.sendMessage("Name: " + worldName);
                    p.sendMessage("Block: " + worldBlock.toString());
                    p.sendMessage("Desc: " + worldDesc);
                    p.sendMessage("Order: " + worldOrder);
                }
                else {
                    p.sendMessage(ChatColor.YELLOW + "Please check you arguments.");
                }
            }else{
                p.sendMessage("Please use /createWorldName first.");
            }

        }
        return false;
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
        if (args.length == 2){
            if(args[1].equals("block")){
                String[] materials = Arrays.stream(Material.values()).map(Enum::name).toArray(String[]::new);
                options.addAll(Arrays.asList(materials));
                options.add("help");
                return options;
            }else{
                options.add("help");
                return options;
            }
        }
        return null;
    }

    public boolean checkWorldFolder(UUID worldUUID) {
        String UUIDString = worldUUID.toString();
        String fileDir = System.getProperty("user.dir") + "\\plugins\\QuickWarp\\worldData\\";
        try {
            File dirList = new File(fileDir);
            String[] worldDirList = dirList.list();
            if (worldDirList.length != 0) {
                for (String s : worldDirList) {
                    String[] nameParts = s.split("=");
                    String UUIDPart = nameParts[1].trim();
                    if (UUIDPart.equals(UUIDString)) {
                        worldName = nameParts[0].trim();
                        getCurrentWorldInfo(fileDir + worldName + "=" + UUIDPart);
                        return true;
                    } else {
                        return false;
                    }
                }
            } else {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void getCurrentWorldInfo(String dir){
        try{
            File file = new File(dir);
            Scanner fileReader = new Scanner(file).useDelimiter(",");
            List<String> dataList = new ArrayList<>();
            while (fileReader.hasNext()) {
                dataList.add(fileReader.next());
            }
            fileReader.close();
            String[] data = dataList.toArray(new String[0]);
            Material material = Material.matchMaterial(data[0]);
            if (material != null){
                worldBlock = material;
            }else{
                worldBlock = Material.GRASS_BLOCK;
            }
            worldDesc = data[1];
            worldOrder = Integer.parseInt(data[2]);

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}

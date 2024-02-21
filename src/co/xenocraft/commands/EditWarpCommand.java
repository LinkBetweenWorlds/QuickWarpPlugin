package co.xenocraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;

public class EditWarpCommand implements TabExecutor {
    private static final String currentDir = System.getProperty("user.dir");
    private static final String playerDir = currentDir + "\\plugins\\QuickWarp\\playerData\\";
    private static final String worldDir = currentDir + "\\plugins\\QuickWarp\\worldData\\";

    //TODO Update the name of the warp point.
    // Loop thru all the player files and replace the old warp name with the name one.
    public static void updateName(String warp, String newName) {

    }

    //TODO Change block in warp file.
    // Check if material exists than replace.
    public static void updateBlock(String warp, String materialString) {

    }

    //TODO Change warp's order number.
    // Push back all the other warp points behind the new number.
    public static void updateOrder(String warp, int order) {

    }

    //TODO Delete the warp.
    // Remove the warp from all the player files.
    // Remove command blocks from world as well. (Replace with air blocks. )
    public static void deleteWarp(String warp) {

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
                            updateBlock(args[0], args[2]);
                        }
                    }
                    case "delete" -> {
                        if (args[2].equalsIgnoreCase("help")) {
                            p.sendMessage("Warp Deletion");
                            p.sendMessage("Deletes the warp point from the worlds.");
                            p.sendMessage("Please use carefully, deleting warp point will remove it from player files as well.");
                        } else if (args[2].equalsIgnoreCase("confirm")) {
                            //TODO Deleting both the warp points and the world folder.
                            p.sendMessage(ChatColor.RED + "Deleting warp point...");
                            deleteWarp(args[0]);
                            p.sendMessage(ChatColor.GREEN + "The warp point has been deleted.");
                        } else {
                            p.sendMessage(ChatColor.RED + "Please type /editWarp delete confirm");
                            p.sendMessage(ChatColor.RED + "To delete the world name and the warp point within it.");
                            p.sendMessage(ChatColor.RED + "PLEASE NOTE, THERE IS NO GOING BACK AFTER YOU TYPE THIS COMMAND!");

                        }
                    }
                    case "name" -> {
                        if(args[2].equalsIgnoreCase("help")){
                            p.sendMessage("Warp Point Name Change");
                            p.sendMessage("Changes the name of all the warp points.");
                        } else{
                            updateName(args[0], args[2]);
                        }
                    }
                    case "order" -> {
                        if(args[2].equalsIgnoreCase("help")){
                            p.sendMessage("Warp Point Order Change");
                            p.sendMessage("Changes the order that world appear in world menu.");
                            p.sendMessage("This will push down all other world after it.");
                        }else{
                            updateOrder(args[0], Integer.parseInt(args[2]));
                        }
                    }
                }
            } else {
                p.sendMessage(ChatColor.YELLOW + "Please provide arguments. " +
                        "/editWarp <warpName> <block/name/order/delete> <options>");
            }
        }
        return false;
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
                            options.add(warpNameParts[0]);
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
            }
            if (args.length == 3) {
                if (args[0].contains("block")) {
                    options = getContainedMaterials(args[1]);
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

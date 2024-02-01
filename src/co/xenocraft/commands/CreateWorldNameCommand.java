package co.xenocraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;

public class CreateWorldNameCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length >= 1) {
                UUID worldUUID = p.getWorld().getUID();
                String worldName = args[0];

                //Change all args into a String
                for (int i = 1; i < args.length; i++) {
                    worldName += " ";
                    worldName += args[i];
                }

                checkWorldExists(worldUUID, worldName, p);
            } else {
                p.sendMessage(ChatColor.YELLOW + "Please input the name name of this world.");
                p.sendMessage("/createWorldName <name>");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> options = new ArrayList<>();
        return options;
    }


    public boolean checkWorldExists(UUID worldUUID, String worldName, Player p) {
        //Creates the file directory path of the folder.
        String UUIDString = worldUUID.toString();
        String folderName = worldName + "=" + UUIDString;
        String fileDir = System.getProperty("user.dir") + "\\plugins\\QuickWarp\\worldData\\";
        String worldDir = fileDir + folderName;
        p.sendMessage(ChatColor.GREEN + "Trying to create world...");
        try {
            //Checks the world UUID to check if world exists.
            File dirList = new File(fileDir);
            String[] worldDirList = dirList.list();
            //Checks if world exists within the list that was returned.
            if (worldDirList != null) {
                if (worldDirList.length != 0) {
                    for (int i = 0; i < worldDirList.length; i++) {
                        //Gets and checks the UUID against all world folders.
                        String[] nameParts = worldDirList[i].split("=");
                        String UUIDParts = nameParts[1].trim();
                        if (UUIDParts.equals(UUIDString)) {
                            p.sendMessage(ChatColor.RED + "The world already has a name.");
                            return false;
                        } else {
                            File dir = new File(worldDir);
                            if (!dir.exists()) {
                                dir.mkdirs();
                                worldDataFile(worldDir);
                                p.sendMessage(ChatColor.GREEN + worldName + " has been created.");
                                return true;
                            }
                        }
                    }
                } else {
                    File dir = new File(worldDir);
                    if (!dir.exists()) {
                        dir.mkdirs();
                        worldDataFile(worldDir);
                        p.sendMessage(ChatColor.GREEN + worldName + " has been created.");
                        return true;
                    }
                }
            } else {
                File dir = new File(worldDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                    worldDataFile(worldDir);
                    p.sendMessage(ChatColor.GREEN + worldName + " has been created.");
                    return true;
                }
            }

        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }
        return false;
    }

    public void worldDataFile(String dir) {
        //TODO Add data file that contains the world's info.
        // Like What block to display.
        // The world desc and the order it should appear in.
        // Add another command that allows you to edit this data file.
        try {
            Material worldBlock = Material.GRASS_BLOCK;
            String worldDesc = "";
            int worldOrder = 0;
            try {
                String worldDir = System.getProperty("user.dir") + "\\plugins\\QuickWarp\\worldData\\";
                File[] dirList = new File(worldDir).listFiles();
                if (dirList != null) {
                    worldOrder = dirList.length;
                }
            } catch (Exception e) {
                getLogger().log(Level.WARNING, e.toString());
            }
            FileWriter file = new FileWriter(dir + "\\worldData.dat");
            String data = worldBlock.toString() + "," + worldDesc + "," + worldOrder;
            file.write(data);
            file.close();
        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }


    }

}


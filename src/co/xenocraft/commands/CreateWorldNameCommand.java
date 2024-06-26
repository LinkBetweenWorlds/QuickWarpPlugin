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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;

public class CreateWorldNameCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player p) {
            if (args.length >= 1) {
                UUID worldUUID = p.getWorld().getUID();
                StringBuilder worldName = new StringBuilder(args[0]);

                //Change all args into a String
                for (int i = 1; i < args.length; i++) {
                    worldName.append(" ");
                    worldName.append(args[i]);
                }

                checkWorldExists(worldUUID, worldName.toString(), p);
            } else {
                p.sendMessage(ChatColor.YELLOW + "Please input the name name of this world.");
                p.sendMessage("/createWorldName <name>");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        return new ArrayList<>();
    }


    public void checkWorldExists(UUID worldUUID, String worldName, Player p) {
        //Creates the file directory path of the folder.
        String UUIDString = worldUUID.toString();
        String folderName = worldName + "=" + UUIDString;
        String fileDir = QuickWarp.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " ").split("QuickWarp.jar")[0] + "/QuickWarp/worldData/";
        String worldDir = fileDir + folderName;
        p.sendMessage(ChatColor.GREEN + "Trying to create world...");
        try {
            //Checks the world UUID to check if world exists.
            File dirList = new File(fileDir);
            String[] worldDirList = dirList.list();
            //Checks if world exists within the list that was returned.
            if (worldDirList != null) {
                if (worldDirList.length != 0) {
                    for (String s : worldDirList) {
                        //Gets and checks the UUID against all world folders.
                        String[] nameParts = s.split("=");
                        String UUIDParts = nameParts[1].trim();
                        if (UUIDParts.equals(UUIDString)) {
                            p.sendMessage(ChatColor.RED + "The world already has a name.");
                            return;
                        } else {
                            File dir = new File(worldDir);
                            if (!dir.exists()) {
                                dir.mkdirs();
                                worldDataFile(worldDir);
                                p.sendMessage(ChatColor.GREEN + worldName + " has been created.");
                                return;
                            }
                        }
                    }
                } else {
                    File dir = new File(worldDir);
                    if (!dir.exists()) {
                        dir.mkdirs();
                        worldDataFile(worldDir);
                        p.sendMessage(ChatColor.GREEN + worldName + " has been created.");
                    }
                }
            } else {
                File dir = new File(worldDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                    worldDataFile(worldDir);
                    p.sendMessage(ChatColor.GREEN + worldName + " has been created.");
                }
            }

        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }
    }

    public void worldDataFile(String dir) {
        try {
            Material worldBlock = Material.GRASS_BLOCK;
            String worldDesc = "";
            int worldOrder = 0;
            try {
                String worldDir = QuickWarp.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " ").split("QuickWarp.jar")[0] + "/QuickWarp/worldData/";
                File[] dirList = new File(worldDir).listFiles();
                if (dirList != null) {
                    worldOrder = dirList.length;
                }
            } catch (Exception e) {
                getLogger().log(Level.WARNING, e.toString());
            }
            FileWriter file = new FileWriter(dir + "/worldData.dat");
            String data = worldBlock + "," + worldDesc + "," + worldOrder;
            file.write(data);
            file.close();
        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }


    }

}


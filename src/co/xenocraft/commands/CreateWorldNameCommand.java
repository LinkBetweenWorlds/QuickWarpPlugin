package co.xenocraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;
import java.util.UUID;

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
        return null;
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
            //Checks if world exsists within the list that was returned.
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
                                p.sendMessage(ChatColor.GREEN + worldName + " has been created.");
                                return true;
                            }
                        }
                    }
                } else {
                    File dir = new File(worldDir);
                    if (!dir.exists()) {

                        dir.mkdirs();
                        p.sendMessage(ChatColor.GREEN + worldName + " has been created.");
                        return true;
                    }
                }
            } else {
                File dir = new File(worldDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                    p.sendMessage(ChatColor.GREEN + worldName + " has been created.");
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}


package co.xenocraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CreateWorldNameCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player p) {
            if (args.length >= 1) {
                UUID worldUUID = p.getWorld().getUID();
                String worldName = args[0];

                //Change all args into a String
                for (int i = 1; i < args.length; i++) {
                    worldName += " ";
                    worldName += args[i];
                }

                //Checks if the world already has a world given to it.
                if(!checkWorldExists(worldUUID, worldName)){
                    p.sendMessage(ChatColor.RED + "This world already has a name or there was at error in the code.");
                }else{
                    p.sendMessage(ChatColor.GREEN + worldName + " was created.");
                }


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

    String currentDir = System.getProperty("user.dir");
    String fileDir = currentDir + "\\plugins\\QuickWarp";

    public boolean checkWorldExists(UUID worldUUID, String worldName){
        //Creates the file directory of the folder.
        String UUIDString = worldUUID.toString();
        String folderName = worldName + ":" + UUIDString;
        String worldDir = fileDir + folderName;
        try{
            //Checks the world UUID to check if world exists.
            //TODO Rewrite using list() to check faster.
            File file = new File(worldDir);
            String fileName = file.getName();
            String[] nameParts = fileName.split(":");
            String UUIDParts = nameParts[1].trim();
            if (UUIDParts.equals(UUIDString)) {
                if(!file.exists()){
                    return file.mkdirs();
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}


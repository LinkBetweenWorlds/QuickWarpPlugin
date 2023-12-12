package co.xenocraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class editWorldCommand implements TabExecutor {
    //TODO Allows the user to change the name of the world, or delete it.
    // /<command> <edit> <name>
    // /<command> <delete>
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            if (args[0].equals("edit")) {
                UUID worldUUID = p.getWorld().getUID();
                if (checkWorldFolder(worldUUID)) {
                    String fileDir = System.getProperty("user.dir") + "\\plugins\\QuickWarp\\worldData\\";

                } else {
                    p.sendMessage(ChatColor.RED + "Please use /createWorldName first to give the world a name.");
                }
            } else if (args[0].equals("delete")) {

            } else {
                p.sendMessage(ChatColor.RED + "Please check you arguments. Edit/Delete");
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
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
                    String UUIDParts = nameParts[1].trim();
                    if (UUIDParts.equals(UUIDString)) {
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
}

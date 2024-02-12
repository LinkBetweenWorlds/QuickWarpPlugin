package co.xenocraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;


public class ViewCommand implements TabExecutor {
    private final String worldDir = System.getProperty("user.dir") + "\\plugins\\QuickWarp\\worldData\\";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player p) {
            if (args.length == 1) {
                args[0].toLowerCase();
                //TODO Display either all worlds with a name.
                // or display all the warp points in the current world,
                // along with each warp points data (secret, location, range)
                if (args[0].equals("world")) {
                    List<String> worldFileList;
                    List<String> worldList = new ArrayList<>();
                    List<UUID> worldUUIDList = new ArrayList<>();
                    File worldFiles = new File(worldDir);
                    worldFileList = Arrays.asList(Objects.requireNonNull(worldFiles.list()));
                    for (String s : worldFileList) {
                        String[] worldParts = s.split("=");
                        worldList.add(worldParts[0].trim());
                        worldUUIDList.add(UUID.fromString(worldParts[1].trim()));
                    }
                    for(int i = 0; i <worldList.size(); i++ ){
                        p.sendMessage("World Name: " + worldList.get(i) + " UUID: " + worldUUIDList.get(i));
                    }

                } else if (args[0].equals("warps")) {
                    String worldUUID = p.getWorld().getUID().toString();
                } else {
                    argsMessage(p);
                }
            } else {
                argsMessage(p);
            }
        }
        return false;
    }

    private void argsMessage(Player p) {
        p.sendMessage(ChatColor.YELLOW + "Please state weather you want to view the different worlds, " +
                "or the warps in this world.");
        p.sendMessage(ChatColor.YELLOW + "/view <world/warps>");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 1) {
            List<String> options = new ArrayList<>();
            options.add("world");
            options.add("warps");
            return options;
        }
        return null;
    }


}

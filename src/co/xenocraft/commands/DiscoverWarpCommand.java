package co.xenocraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.io.File;
import java.io.FileReader;
import java.util.*;

public class DiscoverWarpCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof BlockCommandSender) {
            BlockCommandSender block = (BlockCommandSender) sender;
            //System.out.println(block.getName());

            //Gets the command block location
            Location blockLoc = block.getBlock().getLocation();
            int blockX = blockLoc.getBlockX();
            int blockY = blockLoc.getBlockY();
            int blockZ = blockLoc.getBlockZ();

            //Gets the nearby entities in bbox
            //TODO Check if the args from command block get passed thru.
            // If so change createWarp command to pass range thru and use it in bbox.
            int range = 5;
            BoundingBox bbox = new BoundingBox(blockX - range, blockY - 1, blockZ - range, blockX + range, blockY + 4, blockZ + range);
            Collection<Entity> players = Bukkit.getWorld(blockLoc.getWorld().getUID()).getNearbyEntities(bbox);
            ArrayList<Entity> nearestPLayers = new ArrayList<>((players));

            //TODO Change so it updates player file with discovered warp points.

            //Find the nearest Player in bbox
            for (Entity nearestPLayer : nearestPLayers) {
                if (nearestPLayer.getType().equals(EntityType.PLAYER)) {
                    Player p = (Player) nearestPLayer;
                    UUID playerID = p.getUniqueId();
                    try {
                        //Gets the player file to check if they have already discovered this warp.
                        String fileDir = System.getProperty("user.dir") + "\\plugins\\QuickWarp\\playerData\\" + playerID.toString() + ".yml";
                        File file = new File(fileDir);
                        Scanner fileReader = new Scanner(file).useDelimiter(",");
                        List<String> warpList = new ArrayList<>();
                        while (fileReader.hasNext()) {
                            warpList.add(fileReader.next());
                        }
                        fileReader.close();
                        String[] warps = warpList.toArray(new String[0]);
                        for (String w : warps){
                            System.out.println(w);
                        }
                        //TODO After reading the file, Check the discovered warp points.
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        } else if (sender instanceof Player) {
            Player p = (Player) sender;
            p.sendMessage("This is a command block command for now.");
        } else if (sender instanceof ConsoleCommandSender) {
            System.out.println("This is a command block command for now.");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        return null;
    }
}

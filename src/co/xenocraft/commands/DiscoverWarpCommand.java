package co.xenocraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DiscoverWarpCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof BlockCommandSender){
            BlockCommandSender block = (BlockCommandSender) sender;
            System.out.println(block.getName());
            Location blockLoc = block.getBlock().getLocation();
            int blockX = blockLoc.getBlockX();
            int blockY = blockLoc.getBlockY();
            int blockZ = blockLoc.getBlockZ();
            int range = 5;
            BoundingBox bbox = new BoundingBox(blockX - range, blockY - 1, blockZ - range, blockX + range, blockY + 4, blockZ + range);
            Collection<Entity> players = Bukkit.getWorld(blockLoc.getWorld().getUID()).getNearbyEntities(bbox);
            ArrayList<Entity> nearestPLayers = new ArrayList<>((players));
            for (int i = 0; i < nearestPLayers.size(); i ++){
                if(nearestPLayers.get(i).getType().equals(EntityType.PLAYER)){
                    System.out.println(nearestPLayers.get(i));
                    Player p = (Player) nearestPLayers.get(i);
                    System.out.println(p.getDisplayName());
                    System.out.println(p.getLocation());
                    p.sendMessage("You have discovered " + block.getName());
                }
            }
        }else if (sender instanceof Player){
            Player p = (Player) sender;
            p.sendMessage("This is a command block command for now.");
        }else if (sender instanceof ConsoleCommandSender){
            System.out.println("Test");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        return null;
    }
}

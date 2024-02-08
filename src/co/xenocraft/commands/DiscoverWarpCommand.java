package co.xenocraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;

public class DiscoverWarpCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof BlockCommandSender block) {
            //TODO Display discovered warp name on player's screen.
            // Change colour and effects if secret warp points.

            //Gets the command block location
            Location blockLoc = block.getBlock().getLocation();
            int blockX = blockLoc.getBlockX();
            int blockY = blockLoc.getBlockY();
            int blockZ = blockLoc.getBlockZ();

            //Gets the nearby entities in bbox
            //TODO Check if the args from command block get passed thru.
            // If so change createWarp command to pass range thru and use it in bbox.
            int range = 5;
            boolean secret = false;
            String blockName = "Warp Point";
            String blockString = block.getName();
            String[] blockArgs = blockString.split("=");
            try {
                secret = Boolean.parseBoolean(blockArgs[0].trim());
                blockName = blockArgs[1].trim();
                range = Integer.parseInt(blockArgs[2].trim());
            } catch (Exception e) {
                getLogger().log(Level.WARNING, e.toString());
            }
            BoundingBox bbox = new BoundingBox(blockX - range, blockY - 1, blockZ - range, blockX + range, blockY + 4, blockZ + range);
            Collection<Entity> players = Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(blockLoc.getWorld()).getUID())).getNearbyEntities(bbox);
            ArrayList<Entity> nearestPLayers = new ArrayList<>((players));

            //TODO Change so it updates player file with discovered warp points.

            //Find the nearest Player in bbox
            for (Entity nearestPLayer : nearestPLayers) {
                if (nearestPLayer.getType().equals(EntityType.PLAYER)) {
                    Player p = (Player) nearestPLayer;
                    UUID playerID = p.getUniqueId();
                    try {
                        //Gets the player file to check if they have already discovered this warp.
                        String fileDir = System.getProperty("user.dir") + "\\plugins\\QuickWarp\\playerData\\" + playerID + ".yml";
                        File file = new File(fileDir);
                        Scanner fileReader = new Scanner(file).useDelimiter(",");
                        List<String> warpList = new ArrayList<>();
                        while (fileReader.hasNext()) {
                            warpList.add(fileReader.next());
                        }
                        fileReader.close();
                        for (String w : warpList) {
                            System.out.println(w);
                        }
                        //TODO After reading the file, Check the discovered warp points.
                        if (warpList.contains(blockName)) {
                            break;
                        } else {
                            Location pLoc = p.getLocation();
                            p.sendMessage(ChatColor.GREEN + "You discovered " + blockName);
                            if (secret) {
                                p.sendTitle(ChatColor.YELLOW + blockName, "", 15, 80, 15);
                                p.spawnParticle(Particle.FIREWORKS_SPARK, pLoc, 10);
                            } else {
                                p.sendTitle(ChatColor.GOLD + blockName, "", 10, 100, 15);
                                p.spawnParticle(Particle.FIREWORKS_SPARK, pLoc, 20);
                                p.spawnParticle(Particle.ELECTRIC_SPARK, pLoc, 20);
                            }
                            StringBuilder dataString = new StringBuilder("blockName");
                            for (String d : warpList) {
                                dataString.append(",").append(d);
                            }
                            String data = dataString.toString();
                            try {
                                FileWriter writer = new FileWriter(file);
                                writer.write(data);
                                writer.close();
                            } catch (Exception e) {
                                getLogger().log(Level.WARNING, e.toString());
                            }
                        }
                    } catch (Exception e) {
                        getLogger().log(Level.WARNING, e.toString());
                    }
                }
            }
        } else if (sender instanceof Player p) {
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

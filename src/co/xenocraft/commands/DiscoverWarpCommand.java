package co.xenocraft.commands;

import org.bukkit.*;
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
            //Gets the command block location
            Location blockLoc = block.getBlock().getLocation();
            int blockX = blockLoc.getBlockX();
            int blockY = blockLoc.getBlockY();
            int blockZ = blockLoc.getBlockZ();

            //Gets the nearby entities in bbox
            int range = 5;
            boolean secret = false;
            String blockName = "WarpPoint";
            String blockString = block.getName();
            String[] blockArgs = blockString.split("=");
            try {
                if (blockArgs[0].trim().equals("1")) {
                    secret = true;
                }
                blockName = blockArgs[1].trim();
                range = Integer.parseInt(blockArgs[2].trim());
            } catch (Exception e) {
                getLogger().log(Level.WARNING, e.toString());
            }
            BoundingBox bbox = new BoundingBox(blockX - range, blockY - 1, blockZ - range, blockX + range, blockY + 4, blockZ + range);
            Collection<Entity> players = Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(blockLoc.getWorld()).getUID())).getNearbyEntities(bbox);
            ArrayList<Entity> nearestPLayers = new ArrayList<>((players));

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
                        //TODO After reading the file, Check the discovered warp points.
                        if (warpList.contains(blockName)) {
                            break;
                        } else {
                            Location pLoc = p.getLocation();
                            Location particleLoc = new Location(pLoc.getWorld(), pLoc.getX(), pLoc.getY() + 2, pLoc.getZ());
                            p.sendMessage(ChatColor.GREEN + "You discovered " + blockName);
                            if (secret) {
                                p.sendTitle(ChatColor.GOLD + blockName, ChatColor.GOLD + "has been discovered", 8, 80, 14);
                                p.spawnParticle(Particle.FIREWORKS_SPARK, particleLoc, 500);
                                p.spawnParticle(Particle.ELECTRIC_SPARK, particleLoc, 500);
                                p.playSound(p, Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MUSIC, 115, 0.95f);
                            } else {
                                p.sendTitle(ChatColor.GREEN + blockName, "has been discovered", 6, 60, 12);
                                p.spawnParticle(Particle.FIREWORKS_SPARK, particleLoc, 200);
                                //String soundDir = System.getProperty("user.dir") + "\\plugins\\QuickWarp\\sounds\\test.mp3";
                                //p.playSound(p.getLocation(), soundDir, SoundCategory.MUSIC, 100, 1);
                                p.playSound(p, Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MUSIC, 100, 1);
                            }
                            StringBuilder data = new StringBuilder(blockName);
                            for (String d : warpList) {
                                data.append(",").append(d);
                            }
                            try {
                                FileWriter writer = new FileWriter(file);
                                writer.write(data.toString());
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

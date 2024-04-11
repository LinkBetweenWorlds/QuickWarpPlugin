package co.xenocraft.commands;

import co.xenocraft.QuickWarp;
import co.xenocraft.fileSystem;
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
    private static final String currentDir = QuickWarp.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " ").split("QuickWarp.jar")[0];
    private static final String playerDir = currentDir + "/QuickWarp/playerData/";
    private static final String worldDir = currentDir + "/QuickWarp/worldData/";

    //TODO Add feature that allows user to unlock or lock a warp point for a player.
    // /<command> <world> <warpPoint> <player> <unlock/lock>
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof BlockCommandSender block) {
            if (args.length == 4) {
                //Gets the command block location
                Location blockLoc = block.getBlock().getLocation();
                int blockX = blockLoc.getBlockX();
                int blockY = blockLoc.getBlockY();
                int blockZ = blockLoc.getBlockZ();

                //Gets the nearby entities in bbox
                String warpName = args[0].replace("_", " ");
                int range = Integer.parseInt(args[1]);
                boolean secret = Boolean.parseBoolean(args[2]);
                int yOffset = Integer.parseInt(args[3]);

                BoundingBox bbox = new BoundingBox(blockX - range, blockY - 1 - yOffset, blockZ - range, blockX + range, blockY + 4 - yOffset, blockZ + range);
                Collection<Entity> players = Objects.requireNonNull(Bukkit.getWorld(Objects.requireNonNull(blockLoc.getWorld()).getUID())).getNearbyEntities(bbox);
                ArrayList<Entity> nearestPlayers = new ArrayList<>((players));

                //Find the nearest Player in bbox
                for (Entity nearestPLayer : nearestPlayers) {
                    if (nearestPLayer.getType().equals(EntityType.PLAYER)) {
                        Player p = (Player) nearestPLayer;
                        UUID playerUUID = p.getUniqueId();
                        try {
                            //Gets the player file to check if they have already discovered this warp.
                            List<String> warpList = fileSystem.getPlayerWarps(playerUUID);
                            if (warpList.contains(warpName)) {
                                break;
                            } else {
                                Location pLoc = p.getLocation();
                                Location particleLoc = new Location(pLoc.getWorld(), pLoc.getX(), pLoc.getY() + 2, pLoc.getZ());
                                p.sendMessage(ChatColor.GREEN + "You discovered " + warpName);
                                if (secret) {
                                    p.sendTitle(ChatColor.GOLD + warpName, ChatColor.GOLD + "has been discovered", 8, 80, 14);
                                    p.spawnParticle(Particle.FIREWORKS_SPARK, particleLoc, 500);
                                    p.spawnParticle(Particle.ELECTRIC_SPARK, particleLoc, 500);
                                    p.playSound(p, Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MUSIC, 115, 0.95f);
                                } else {
                                    p.sendTitle(ChatColor.GREEN + warpName, "has been discovered", 6, 60, 12);
                                    p.spawnParticle(Particle.FIREWORKS_SPARK, particleLoc, 200);
                                    p.playSound(p, Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MUSIC, 100, 1);
                                }
                                updatePlayerFile(warpName, warpList, new File(playerDir + playerUUID + ".yml"));
                            }
                        } catch (Exception e) {
                            getLogger().log(Level.WARNING, e.toString());
                        }
                    }
                }
            }

        } else if (sender instanceof Player p) {
            p.sendMessage(ChatColor.YELLOW + "Please use /unlockCommand instead.");

        } else if (sender instanceof ConsoleCommandSender) {
            getLogger().log(Level.INFO, "This command can't be used in the console.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> optoins = new ArrayList<>();
        return optoins;
    }

    void updatePlayerFile(String warpName, List<String> warpList, File playerFile) {
        System.out.println(warpName);
        System.out.println(warpList);
        StringBuilder data = new StringBuilder(warpName);
        for (String s : warpList) {
            data.append(",");
            data.append(s);
        }
        try {
            FileWriter writer = new FileWriter(playerFile);
            writer.write(data.toString());
            writer.close();
        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }
    }
}

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
    private static final String currentDir = System.getProperty("user.dir");
    private static final String playerDir = currentDir + "\\plugins\\QuickWarp\\playerData\\";
    private static final String worldDir = currentDir + "\\plugins\\QuickWarp\\worldData\\";

    //TODO Add feature that allows user to unlock a warp point for a player.
    // /<command> <world> <warpPoint> <player>
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof BlockCommandSender block) {
            if (args.length == 3) {
                //Gets the command block location
                Location blockLoc = block.getBlock().getLocation();
                int blockX = blockLoc.getBlockX();
                int blockY = blockLoc.getBlockY();
                int blockZ = blockLoc.getBlockZ();

                //Gets the nearby entities in bbox
                String warpName = args[0].replace("_", " ");
                int range = Integer.parseInt(args[1]);
                boolean secret = Boolean.parseBoolean(args[2]);

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
                                StringBuilder data = new StringBuilder(warpName);
                                for (String d : warpList) {
                                    data.append(",");
                                    data.append(d);
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
            }

        } else if (sender instanceof Player p) {
            if (args.length == 3) {
                String worldName = args[0].replace("_", " ");
                String warpPointName = args[1].replace("_", " ");
                String playerName = args[2];

                //Gets all players weather online or offline.
                List<String> playersList = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    playersList.add(player.getDisplayName());
                }

                //Check if the world exists.
                if (playersList.contains(playerName)) {
                    File[] worldList = new File(worldDir).listFiles();
                    if (worldList != null) {
                        for (File f : worldList) {
                            if(f.getName().startsWith(worldName)){
                                List<String> warpList = Arrays.asList(Objects.requireNonNull(new File(worldDir + "").list()));
                                Player unlockPlayer = Bukkit.getPlayer(playerName);
                                if(unlockPlayer != null){

                                    unlockPlayer.sendMessage(ChatColor.GREEN + p.getDisplayName() + " unlocked " + warpPointName + " for you.");
                                    Location unlockPlayerLocation = unlockPlayer.getLocation();
                                    unlockPlayer.sendTitle(ChatColor.GREEN + warpPointName, "has been discovered", 6, 60, 12);
                                    unlockPlayer.spawnParticle(Particle.FIREWORKS_SPARK, unlockPlayerLocation, 200);
                                    unlockPlayer.playSound(p, Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MUSIC, 100, 1);
                                    p.sendMessage(ChatColor.GREEN + "Warp point unlocked.");
                                }
                            }
                        }
                        p.sendMessage(ChatColor.YELLOW + "That world does not exist.");
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "That player does not exist, or is not online.");
                }


            } else {
                p.sendMessage(ChatColor.YELLOW + "Please check your arguments.");
                p.sendMessage("/discoverWarp <world> <warpPoint> <player>");
            }
        } else if (sender instanceof ConsoleCommandSender) {
            getLogger().log(Level.INFO, "This command can't be used in the console.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> options = new ArrayList<>();
        if (args.length == 1) {
            options.add("<world>");
        }
        if (args.length == 2) {
            options.add("<warpPoint>");
        }
        if (args.length == 3) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                options.add(p.getDisplayName());
            }
        }
        return options;
    }
}

package co.xenocraft.commands;

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
    private static final String currentDir = System.getProperty("user.dir");
    private static final String playerDir = currentDir + "\\plugins\\QuickWarp\\playerData\\";
    private static final String worldDir = currentDir + "\\plugins\\QuickWarp\\worldData\\";

    //TODO Add feature that allows user to unlock or lock a warp point for a player.
    // /<command> <world> <warpPoint> <player> <unlock/lock>
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
                                    p.sendTitle(ChatColor.MAGIC + warpName, ChatColor.MAGIC + "has been discovered", 8, 80, 14);
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
            if (args.length == 4) {
                String worldName = args[0].replace("_", " ");
                String warpPointName = args[1].replace("_", " ");
                String playerName = args[2];
                String unlockOption = args[3];

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
                            if (f.getName().startsWith(worldName)) {
                                List<String> warpList = Arrays.asList(Objects.requireNonNull(new File(worldDir + f.getName()).list()));
                                Player unlockPlayer = Bukkit.getPlayer(playerName);
                                if (unlockPlayer != null) {
                                    UUID playerUUID = unlockPlayer.getUniqueId();
                                    List<String> playerWarpList = fileSystem.getPlayerWarps(playerUUID);
                                    if (unlockOption.equals("unlock")) {
                                        for (String pw : playerWarpList) {
                                            if (warpList.contains(pw)) {
                                                p.sendMessage(ChatColor.YELLOW + playerName + " already has that warp point unlocked.");
                                                return true;
                                            }
                                        }
                                        unlockPlayer.sendMessage(ChatColor.GREEN + p.getDisplayName() + " unlocked " + warpPointName + " for you.");
                                        Location unlockPlayerLocation = unlockPlayer.getLocation();
                                        unlockPlayer.sendTitle(ChatColor.GREEN + warpPointName, "has been discovered", 6, 60, 12);
                                        unlockPlayer.spawnParticle(Particle.FIREWORKS_SPARK, unlockPlayerLocation, 200);
                                        unlockPlayer.playSound(p, Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MUSIC, 100, 1);
                                        p.sendMessage(ChatColor.GREEN + "Warp point unlocked.");
                                        updatePlayerFile(warpPointName, warpList, new File(playerDir + playerUUID + ".yml"));
                                    } else if (unlockOption.equals("lock")) {
                                        for (int i = 0; i < playerWarpList.size(); i++) {
                                            if (warpList.contains(playerWarpList.get(i))) {
                                                playerWarpList.remove(i);
                                                unlockPlayer.sendMessage(ChatColor.RED + p.getDisplayName() + " has removed " + warpPointName + " from you.");
                                            }
                                        }
                                        StringBuilder dataBuilder = new StringBuilder(playerWarpList.get(0));
                                        for (int i = 1; i < playerWarpList.size(); i++) {
                                            dataBuilder.append(",");
                                            dataBuilder.append(playerWarpList.get(i));
                                        }
                                        try {
                                            FileWriter writer = new FileWriter(playerDir + playerUUID + ".yml");
                                            writer.write(dataBuilder.toString());
                                            writer.close();
                                        } catch (Exception e) {
                                            getLogger().log(Level.WARNING, e.toString());
                                        }
                                        p.sendMessage(ChatColor.GREEN + "Warp locked.");
                                    }
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
        switch (args.length){
            case 1:
                options.add("<world>");
                break;
            case 2:
                options.add("<warpPoint>");
                break;
            case 3:
                for (Player p : Bukkit.getOnlinePlayers()) {
                    options.add(p.getDisplayName());
                }
                break;
            case 4:
                options.add("lock");
                options.add("unlock");
        }
        return options;
    }

    public void updatePlayerFile(String warpName, List<String> warpList, File playerFile) {
        StringBuilder data = new StringBuilder(warpName);
        for (String d : warpList) {
            data.append(",");
            data.append(d);
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

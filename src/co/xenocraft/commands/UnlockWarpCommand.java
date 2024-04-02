package co.xenocraft.commands;

import co.xenocraft.fileSystem;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;

public class UnlockWarpCommand implements TabExecutor {
    // TODO allows user to unlock a warp point for all players or remove a warp point for unlocked list.
    // /<command> <world> <warpPoint> <player> <unlock/lock>

    private static final String currentDir = System.getProperty("user.dir");
    private static final String playerDir = currentDir + "\\plugins\\QuickWarp\\playerData\\";
    private static final String worldDir = currentDir + "\\plugins\\QuickWarp\\worldData\\";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player p) {


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
                        List<String> worldNameList = new ArrayList<>();
                        for (File f : worldList) {
                            worldNameList.add(f.getName().split("=")[0]);
                        }
                        if (worldNameList.contains(worldName)) {
                            Player unlockPlayer = Bukkit.getPlayer(playerName);
                            if (unlockPlayer != null) {
                                UUID playerUUID = unlockPlayer.getUniqueId();
                                List<String> playerWarpList = fileSystem.getPlayerWarps(playerUUID);
                                if (unlockOption.equals("unlock")) {
                                    for (String pw : playerWarpList) {
                                        if (pw.equals(warpPointName)) {
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
                                    updatePlayerFile(warpPointName, playerWarpList, new File(playerDir + playerUUID + ".yml"));
                                } else if (unlockOption.equals("lock")) {
                                    System.out.println(playerWarpList);
                                    for (int i = 0; i < playerWarpList.size(); i++) {
                                        if (playerWarpList.get(i).equals(warpPointName)) {
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
                        } else {
                            p.sendMessage(ChatColor.RED + "That world does not exist.");
                        }
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "That player is not online, or does not exist.");
                }
            } else {
                p.sendMessage(ChatColor.YELLOW + "Please check your arguments.");
                p.sendMessage("/discoverWarp <world> <warpPoint> <player> <unlock/lock>");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> options = new ArrayList<>();
        switch (args.length) {
            case 1:
                for (String s : fileSystem.getWorldList()) {
                    options.add(s.replace(" ", "_"));
                }
                break;
            case 2:
                options.addAll(fileSystem.getWarpList(args[0]));
                break;
            case 3:
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getDisplayName().startsWith(args[2])) {
                        options.add(p.getDisplayName());
                    }

                }
                break;
            case 4:
                options.add("lock");
                options.add("unlock");
        }
        return options;
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

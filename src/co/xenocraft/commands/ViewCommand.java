package co.xenocraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;


public class ViewCommand implements TabExecutor {
    private final String worldDir = System.getProperty("user.dir") + "\\plugins\\QuickWarp\\worldData\\";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player p) {
            if (args.length == 1) {
                args[0].toLowerCase();
                if (args[0].equals("world")) {
                    List<String> worldFileList;
                    try {
                        File worldFiles = new File(worldDir);
                        worldFileList = Arrays.asList(Objects.requireNonNull(worldFiles.list()));
                        for (String s : worldFileList) {
                            String[] worldParts = s.split("=");
                            p.sendMessage("World Name: " + worldParts[0].trim() + " UUID: " + UUID.fromString(worldParts[1].trim()));
                            File warpLengthFile = new File(worldDir + s);
                            p.sendMessage("Number of warps: " + (Objects.requireNonNull(warpLengthFile.listFiles()).length - 1));
                        }
                    } catch (Exception e) {
                        getLogger().log(Level.WARNING, e.toString());
                    }
                } else if (args[0].equals("warps")) {
                    String worldUUID = p.getWorld().getUID().toString();
                    List<String> worldFileList;
                    List<File> warpFileList;
                    try {
                        File worldFiles = new File(worldDir);
                        worldFileList = Arrays.asList(Objects.requireNonNull(worldFiles.list()));
                        for (String f : worldFileList) {
                            String[] worldParts = f.split("=");
                            if (worldUUID.equals(worldParts[1].trim())) {
                                File warpFiles = new File(worldDir + f);
                                warpFileList = Arrays.asList(Objects.requireNonNull(warpFiles.listFiles()));
                                p.sendMessage("World Name: " + worldParts[0].trim());
                                p.sendMessage("Warps: ");
                                for (File s : warpFileList) {
                                    String[] warpNameParts = s.getName().split("\\.");
                                    if (!warpNameParts[0].trim().equals("worldData")) {
                                        System.out.println(s.getName());
                                        Scanner fileReader = new Scanner(s).useDelimiter(",");
                                        List<String> dataList = new ArrayList<>();
                                        while (fileReader.hasNext()) {
                                            dataList.add(fileReader.next());
                                        }
                                        fileReader.close();
                                        p.sendMessage("Name: " + warpNameParts[0].trim());
                                        p.sendMessage("    Location: X: " + dataList.get(0) + ", Y: " + dataList.get(1) + ", Z: " + dataList.get(2));
                                    }

                                }
                            }
                        }
                    } catch (Exception e) {
                        getLogger().log(Level.WARNING, e.toString());
                    }
                } else {
                    argsMessage(p);
                }
            } else {
                argsMessage(p);
            }
        }
        return true;
    }

    private void argsMessage(Player p) {
        p.sendMessage(ChatColor.YELLOW + "Please state weather you want to view the different worlds, " + "or the warps in this world.");
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

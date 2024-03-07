package co.xenocraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;

public class CreateWarpCommand implements TabExecutor {
    //Gets cardinal direction of user to make teleport look nice.
    public static int getCardinalDirection(float deg) {
        int normalDeg = (int) ((deg + 360) % 360);
        int sector = (normalDeg + 45) / 90;
        return switch (sector % 4) {
            case 1 -> 90;
            case 2 -> 180;
            case 3 -> -90;
            default -> 0;
        };
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player p) {
            //Checks for all needed args
            if (args.length == 0) {
                p.sendMessage("Please provide arguments. /createwarp <name> <range> <secret>");
            } else if (args.length == 3) {
                if (checkWorldFolder(p.getWorld().getUID())) {
                    //Pulls name, range, and secret from arguments.
                    String locName = args[0].replace("_", " ");
                    String rangeString = args[1];
                    int range = Integer.parseInt(rangeString);
                    boolean secret = Boolean.parseBoolean(args[2]);
                    //Gets the current location of sender.
                    Location playerLoc = p.getLocation();
                    if (addWarpPoint(p.getWorld().getUID(), p, locName, playerLoc)) {
                        p.sendMessage("Name: " + locName);
                        p.sendMessage("Secret: " + secret);
                        p.sendMessage("Range: " + range);

                        int blockX = playerLoc.getBlockX();
                        int blockY = playerLoc.getBlockY();
                        int blockZ = playerLoc.getBlockZ();

                        //Works out the placement of the blocks.
                        Location repeatBlockLoc = new Location(p.getWorld(), blockX, blockY - 2, blockZ);
                        Location chainBlockLoc = new Location(p.getWorld(), blockX, blockY - 2, blockZ - 1);
                        Location redstoneRepeatLoc = new Location(p.getWorld(), blockX, blockY - 3, blockZ);

                        //Generates the command of command block.
                        String repeatCommand = "execute if entity @a[x=" + (blockX - (range / 2)) + ", y=" + blockY + ", z=" + (blockZ - (range / 2)) +
                                ", dx=" + range + ", dy=3, dz=" + range + "]";
                        String chainCommand = "discoverwarp";
                        p.sendMessage("Placing a warp point at: X: " + repeatBlockLoc.getBlockX() + ", Y: " + repeatBlockLoc.getBlockY() + ", Z: " + repeatBlockLoc.getBlockZ());

                        //Sets up the repeating command block with name, command, and a redstone block.
                        repeatBlockLoc.getBlock().setType(Material.REPEATING_COMMAND_BLOCK);
                        CommandBlock repeatBlock = (CommandBlock) repeatBlockLoc.getBlock().getState();
                        repeatBlock.setCommand(repeatCommand);
                        repeatBlock.update();
                        redstoneRepeatLoc.getBlock().setType(Material.REDSTONE_BLOCK);

                        //Sets up the chain command block with command, and conditional value.
                        chainBlockLoc.getBlock().setType(Material.CHAIN_COMMAND_BLOCK);
                        CommandBlock chainBlock = (CommandBlock) chainBlockLoc.getBlock().getState();
                        BlockData newData = Material.CHAIN_COMMAND_BLOCK.createBlockData("[conditional=true,facing=north]");
                        chainBlock.setBlockData(newData);
                        chainBlock.setCommand(chainCommand);
                        chainBlock.update();

                        //Set the name of chain command block.
                        if (secret) {
                            chainBlock.setName("1=" + locName + "=" + range);
                        } else {
                            chainBlock.setName("0=" + locName + "=" + range);
                        }
                        chainBlock.update();
                        p.sendMessage(ChatColor.GREEN + "Warp Point successfully created.");
                    } else {
                        p.sendMessage(ChatColor.RED + "A warp point in this world already has that name.");
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "This world does not have a name yet.");
                    p.sendMessage(ChatColor.YELLOW + "Please use /createWorldName first.");
                }

            } else {
                p.sendMessage("Please provide arguments. /createwarp <name> <range> <secret>");
                p.sendMessage("Please use _ for spaces in warp point name.");
            }

        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 3) {
            List<String> options = new ArrayList<>();
            options.add("true");
            options.add("false");
            return options;
        }
        return null;
    }

    //Checks if the world folder exists.
    public boolean checkWorldFolder(UUID worldUUID) {
        String UUIDString = worldUUID.toString();
        String fileDir = System.getProperty("user.dir") + "\\plugins\\QuickWarp\\worldData\\";
        try {
            File dirList = new File(fileDir);
            String[] worldDirList = dirList.list();
            if (Objects.requireNonNull(worldDirList).length != 0) {
                for (String s : worldDirList) {
                    String[] nameParts = s.split("=");
                    String UUIDParts = nameParts[1].trim();
                    if (UUIDParts.equals(UUIDString)) {
                        return true;
                    }
                }
            } else {
                return false;
            }

        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }
        return false;
    }

    //Creates a file in the world folder with the warp point data.
    public boolean addWarpPoint(UUID worldUUID, Player p, String warpName, Location loc) {
        String UUIDString = worldUUID.toString();
        String fileDir = System.getProperty("user.dir") + "\\plugins\\QuickWarp\\worldData\\";
        Material warpMaterial = Material.GRASS_BLOCK;
        try {
            File dirList = new File(fileDir);
            String[] worldDirList = dirList.list();
            for (String s : Objects.requireNonNull(worldDirList)) {
                File checkWarpDir = new File(fileDir + "\\" + s);
                String[] checkWarpList = checkWarpDir.list();
                for (String w : Objects.requireNonNull(checkWarpList)) {
                    if (w.equals(warpName + ".yml")) {
                        return false;
                    }
                }
                if (s.endsWith(UUIDString)) {
                    int order = checkWarpList.length;
                    int blockX = loc.getBlockX();
                    int blockY = loc.getBlockY();
                    int blockZ = loc.getBlockZ();
                    int pitch = 0;
                    int yaw = getCardinalDirection(p.getLocation().getYaw());
                    FileWriter warpFile = new FileWriter(fileDir + s + "\\" + warpName + ".yml");
                    String dataString = blockX + "," + blockY + "," + blockZ + "," +
                            pitch + "," + yaw + "," + warpMaterial + "," + order;
                    try {
                        warpFile.write(dataString);
                        warpFile.close();
                        return true;
                    } catch (Exception e) {
                        getLogger().log(Level.WARNING, e.toString());
                        return false;
                    }
                }
            }


        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
            return false;
        }
        return false;
    }
}

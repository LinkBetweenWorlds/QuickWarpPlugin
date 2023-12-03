package co.xenocraft.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CreateWarpCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            //Checks for all needed args
            if (args.length == 0) {
                p.sendMessage("Please provide arguments. /createwarp <name> <range> <secret>");
            } else if (args.length == 3) {
                //Pulls name and discover zone from args
                String locName = args[0];
                String rangeString = args[1];
                int range = Integer.parseInt(rangeString);
                boolean secret = Boolean.parseBoolean(args[1].toLowerCase());
                p.sendMessage("Location Name: " + locName);
                p.sendMessage("Secret: " + secret);

                //Gets the current loc of sender
                Location playerLoc = p.getLocation();
                int blockX = playerLoc.getBlockX();
                int blockY = playerLoc.getBlockY();
                int blockZ = playerLoc.getBlockZ();
                Location glassLoc = new Location(p.getWorld(), blockX, blockY - 1, blockZ);
                glassLoc.getBlock().setType(Material.GLASS);

                //Works out the placement of the blocks
                Location repeatBlockLoc = new Location(p.getWorld(), blockX, blockY - 2, blockZ);
                Location chainBlockLoc = new Location(p.getWorld(), blockX, blockY - 2, blockZ - 1);
                Location redstoneRepeatLoc = new Location(p.getWorld(), blockX, blockY - 3, blockZ);

                //Generates the command of command block
                String repeatCommand = "execute if entity @a[x=" + (blockX - (range / 2)) + ", y=" + blockY + ", z=" + (blockZ - (range / 2)) +
                        ", dx=" + range + ", dy=3, dz=" + range + "]";
                String chainCommand = "discoverwarp @e[limit=1, sort=nearest]";
                p.sendMessage("Placing a Warp point at: X: " + repeatBlockLoc.getBlockX() + ", Y: " + repeatBlockLoc.getBlockY() + ", Z: " + repeatBlockLoc.getBlockZ());

                //Sets up the command block with name, command, and a redstone block
                repeatBlockLoc.getBlock().setType(Material.REPEATING_COMMAND_BLOCK);
                CommandBlock repeatBlock = (CommandBlock) repeatBlockLoc.getBlock().getState();
                repeatBlock.setCommand(repeatCommand);
                repeatBlock.update();
                redstoneRepeatLoc.getBlock().setType(Material.REDSTONE_BLOCK);

                chainBlockLoc.getBlock().setType(Material.CHAIN_COMMAND_BLOCK);
                CommandBlock chainBlock = (CommandBlock) chainBlockLoc.getBlock().getState();
                chainBlock.setCommand(chainCommand);
                if (secret) {
                    chainBlock.setName("1_" + locName);
                } else {
                    chainBlock.setName("0_" + locName);
                }
                chainBlock.update();
                p.sendMessage("Warp Point successfully created.");
            } else {
                p.sendMessage("Please provide arguments. /createwarp <name> <range> <secret>");
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
}

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

public class CreateWarpCommand implements TabExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player p = (Player) sender;
            if (args.length == 0){
                p.sendMessage("Please provide arguments. /createwarp <name> <range> <secret>");
            }else if (args.length == 3){
                String locName = args[0];
                String rangeString = args[1];
                int range = Integer.parseInt(rangeString);
                boolean secret = Boolean.parseBoolean(args[1].toLowerCase());
                p.sendMessage("Location Name: " + locName);
                p.sendMessage("Secret: " + secret);
                Location playerLoc = p.getLocation();
                int blockX = playerLoc.getBlockX();
                int blockY = playerLoc.getBlockY();
                int blockZ = playerLoc.getBlockZ();
                Location glassLoc = new Location(p.getWorld(), blockX, blockY - 1, blockZ);
                glassLoc.getBlock().setType(Material.GLASS);
                Location commandBlockLoc = new Location(p.getWorld(), blockX, blockY - 2, blockZ);
                Location redstoneBlockLoc = new Location(p.getWorld(), blockX, blockY - 3, blockZ);
                String blockCommand = "execute if entity @a[x=" + (blockX - (range / 2)) + ", y=" + blockY + ", z=" + (blockZ - (range / 2)) +
                        ", dx=" + range + ", dy=3, dz=" + range + "] run discoverWarp @p";
                p.sendMessage("Placing a Warp point at: X: " + commandBlockLoc.getBlockX() + ", Y: " + commandBlockLoc.getBlockY() + ", Z: " + commandBlockLoc.getBlockZ());
                commandBlockLoc.getBlock().setType(Material.REPEATING_COMMAND_BLOCK);
                CommandBlock commandBlock = (CommandBlock) commandBlockLoc.getBlock().getState();
                commandBlock.setCommand(blockCommand);
                if (secret){
                    commandBlock.setName("1_"+locName);
                }else{
                    commandBlock.setName("0_"+locName);
                }
                commandBlock.update();
                redstoneBlockLoc.getBlock().setType(Material.REDSTONE_BLOCK);
                p.sendMessage("Warp Point successfully created.");
            }else{
                p.sendMessage("Please provide arguments. /createwarp <name> <range> <secret>");
            }

        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {

        if(args.length == 3){
            List<String> options = new ArrayList<>();
            options.add("true");
            options.add("false");

            return options;
        }
        return null;
    }
}

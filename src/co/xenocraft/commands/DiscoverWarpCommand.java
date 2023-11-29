package co.xenocraft.commands;

import org.bukkit.block.CommandBlock;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;

public class DiscoverWarpCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof BlockCommandSender){
            //CommandBlock commandBlock = (CommandBlock) sender;
            System.out.println("Hello");
            //String nameBlock = commandBlock.getName();
            //System.out.println("Block Name: " + nameBlock + "\nPlayer: " + args[0]);
        }else if (sender instanceof Player){
            Player p = (Player) sender;
            p.sendMessage("This is a command block command for now.");
        }else if (sender instanceof ConsoleCommandSender){
            System.out.println("Test");
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        return null;
    }
}

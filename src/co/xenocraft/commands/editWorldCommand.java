package co.xenocraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

public class editWorldCommand implements TabExecutor {
    //TODO Allows the user to change the name of the world, or delete it.
    // /<command> <edit> <name>
    // /<command> <delete>
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}

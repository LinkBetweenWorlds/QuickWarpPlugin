package co.xenocraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.List;

public class UnlockWarpCommand implements TabExecutor {
    // TODO allows user to unlock a warp point for all players or remove a warp point for unlocked list.
    // /<command> <world> <warpPoint> <unlock/lock>
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}

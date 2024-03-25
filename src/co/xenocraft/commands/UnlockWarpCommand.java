package co.xenocraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class UnlockWarpCommand implements TabExecutor {
    // TODO allows user to unlock a warp point for all players or remove a warp point for unlocked list.
    // /<command> <world> <warpPoint> <unlock/lock>
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> options = new ArrayList<>();
        if(args.length == 1){
            options.add("<world>");
        }
        if(args.length == 2){
            options.add("<warp>");
        }
        if(args.length == 3){
            options.add("lock");
            options.add("unlock");
        }
        return options;
    }
}

package co.xenocraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GodCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (sender instanceof Player){
            Player p = (Player) sender;
            if (p.isInvulnerable()){
                p.setInvulnerable(false);
                p.sendMessage(ChatColor.RED + "You have fallen from Grace.");
            }
            else{
                p.setInvulnerable(true);
                p.setGameMode(GameMode.CREATIVE);
                for (int i = 0; i < 5; i++) {
                    p.getWorld().strikeLightningEffect(p.getLocation());
                }
                p.sendMessage(ChatColor.GREEN + "You are now God.");
            }
        }
        return true;
    }

}

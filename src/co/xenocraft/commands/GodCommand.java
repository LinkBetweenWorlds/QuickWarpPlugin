package co.xenocraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class GodCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player p) {
            if (p.isInvulnerable()) {
                p.setInvulnerable(false);
                p.setGameMode(GameMode.ADVENTURE);
                p.sendMessage(ChatColor.RED + "You have fallen from the light.");
            } else {
                p.setInvulnerable(true);
                p.setGameMode(GameMode.CREATIVE);
                for (int i = 0; i < 10; i++) p.getWorld().strikeLightningEffect(p.getLocation());
                p.sendMessage(ChatColor.GOLD + "You now have the power of a God.");
                p.sendMessage(ChatColor.GOLD + "How does it feel?");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        return null;
    }
}

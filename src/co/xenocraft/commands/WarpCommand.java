package co.xenocraft.commands;

import co.xenocraft.QuickWarp;
import co.xenocraft.menus.WarpMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class WarpCommand implements TabExecutor {

    //key = uuid of the player
    //long = the epoch time of when they run the command
    private final HashMap<UUID, Long> coolDown;

    public WarpCommand() {
        this.coolDown = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player p) {
            if (QuickWarp.warpCooldownEnable) {
                if (p.isOp()) {
                    WarpMenu.open(p);
                } else {
                    if (!this.coolDown.containsKey(p.getUniqueId())) {
                        this.coolDown.put(p.getUniqueId(), System.currentTimeMillis());
                        WarpMenu.open(p);
                    } else {
                        //Difference in milliseconds
                        long timeElapsed = System.currentTimeMillis() - coolDown.get(p.getUniqueId());
                        //Convert to seconds
                        int seconds = (int) ((timeElapsed / 1000) % 60);
                        int warpCooldown = QuickWarp.warpCooldown;
                        if (seconds >= warpCooldown) {
                            this.coolDown.put(p.getUniqueId(), System.currentTimeMillis());
                            WarpMenu.open(p);
                        } else {
                            p.sendMessage(ChatColor.YELLOW + "Please wait " + (warpCooldown - seconds) + " seconds before trying to warp again.");
                        }
                    }
                }
            } else {
                WarpMenu.open(p);
            }

        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        return null;
    }
}

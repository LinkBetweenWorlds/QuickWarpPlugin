package co.xenocraft.listeners;

import co.xenocraft.QuickWarp;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.FileWriter;
import java.security.interfaces.XECKey;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;

import static org.bukkit.Bukkit.*;

public class PlayerJoinLeaveListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        UUID playerID = p.getUniqueId();
        String dir = QuickWarp.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " ").split("QuickWarp.jar")[0];
        String fileDir =  dir + "/QuickWarp/playerData/";
        File playerDir = new File(fileDir);
        List<String> playerList = List.of(Objects.requireNonNull(playerDir.list()));

        try {
            if (!playerList.contains(playerID + ".yml")) {
                FileWriter file = new FileWriter(playerDir + "\\" + playerID + ".yml");
                String data = "";
                for (String s : playerList) {
                    if (!s.equals(playerID.toString())) {
                        file.write(data);
                        file.close();
                    }
                }
            }
        } catch (Exception e) {
            getLogger().log(Level.WARNING, e.toString());
        }
        if (QuickWarp.welcomeMessageEnable) {
            String joinMessage = QuickWarp.welocmeMessageString;
            if(joinMessage.contains("@player")){
                String[] messageParts = joinMessage.split("@player");
                String newJoinMessage = messageParts[0] + p.getDisplayName() + messageParts[1];
                event.setJoinMessage(newJoinMessage);
            }else{
                event.setJoinMessage(joinMessage);
            }

        }

    }
}

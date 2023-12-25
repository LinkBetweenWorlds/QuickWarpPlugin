package co.xenocraft.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.FileWriter;
import java.util.UUID;

public class PlayerJoinLeaveListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        event.setJoinMessage("Welcome " + p.getDisplayName() + " to §4Xenocraft!");
        UUID playerID = p.getUniqueId();
        String fileDir = System.getProperty("user.dir") + "\\plugins\\QuickWarp\\playerData\\";
        File playerDir = new File(fileDir);
        String[] playerList = playerDir.list();
        if (playerList != null) {
            try {
                FileWriter file = new FileWriter(playerDir + "\\" + playerID.toString() + ".yml");
                String data = "";
                if (playerList.length != 0) {
                    for (String s : playerList) {
                        if (!s.equals(playerID.toString())) {
                            file.write(data);
                            file.close();
                        }
                    }
                } else {
                    file.write(data);
                    file.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}

package co.xenocraft.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;

public class PlayerJoinLeaveListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        event.setJoinMessage("Welcome " + p.getDisplayName() + " to §4Xenocraft!");
        UUID playerID = p.getUniqueId();
        String fileDir = System.getProperty("user.dir") + "\\plugins\\QuickWarp\\playerData\\";
        File playerDir = new File(fileDir);
        List<String> playerList = List.of(Objects.requireNonNull(playerDir.list()));
        if (!playerList.isEmpty()) {
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
        }
    }
}

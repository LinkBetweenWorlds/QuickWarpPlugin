package co.xenocraft;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;

public class fileSystem {
    private static final String currentDir = System.getProperty("user.dir");
    private static final String playerDir = currentDir + "\\plugins\\QuickWarp\\playerData\\";
    private static final String worldDir = currentDir + "\\plugins\\QuickWarp\\worldData\\";
    public static List<String> getPlayerWarps (UUID playerUUID) {
        List<String> playerWarps = new ArrayList<>();

        try{
            File file = new File(playerDir + playerUUID + ".yml");
            Scanner fileReader = new Scanner(file).useDelimiter(",");
            while (fileReader.hasNext()) {
                playerWarps.add(fileReader.next());
            }
        } catch(Exception e){
            getLogger().log(Level.WARNING, e.toString());
        }


        return playerWarps;
    }
}

package co.xenocraft;

import java.io.File;
import java.util.*;
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

    public static List<String> getWorldList(){
        List<String> worldList = new ArrayList<>();
        try{
            File[] worldFileList = new File(worldDir).listFiles();
            if(worldFileList != null){
                for(File f : worldFileList){
                    worldList.add(f.getName().split("=")[0]);
                }
            }
        } catch (Exception e){
            getLogger().log(Level.WARNING, e.toString());
        }
        return worldList;
    }

    public static List<String> getWarpList(String worldName){
        List<String> warpList = new ArrayList<>();
        try{
            File[] worldFileList = new File(worldDir).listFiles();
            if(worldFileList != null){
                for(File f : worldFileList){
                    if(worldName.equals(f.getName().split("=")[0])){
                        File[] warpFileList = new File(worldDir + f.getName()).listFiles();
                        if(warpFileList != null){
                            for(File wf : warpFileList){
                                if(!wf.getName().startsWith("worldData")){
                                    warpList.add(wf.getName().split("\\.")[0].replace(" ", "_"));
                                }

                            }
                        }

                    }
                }
            }
        }catch (Exception e){
            getLogger().log(Level.WARNING, e.toString());
        }
        return warpList;
    }
}

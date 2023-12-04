package co.xenocraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CreateWorldNameCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player p) {
            if (args.length >= 1) {
                UUID worldUUID = p.getWorld().getUID();
                String worldName = args[0];
                for (int i = 1; i < args.length; i++) {
                    worldName += " ";
                    worldName += args[i];
                }
                HashMap<UUID, String> UUIDData = readUUIDData();
                HashMap<UUID, HashMap> worldData = readWorldData();
                HashMap warpData = worldData.get(worldUUID);

            } else {
                p.sendMessage(ChatColor.YELLOW + "Please input the name name of this world.");
                p.sendMessage("/createWorldName <name>");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        return null;
    }

    String currentDir = System.getProperty("user.dir");
    String fileDir = currentDir + "\\plugins\\QucikWarp";

    public HashMap<UUID, String> readUUIDData() {
        HashMap<UUID, String> UUIDData = new HashMap<>();
        String dataDir = fileDir + "\\UUID.yml";
        BufferedReader br = null;
        try {
            File file = new File(dataDir);
            br = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                String worldUUID = parts[0].trim();
                String worldName = parts[1].trim();
                if (!worldUUID.equals("") && !worldName.equals("")) {
                    UUIDData.put(UUID.fromString(worldUUID), worldName);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return UUIDData;
    }

    public void writeUUIDData(HashMap data) {
        HashMap<UUID, String> UUIDData = data;
        File file = new File(fileDir + "\\UUID.yml");
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            for (Map.Entry<UUID, String> entry : UUIDData.entrySet()) {
                bw.write(entry.getKey() + ":" + entry.getValue());
                bw.newLine();
            }
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public HashMap<UUID, HashMap> readWorldData() {
        HashMap<UUID, HashMap> worldData = new HashMap<>();
        HashMap<String, Location> warpData = new HashMap<>();

        return worldData;
    }

    public void writeWorldData(HashMap data, UUID id) {
        HashMap<UUID, HashMap> worldData = data;
        HashMap<String, Location> warpData = worldData.get(id);
        File file = new File(fileDir + "\\warp.yml");

    }
}


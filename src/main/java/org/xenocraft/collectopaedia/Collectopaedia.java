package org.xenocraft.collectopaedia;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.xenocraft.collectopaedia.commands.OpenMenuCommand;
import org.xenocraft.collectopaedia.listener.PlayerJoinLeaveListener;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public final class Collectopaedia extends JavaPlugin implements Listener {

    public FileConfiguration areasData;
    public FileConfiguration itemsData;
    public FileConfiguration rewardsData;

    @Override
    public void onEnable() {
        createDataFiles();

        registerEvent();
        registerCommands();

        getLogger().log(Level.INFO, "[Collectopaedia] Enabled successfully.");
        getLogger().log(Level.INFO, "[Collectopaedia] My code works??? How??");
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "[Collectopaedia] Disabled successfully.");
    }

    //Register Listener
    public void registerEvent() {
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new PlayerJoinLeaveListener(this), this);
    }

    //Register Commands
    public void registerCommands() {
        Objects.requireNonNull(getCommand("open")).setExecutor(new OpenMenuCommand(this));
    }

    //Create new player file if one does not exist.
    public void createPlayerFile(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            String uuid = player.getUniqueId().toString();
            File playerDataFolder = new File(getDataFolder(), "playerData");
            // Ensure the playerData folder exists
            if (!playerDataFolder.exists() && !playerDataFolder.mkdirs()) {
                getLogger().log(Level.WARNING, "[Collectopaedia] Could not create player data folder.");
                return;
            }
            File file = new File(playerDataFolder, uuid + ".yml");

            if (!file.exists()) {
                try {
                    // Create the file
                    if (file.createNewFile()) {
                        FileConfiguration playerFile = YamlConfiguration.loadConfiguration(file);

                        //TODO
                        // Add count of item per area that player has deposited.
                        // Add list of rewards that player has collected.

                        // Fill the file with default data
                        playerFile.set("name", player.getName());
                        playerFile.set("selectedArea", "colony9");
                        playerFile.set("selectedPage", 0);
                        List<String> unlockList = List.of("other", "colony9");
                        List<String> list = List.of("other", "colony9", "tephraCave",
                                "bionisLeg", "colony6");
                        playerFile.set("unlockedAreas", unlockList);
                        playerFile.set("depositedItems", list);
                        for (String area : list) {
                            playerFile.set("depositedItems." + area + ".count", 0);
                        }
                        playerFile.set("rewards", "colony9");


                        // Save the data to the fileB
                        savePlayerFile(playerFile, player);

                        getLogger().info("[Collectopaedia] Player file created for " + player.getName());
                    }
                } catch (IOException e) {
                    getLogger().log(Level.WARNING, "[Collectopaedia] Could not create or save player file: " + e);
                }
            } else {
                getLogger().info("[Collectopaedia] Player file already exists for " + player.getName());
            }
        });
    }

    public boolean playerFileExists(Player p) {
        return new File(getDataFolder() + "/playerData", p.getUniqueId() + ".yml").exists();
    }

    // Save the player data file asynchronously
    public void savePlayerFile(FileConfiguration file, Player p) {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                file.save(new File(getDataFolder() + "/playerData", p.getUniqueId() + ".yml"));
            } catch (IOException e) {
                getLogger().log(Level.WARNING, "[Collectopaedia] Could not save player file for " + p.getName() + ": " + e);
            }
        });
    }

    public FileConfiguration loadPlayerData(Player p) {
        File file = new File(getDataFolder() + "/playerData", p.getUniqueId() + ".yml");
        if (!file.exists()) {
            createPlayerFile(p);
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    // Create all required data files
    private void createDataFiles() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            saveResource("items.yml", true);
            saveResource("rewards.yml", true);
            saveResource("areas.yml", true);
            itemsData = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "items.yml"));
            areasData = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "areas.yml"));
            rewardsData = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "rewards.yml"));
        });
    }
}

package org.xenocraft.collectopaedia.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.xenocraft.collectopaedia.Collectopaedia;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class OpenMenuCommand implements TabExecutor {

    private final Collectopaedia collectopaedia;

    public OpenMenuCommand(Collectopaedia collectopaedia) {
        this.collectopaedia = collectopaedia;
    }

    private static final ItemStack INFILL = createStaticItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, " ", true);
    private static final ItemStack BACK_BUTTON = createStaticItemStack(Material.BARRIER, ChatColor.RED + "Back", false);
    private static final ItemStack LOCKED_AREA = createStaticItemStack(Material.RED_STAINED_GLASS_PANE, ChatColor.DARK_RED + "Locked Area", false);
    private static final ItemStack NEXT_PAGE = createStaticItemStack(Material.PAPER, ChatColor.GREEN + "Next", false);

    private final ConcurrentHashMap<UUID, FileConfiguration> playerDataCache = new ConcurrentHashMap<>();

    // Helper method to create a static ItemStack with a specific material and display name
    private static ItemStack createStaticItemStack(Material material, String displayName, boolean hideToolTip) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        Objects.requireNonNull(meta).setHideTooltip(hideToolTip);
        Objects.requireNonNull(meta).setDisplayName(displayName);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof ConsoleCommandSender console) {
            console.sendMessage(ChatColor.RED + "[Collectopaedia] This command can only be executed by players");
            return true;
        } else if (sender instanceof Player player) {
            createInventory(player);
            return true;
        }
        return false;
    }

    public void createInventory(Player player) {
        Bukkit.getScheduler().runTask(collectopaedia, () -> {
            int rows = 6;
            int cols = 9;
            int invSize = rows * cols;

            Inventory inventory = Bukkit.createInventory(null, invSize, ChatColor.DARK_GREEN + "Collectopaedia");

            for (int slot = 0; slot < invSize; slot++) {
                // Fill lockedArea for border slots
                if (slot < 9 || slot % 9 == 8 || slot >= 45) {
                    inventory.setItem(slot, LOCKED_AREA);
                } else {
                    // Fill infill for all other slots
                    inventory.setItem(slot, INFILL);
                }
            }
            inventory.setItem(36, NEXT_PAGE);
            inventory.setItem(45, BACK_BUTTON);

            Inventory playerInv = updateInventory(player, inventory);

            player.openInventory(playerInv);
        });
    }

    public Inventory updateInventory(Player player, Inventory inventory) {
        Bukkit.getScheduler().runTask(collectopaedia, () -> {
            FileConfiguration playerData = getPlayerData(player);
            List<String> unlockedAreas = playerData.getStringList("unlockedAreas");
            String selectedArea = playerData.getString("selectedArea");
            Bukkit.getLogger().log(Level.INFO, "[Collectopaedia] Unlocked Areas: " + unlockedAreas);


            int rows = 6;
            int cols = 9;
            int invSize = rows * cols;
            int slot = 0;
            for (String area : unlockedAreas) {
                if (slot < 9 || slot % 9 == 8 || slot >= 45) {
                    ItemStack areaMaps;
                    if (area.equals(selectedArea)) {
                        areaMaps = createAreaMapItem(area, true);
                    } else {
                        areaMaps = createAreaMapItem(area, false);
                    }
                    inventory.setItem(slot, areaMaps);
                }
                slot++;
            }
            inventory.setItem(9, createPercentMeter(playerData, selectedArea));

        });
        return inventory;
    }

    private FileConfiguration getPlayerData(Player p) {
        return playerDataCache.computeIfAbsent(p.getUniqueId(), id -> collectopaedia.loadPlayerData(p));
    }

    private ItemStack createAreaMapItem(String area, boolean selected) {
        String areaDisplayName = collectopaedia.areasData.getString(area);
        Bukkit.getLogger().log(Level.INFO, "[Collectopaedia] " + areaDisplayName);
        ItemStack item = new ItemStack(Material.PAPER);
        if (selected) {
            item.setType(Material.MAP);
        }
        ItemMeta meta = item.getItemMeta();
        Objects.requireNonNull(meta).setDisplayName(ChatColor.WHITE + areaDisplayName);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createPercentMeter(FileConfiguration playerFile, String area) {
        ItemStack item = new ItemStack(Material.CLOCK);
        ItemMeta meta = item.getItemMeta();
        int itemCount = collectopaedia.itemsData.getInt(area + ".count");
        int playerItemCount = playerFile.getInt("depositedItems." + area + ".count");
        int percent = playerItemCount / itemCount;
        String areaDisplayName = collectopaedia.areasData.getString(area);
        Objects.requireNonNull(meta).setDisplayName(ChatColor.GOLD + areaDisplayName + " : " + percent + "%");
        item.setItemMeta(meta);
        return item;
    }


    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return List.of();
    }
}

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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.xenocraft.collectopaedia.Collectopaedia;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class OpenMenuCommand implements TabExecutor {

    private static final ItemStack INFILL = createStaticItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, " ", true);
    private static final ItemStack BACK_BUTTON = createStaticItemStack(Material.BARRIER, ChatColor.RED + "Back", false);
    private static final ItemStack LOCKED_AREA = createStaticItemStack(Material.RED_STAINED_GLASS_PANE, ChatColor.DARK_RED + "Locked Area", false);
    private static final ItemStack NEXT_PAGE = createStaticItemStack(Material.PAPER, ChatColor.GREEN + "Next Page", false);
    private final Collectopaedia collectopaedia;
    private final ConcurrentHashMap<UUID, FileConfiguration> playerDataCache = new ConcurrentHashMap<>();

    public OpenMenuCommand(Collectopaedia collectopaedia) {
        this.collectopaedia = collectopaedia;
    }

    // Helper method to create a static ItemStack with a specific material and display name
    private static ItemStack createStaticItemStack(Material material, String displayName, boolean hideToolTip) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        Objects.requireNonNull(meta).setHideTooltip(hideToolTip);
        Objects.requireNonNull(meta).setDisplayName(displayName);
        item.setItemMeta(meta);
        return item;
    }

    public static String lowercaseFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    public static String uppercaseFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
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

    public void menuClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        FileConfiguration playerData = getPlayerData(player);
        ItemStack item = event.getCurrentItem();
        ItemMeta meta = Objects.requireNonNull(item).getItemMeta();
        Material material = item.getType();
        String itemName = Objects.requireNonNull(meta).getDisplayName().trim();
        itemName = ChatColor.stripColor(itemName);
        Inventory inv = player.getOpenInventory().getTopInventory();

        if (itemName.contains("Back")) {
            player.closeInventory();
        } else if (itemName.contains("Next")) {
            if (playerData != null) {
                int page = playerData.getInt("selectedPage");
                page = (page == 1) ? 0 : 1;
                playerData.set("selectedPage", page);
                Bukkit.getLogger().log(Level.INFO, "Page selected: " + page);
                collectopaedia.savePlayerFile(playerData, player);
            }
            player.openInventory(updateInventory(player, inv, "page", ""));
        } else if (material == Material.PAPER) {
            itemName = itemName.replace(" ", "");
            itemName = lowercaseFirstLetter(itemName);
            playerData.set("selectedArea", itemName);
            player.openInventory(updateInventory(player, inv, "area", ""));
        } else if (material == Material.BLUE_STAINED_GLASS_PANE) {
            //TODO Take item for player inv
            player.openInventory(updateInventory(player, inv, "submit", itemName));
        }

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
            inventory.setItem(45, BACK_BUTTON);

            player.openInventory(updateInventory(player, inventory, "all", ""));
        });
    }

    private List<List<String>> getItemsList(String area) {
        List<String> types = List.of("veg", "fruit", "flower", "animal", "bug", "nature", "parts", "strange");
        return types.stream().map(type -> collectopaedia.itemsData.getStringList(area + "." + type)).collect(Collectors.toList());
    }

    public Inventory updateInventory(Player player, Inventory inventory, String event, String itemName) {
        Bukkit.getScheduler().runTask(collectopaedia, () -> {
            FileConfiguration playerData = getPlayerData(player);
            List<String> unlockedAreas = playerData.getStringList("unlockedAreas");
            String selectedArea = playerData.getString("selectedArea");

            //Sets up the list of areas and their percent meter.
            if (event.equals("area") || event.equals("all")) {
                Bukkit.getScheduler().runTaskAsynchronously(collectopaedia, () -> {
                    HashMap<Integer, ItemStack> itemStackHashMap = new HashMap<>();
                    int slot = 0;
                    for (String area : unlockedAreas) {
                        if (slot < 9 || slot % 9 == 8 || slot >= 45) {
                            ItemStack areaMaps;
                            if (area.equals(selectedArea)) {
                                areaMaps = createAreaMapItem(area, true);
                            } else {
                                areaMaps = createAreaMapItem(area, false);
                            }
                            itemStackHashMap.put(slot, areaMaps);
                        }
                        slot++;
                    }
                    itemStackHashMap.put(9, createPercentMeter(playerData, selectedArea));
                    Bukkit.getScheduler().runTask(collectopaedia, () -> {
                        for (int i = 0; i < inventory.getSize(); i++) {
                            if (itemStackHashMap.containsKey(i)) {
                                inventory.setItem(i, itemStackHashMap.get(i));
                            }
                        }
                    });
                });
            }

            //Displays the items for each area
            if (event.equals("page") || event.equals("all")) {
                //TODO Displays the items show either item, grey or blue stained glass pane.
                Bukkit.getScheduler().runTaskAsynchronously(collectopaedia, () -> {
                    HashMap<Integer, ItemStack> itemStackHashMap = new HashMap<>();
                    List<String> types = List.of("veg", "fruit", "flower", "animal", "bug", "nature", "parts", "strange");
                    List<List<String>> items = getItemsList(selectedArea);
                    ItemStack temp = new ItemStack(Material.EMERALD);
                    ItemMeta meta = temp.getItemMeta();
                    int selectedPage = 0;
                    if (!(event.equals("all"))) {
                        selectedPage = playerData.getInt("selectedPage");
                    }

                    int noneCount = 0;
                    for (String type : types) {
                        List<String> itemList = collectopaedia.itemsData.getStringList(type);
                        if (itemList.getFirst().equals("None")) {
                            noneCount++;
                        }
                    }
                    for (String type : types) {
                        List<String> itemList = collectopaedia.itemsData.getStringList(type);
                        if (!(itemList.getFirst().equals("None"))) {
                            if (selectedPage == 0) {

                            }
                        }
                    }


                    for (int i = 0; i < types.size(); i++) {
                        if (items.get(i).getFirst().equals("None")) {
                            noneCount++;
                        } else {
                            Bukkit.getLogger().log(Level.INFO, "Page: " + selectedPage + " I: " + i);
                            if (selectedPage == 0 && i <= 4) {
                                Objects.requireNonNull(meta).setDisplayName(uppercaseFirstLetter(types.get(i)));
                                temp.setItemMeta(meta);
                                itemStackHashMap.put((9 * i) + 2, temp);
                            } else if (selectedPage == 1 && i > 4) {
                                Bukkit.getLogger().log(Level.INFO, "I: " + i);
                                Objects.requireNonNull(meta).setDisplayName(uppercaseFirstLetter(types.get(i)));
                                temp.setItemMeta(meta);
                                itemStackHashMap.put(9 * (i - 4), temp);
                            }
                        }
                    }
                    if (noneCount < 4) {
                        itemStackHashMap.put(36, NEXT_PAGE);
                    }
                    Bukkit.getScheduler().runTask(collectopaedia, () -> {
                        for (int i = 0; i < inventory.getSize(); i++) {
                            if (itemStackHashMap.containsKey(i)) {
                                inventory.setItem(i, itemStackHashMap.get(i));
                            }
                        }
                    });
                });
            }

            //Change the inventory after the player submits an idea.
            if (event.equals("submit") && !itemName.isEmpty()) {
                Bukkit.getScheduler().runTaskAsynchronously(collectopaedia, () -> {
                    HashMap<Integer, ItemStack> itemStackHashMap = new HashMap<>();
                });
                inventory.setItem(9, createPercentMeter(playerData, selectedArea));

            }
        });
        return inventory;
    }

    private FileConfiguration getPlayerData(Player p) {
        return playerDataCache.compute(p.getUniqueId(), (id, cachedData) -> {
            FileConfiguration fileData = collectopaedia.loadPlayerData(p);
            // If there's no cached data, or it differs from the file data, update the cache.
            if (cachedData == null || !cachedData.equals(fileData)) {
                return fileData;
            }
            // Otherwise, keep the current cached data.
            return cachedData;
        });
    }

    private ItemStack createAreaMapItem(String area, boolean selected) {
        String areaDisplayName = collectopaedia.areasData.getString(area);
        ItemStack item = new ItemStack(Material.PAPER);
        if (selected) {
            item.setType(Material.FILLED_MAP);
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

    private ItemStack createItem(String name, String type) {
        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        switch (type) {
            case "deposited":
                meta.setDisplayName(ChatColor.GREEN + name);
                item.setType(Material.EMERALD);
                break;
            case "has":
                meta.setDisplayName(ChatColor.BLUE + name);
                item.setType(Material.BLUE_STAINED_GLASS_PANE);
                break;
            case "needed":
                meta.setDisplayName("");
                item.setType(Material.BLACK_STAINED_GLASS_PANE);
                break;
        }
        return item;
    }


    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return List.of();
    }
}

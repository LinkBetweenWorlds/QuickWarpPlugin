package co.xenocraft.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class CreateWorldNameCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player p) {
            if (args.length >= 1) {
                UUID worldUUID = p.getWorld().getUID();
                StringBuilder worldName = new StringBuilder();
                worldName = new StringBuilder(args[0]);
                for (int i = 1; i < args.length; i++) {
                    worldName.append(" ");
                    worldName.append(args[i]);
                }
                System.out.println("World UUID: " + worldUUID);
                System.out.println("World Name: " + worldName);
                String currentDir = System.getProperty("user.dir");
                System.out.println(currentDir);
                String fileDir = currentDir + "\\plugins\\QuickWarp";
                System.out.println(fileDir);
                try {

                    File theDir = new File(fileDir);
                    if (!theDir.exists()) {
                        theDir.mkdirs();
                    }
                    File worldNamesData = new File(fileDir + "\\worldNames.dat");
                    if (worldNamesData.createNewFile()) {
                        System.out.println("File created: " + worldNamesData.getName());
                    } else {
                        System.out.println("File already exists.");
                    }
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
                try {
                    FileWriter worldNameData = new FileWriter(fileDir + "\\worldNames.dat");
                    worldNameData.write(worldUUID.toString());
                    worldNameData.close();
                    System.out.println("File written to.");

                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
                p.sendMessage(ChatColor.GREEN + worldName.toString() + " has been created.");

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
}

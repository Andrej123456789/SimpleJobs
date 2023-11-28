package me.andrej123456789.simplejobs.commands;

import me.andrej123456789.simplejobs.SimpleJobs;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.moandjiezana.toml.Toml;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Jobs implements CommandExecutor, TabExecutor {
    private static final Plugin plugin = JavaPlugin.getProvidingPlugin(SimpleJobs.class);

    private static Set<String> getJobs() {
        return plugin.getConfig().getConfigurationSection("jobs").getKeys(false);
    }

    private static String readFileToString(String filePath) throws IOException {
        Path file = Paths.get(filePath);
        byte[] fileBytes = Files.readAllBytes(file);
        return new String(fileBytes, StandardCharsets.UTF_8);
    }

    private static Map<String, Object> getPrices(Toml toml, @NotNull CommandSender sender) {
        // Get all keys under the 'prices' sub-config
        Toml tomlPrices = toml.getTable("prices");
        Map<String, Object> prices;

        if (tomlPrices == null) {
            sender.sendMessage(ChatColor.YELLOW + "Prices sub-config in config.toml not found!" + ChatColor.RESET);
            return null;
        }

        prices = tomlPrices.toMap();
        return prices;
    }

    private static ArrayList<String> getFiles(String path) {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        ArrayList<String> file_names = new ArrayList<>();

        for (int i = 0; i < listOfFiles.length; i++) {
           file_names.add(listOfFiles[i].getName().replace(".toml", ""));
        }

        return file_names;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only in-game players can execute this command!");
            return true;
        }

        if (!sender.hasPermission("simplejobs.jobs")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
            return true;
        }

        switch (args[0]) {
            case "accept":

                if (args.length < 2) {
                    sender.sendMessage(ChatColor.YELLOW + "This subcommand requires (job) argument!" + ChatColor.RESET);
                    return false;
                }

                Toml toml;

                try {
                    toml = new Toml().read(readFileToString(plugin.getDataFolder() + "/jobs/scraping_copper.toml"));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                ArrayList<String> files = getFiles(plugin.getDataFolder() + "/jobs/");
                for (int i = 0; i < files.size(); i++) {
                    plugin.getLogger().info(files.get(i));
                }

                if (!files.contains(args[1])) {
                    sender.sendMessage(ChatColor.YELLOW + "Job not found in `jobs` folder!" + ChatColor.RESET);
                    return true;
                }

                // TODO
                // accept job

                break;

            case "status":
                break;

            case "quit":
                break;

            case "help":
                sender.sendMessage("Supported arguments: 'accept', 'status', 'quit', 'help'");
                break;

            default:
                sender.sendMessage(ChatColor.YELLOW + "Type /jobs help to see list of supported arguments!");
                break;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("accept", "status", "quit", "help");
        }

        if (args.length == 2) {
            return new ArrayList<>();
        }

        return new ArrayList<>(); /* null = all player names */
    }
}

package me.andrej123456789.simplejobs.commands;

import me.andrej123456789.simplejobs.SimpleJobs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
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
import com.moandjiezana.toml.TomlWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import java.time.format.DateTimeFormatter;

public class Jobs implements CommandExecutor, TabExecutor {
    private static final List<String> job_difficulty = List.of("peaceful", "easy", "medium", "hard", "extreme");
    private static final Plugin plugin = JavaPlugin.getProvidingPlugin(SimpleJobs.class);

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

        for (File listOfFile : listOfFiles) {
            file_names.add(listOfFile.getName().replace(".toml", ""));
        }

        return file_names;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) {
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

                /* -------------------- */
                /*      Get jobs        */
                /* -------------------- */

                if (args.length < 2) {
                    sender.sendMessage(ChatColor.YELLOW + "This subcommand requires (job) argument!" + ChatColor.RESET);
                    return false;
                }

                ArrayList<String> files = getFiles(plugin.getDataFolder() + "/jobs/");
                for (int i = 0; i < files.size(); i++) {
                    plugin.getLogger().info(files.get(i));
                }

                if (!files.contains(args[1])) {
                    sender.sendMessage(ChatColor.YELLOW + "Job not found in `jobs` folder!" + ChatColor.RESET);
                    return true;
                }

                // Sub job

                if (!job_difficulty.contains(args[2])) {
                    sender.sendMessage(ChatColor.YELLOW + "Invalid job difficulty!" + ChatColor.RESET);
                    return true;
                }

                /* -------------------- */
                /*      Accept job      */
                /* -------------------- */

                DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE;
                LocalDateTime now = LocalDateTime.now();

                // Define your data in a modular way
                Map<String, Map<String, Object>> jobs = new HashMap<>();

                // Add job 1
                Map<String, Object> job = new HashMap<>();
                job.put("started", dtf.format(now));
                job.put("ended", "");
                job.put("timeout", "");
                job.put("price", 0.0);
                job.put("blocks_done", 0.0);

                jobs.put(args[1] + "_" + args[2], job);

                appendToTOML(sender, jobs, plugin.getDataFolder() + "/players/" + sender.getName() + ".toml");
                sender.sendMessage(ChatColor.GREEN + "Job" + args[1] + "with difficulty of " + args[2] + " has been accepted!" + ChatColor.RESET);

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

        return new ArrayList<>(); /* null = all player names */
    }

    private static void writeTOML(@NotNull CommandSender sender, Map<String, Map<String, Object>> data, String path) {
        TomlWriter writer = new TomlWriter();
        File outputFile = new File(path);

        try (FileWriter fileWriter = new FileWriter(outputFile)) {
            writer.write(data, fileWriter);
        } catch (IOException e) {
            sender.sendMessage(ChatColor.YELLOW + "Job not found in `jobs` folder!" + ChatColor.RESET);
        }
    }

    // TODO
    private static void appendToTOML(@NotNull CommandSender sender, Map<String, Map<String, Object>> data, String fileName) {
        TomlWriter writer = new TomlWriter();
        File outputFile = new File(fileName);

        // If the file exists, load existing data
        Map<String, Object> existingData = new HashMap<>();
        if (outputFile.exists()) {
            existingData = new Toml().read(outputFile).toMap();
        }

        // Merge existing data with new data
        existingData.putAll(data);

        try (FileWriter fileWriter = new FileWriter(outputFile)) {
            writer.write(existingData, fileWriter);
        } catch (IOException e) {
            sender.sendMessage(ChatColor.YELLOW + "Job not found in `jobs` folder!" + ChatColor.RESET);
        }
    }

    private static String removeSubJob(String input) {
        int dotIndex = input.indexOf('.');

        if (dotIndex != -1) {
            return input.substring(0, dotIndex);
        } else {
            // If there is no dot, return the original string
            return input;
        }
    }
}

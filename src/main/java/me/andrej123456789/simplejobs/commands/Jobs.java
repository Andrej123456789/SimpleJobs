package me.andrej123456789.simplejobs.commands;

import me.andrej123456789.simplejobs.SimpleJobs;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Jobs implements CommandExecutor, TabExecutor {
    private static final Plugin plugin = JavaPlugin.getProvidingPlugin(SimpleJobs.class);

    private static Set<String> getJobs() {
        return plugin.getConfig().getConfigurationSection("jobs").getKeys(false);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.YELLOW + "Too few arguments!");
            sender.sendMessage("Usage: /jobs <subcommand> <argument>");
            return true;
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
                Set<String> jobs = getJobs();
                if (jobs == null) {
                    sender.sendMessage(ChatColor.RED + "No jobs have been found!");
                    break;
                }

                if (!jobs.contains(args[1])) {
                    sender.sendMessage(ChatColor.YELLOW + "Requested job does not exist!");
                    break;
                }

                ConfigurationSection root_section = plugin.getConfig().getConfigurationSection("jobs.scrape_copper_peaceful.players");
                if (root_section == null) {
                    sender.sendMessage(ChatColor.RED + "Root section has not been found!");
                    break;
                }

                ConfigurationSection example_section = root_section.getConfigurationSection("Player1");
                if (example_section == null) {
                    sender.sendMessage(ChatColor.RED + "Example section has not been found!");
                    break;
                }

                Map<String, Object> new_section = new HashMap<>();
                for (String i : example_section.getKeys(false)) {
                    new_section.put(i, example_section.get(i));
                }

                DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE;
                LocalDateTime now = LocalDateTime.now();

                plugin.getConfig().createSection("jobs." + args[1] + ".players." + sender.getName(), new_section);
                plugin.getConfig().set("jobs." + args[1] + ".players." + sender.getName() + ".started", dtf.format(now));

                plugin.saveConfig();
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
            return new ArrayList<>(getJobs());
        }

        return new ArrayList<>(); /* null = all player names */
    }
}

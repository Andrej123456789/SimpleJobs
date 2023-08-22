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
            return false;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("Only in-game players can execute this command!");
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

                plugin.getConfig().createSection("jobs." + args[1] + ".players." + sender.getName(), new_section);
                plugin.saveConfig();

                break;

            case "status":
                break;

            case "quit":
                break;

            default:
                return false;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("accept", "status", "quit");
        }

        if (args.length == 2) {
            return new ArrayList<>(getJobs());
        }

        return new ArrayList<>(); /* null = all player names */
    }
}

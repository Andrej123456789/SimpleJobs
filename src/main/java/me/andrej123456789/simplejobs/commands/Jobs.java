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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Jobs implements CommandExecutor, TabExecutor {
    private static final Plugin plugin = JavaPlugin.getProvidingPlugin(SimpleJobs.class);

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

                for (String i : example_section.getKeys(false)) {
                    sender.sendMessage(i);
                }

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
            return Arrays.asList("scrape_copper_peaceful");
        }

        return new ArrayList<>(); /* null = all player names */
    }
}

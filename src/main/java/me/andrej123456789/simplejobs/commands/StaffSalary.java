package me.andrej123456789.simplejobs.commands;

import com.moandjiezana.toml.Toml;
import me.andrej123456789.simplejobs.SimpleJobs;
import static me.andrej123456789.simplejobs.SimpleJobs.getEconomy;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class StaffSalary implements CommandExecutor, TabExecutor {
    private static final Plugin plugin = JavaPlugin.getProvidingPlugin(SimpleJobs.class);

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only in-game players can execute this command!");
            return true;
        }

        String path = plugin.getDataFolder() + "/staff_jobs/staff_team.toml";
        Toml toml = new Toml().read(new File(path));

        Set<String> keys = toml.getTable("staff_team").toMap().keySet();

        LocalDate currentDate = LocalDate.now();
        boolean isSixthDay = currentDate.getDayOfMonth() == 6;

        boolean found_permission = false;
        String job = "";

        for (String key : keys) {
            if (sender.hasPermission("staff_salary." + key)) {
                found_permission = true;
                job = key;
            }
        }

        if (found_permission) {
            if (isSixthDay) {
                getEconomy().depositPlayer((OfflinePlayer) sender, toml.getDouble("staff_team." + job));
            } else {
                sender.sendMessage("You can claim your salary 6th in month!");
            }
        } else {
            sender.sendMessage("Permission for you was not found!");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>(); /* null = all player names */
    }
}

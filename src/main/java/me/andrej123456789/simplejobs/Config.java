package me.andrej123456789.simplejobs;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {
    private static File file;
    private static FileConfiguration customFile;

    /* Finds or generates custom config file(s) */
    public static void setup() {
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("SimpleJobs").getDataFolder(), "jobs.yml");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().warning("[SimpleJobs] Exception occured: " + e.toString());
            }
        }

        customFile = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get() {
        return customFile;
    }

    public static void save() {
        try {
            customFile.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().warning("[SimpleJobs] Exception occured: " + e.toString());
        }
    }

    public static void reload() {
        customFile = YamlConfiguration.loadConfiguration(file);
    }
}

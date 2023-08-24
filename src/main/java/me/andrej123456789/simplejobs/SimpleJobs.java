package me.andrej123456789.simplejobs;

import me.andrej123456789.simplejobs.commands.Jobs;
import me.andrej123456789.simplejobs.commands.JobsAdmin;
import me.andrej123456789.simplejobs.jobs.Copper;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleJobs extends JavaPlugin {

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new Copper(), this);

        getCommand("jobs_admin").setExecutor(new JobsAdmin());
        getCommand("jobs").setExecutor(new Jobs());

        getLogger().info("Initialization of ExecSigns is done!");
        getServer().getConsoleSender().sendMessage("If you like this plugin, give it a star on Github: " + ChatColor.AQUA + getConfig().getString("github"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

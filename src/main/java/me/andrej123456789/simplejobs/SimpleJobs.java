package me.andrej123456789.simplejobs;

import me.andrej123456789.simplejobs.jobs.Copper;
import me.andrej123456789.simplejobs.commands.JobsAdmin;

import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleJobs extends JavaPlugin {

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new Copper(), this);
        getCommand("jobs_admin").setExecutor(new JobsAdmin());

        getLogger().info("Initialization of ExecSigns is done!");
        getLogger().info("If you like this plugin, give it a star on Github: " + getConfig().getString("github"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

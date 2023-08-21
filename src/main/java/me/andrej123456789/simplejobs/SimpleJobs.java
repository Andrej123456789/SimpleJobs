package me.andrej123456789.simplejobs;

import me.andrej123456789.simplejobs.commands.JobsAdmin;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleJobs extends JavaPlugin {

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        Config.setup();
        Config.get().addDefault("copper", "default");
        Config.get().options().copyDefaults(true);
        Config.save();

        getServer().getPluginManager().registerEvents(new me.andrej123456789.simplejobs.Copper(), this);
        getCommand("jobs_admin").setExecutor(new JobsAdmin());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

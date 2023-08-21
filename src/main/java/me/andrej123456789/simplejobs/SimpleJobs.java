package me.andrej123456789.simplejobs;

import org.bukkit.plugin.java.JavaPlugin;

public final class SimpleJobs extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Copper(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

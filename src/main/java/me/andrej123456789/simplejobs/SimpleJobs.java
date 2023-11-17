package me.andrej123456789.simplejobs;

import me.andrej123456789.simplejobs.commands.Jobs;
import me.andrej123456789.simplejobs.commands.JobsAdmin;
import me.andrej123456789.simplejobs.jobs.Copper;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;

public final class SimpleJobs extends JavaPlugin {

    private static Economy econ = null;

    @Override
    public void onEnable() {
        // Plugin startup logic

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(new Copper(), this);

        getCommand("jobs_admin").setExecutor(new JobsAdmin());
        getCommand("jobs").setExecutor(new Jobs());

        getLogger().info("Initialization of ExecSigns is done!");
        getServer().getConsoleSender().sendMessage("[SimpleJobs] If you like this plugin, give it a star on Github: " + ChatColor.AQUA + getConfig().getString("github"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        getLogger().info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }
}

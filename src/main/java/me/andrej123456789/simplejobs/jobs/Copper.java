package me.andrej123456789.simplejobs.jobs;

import me.andrej123456789.simplejobs.SimpleJobs;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Copper implements Listener {
    private static final List<Material> EXPOSED = List.of(Material.EXPOSED_COPPER, Material.EXPOSED_CUT_COPPER, Material.EXPOSED_CUT_COPPER_SLAB, Material.EXPOSED_CUT_COPPER_STAIRS);
    private static final List<Material> WEATHERED = List.of(Material.WEATHERED_COPPER, Material.WEATHERED_CUT_COPPER, Material.WEATHERED_CUT_COPPER_SLAB, Material.WEATHERED_CUT_COPPER_STAIRS);

    private static final List<Material> OXIDIZED = List.of(Material.OXIDIZED_COPPER, Material.OXIDIZED_CUT_COPPER, Material.OXIDIZED_CUT_COPPER_SLAB, Material.OXIDIZED_CUT_COPPER_SLAB);

    private static final Plugin plugin = JavaPlugin.getProvidingPlugin(SimpleJobs.class);

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clicked = event.getClickedBlock();
        EquipmentSlot slot = event.getHand();

        if (clicked == null)
            return;

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (slot != EquipmentSlot.HAND)
            return;

        ItemStack hand = event.getItem();

        if (hand == null || hand.getType() != Material.NETHERITE_AXE)
            return;

        ConfigurationSection jobs = plugin.getConfig().getConfigurationSection("jobs");

        String job = "";
        boolean accepted = false;

        for (String _job : jobs.getKeys(false)) {
            ConfigurationSection players = jobs.getConfigurationSection(_job + ".players");

            if (players.getKeys(false).contains(player.getName())) {
                job = _job;
                accepted = true;

                break;
            }
        }

        if (!accepted) {
            return;
        }

        final String job_path = "jobs." + job;
        final String players_path = ".players." + player.getName();

        if (EXPOSED.contains(clicked.getType())) {
            if (plugin.getConfig().getDouble(job_path + ".prices.price_final") == 0.00) {
                double value = plugin.getConfig().getDouble(job_path + players_path + ".current_price");
                value += plugin.getConfig().getDouble(job_path + ".prices.exposed_block");

                plugin.getConfig().set(job_path + players_path + ".current_price", value);
                plugin.saveConfig();
            }

            int blocks_done = plugin.getConfig().getInt(job_path + players_path + ".blocks_done");
            plugin.getConfig().set(job_path + players_path + ".blocks_done", blocks_done += 3);

            plugin.saveConfig();
        }

        if (WEATHERED.contains(clicked.getType())) {
            if (plugin.getConfig().getDouble(job_path + ".prices.price_final") == 0.00) {
                double value = plugin.getConfig().getDouble(job_path + players_path + ".current_price");
                value += plugin.getConfig().getDouble(job_path + ".prices.weathered_block");

                plugin.getConfig().set(job_path + players_path + ".current_price", value);
                plugin.saveConfig();
            }

            int blocks_done = plugin.getConfig().getInt(job_path + players_path + ".blocks_done");
            plugin.getConfig().set(job_path + players_path + ".blocks_done", blocks_done += 2);

            plugin.saveConfig();
        }

        if (OXIDIZED.contains(clicked.getType())) {
            if (plugin.getConfig().getDouble(job_path + ".prices.price_final") == 0.00) {

                double value = plugin.getConfig().getDouble(job_path + players_path + ".current_price");
                value += plugin.getConfig().getDouble(job_path + ".prices.oxidized_block");

                plugin.getConfig().set(job_path + players_path + ".current_price", value);
                plugin.saveConfig();
            }

            int blocks_done = plugin.getConfig().getInt(job_path + players_path + ".blocks_done");
            plugin.getConfig().set(job_path + players_path + ".blocks_done", blocks_done += 1);

            plugin.saveConfig();
        }

        /* Player can scrape oxidized block and then leave it in weathered state.
           You removed the worst state, and you got money; but I'll make sure you don't leave your job unfinished */

        if (plugin.getConfig().getInt(job_path + players_path + ".blocks_done") == plugin.getConfig().getInt(job_path + ".blocks_to_do")) {
            // TODO
            // implement days

            // TODO
            // hook into Vault and give player money

            player.sendMessage(ChatColor.GREEN + "Congrats, you finished your job!");
            player.sendMessage(ChatColor.GREEN + "Your reward: " + ChatColor.DARK_GREEN + Double.toString(plugin.getConfig().getDouble(job_path + players_path + ".current_price")));

            plugin.getConfig().set(job_path + players_path + ".last_finished", "");

            plugin.getConfig().set(job_path + players_path + ".blocks_done", 0);
            plugin.getConfig().set(job_path + players_path + ".current_price", 0.00);

            plugin.saveConfig();
        }
    }
}

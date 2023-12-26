package me.andrej123456789.simplejobs.jobs;

import me.andrej123456789.simplejobs.SimpleJobs;
import static me.andrej123456789.simplejobs.SimpleJobs.getEconomy;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;

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

        // Specify the path to your TOML file
        String filePath = plugin.getDataFolder() + "/players/" + player.getName() + ".toml";

        // Parse the TOML file
        Toml toml = new Toml().read(new File(filePath));

        // Get the root map, which contains all the tables
        Map<String, Object> rootMap = toml.toMap();

        // Extract and print the table names
        ArrayList<String> tableNames = (ArrayList<String>) rootMap.keySet();
        System.out.println("Table Names: " + tableNames);

    }
}

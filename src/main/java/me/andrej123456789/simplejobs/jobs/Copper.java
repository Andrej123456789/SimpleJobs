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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import com.moandjiezana.toml.Toml;

public class Copper implements Listener {
    private static final List<Material> EXPOSED = List.of(Material.EXPOSED_COPPER, Material.EXPOSED_CUT_COPPER, Material.EXPOSED_CUT_COPPER_SLAB, Material.EXPOSED_CUT_COPPER_STAIRS);
    private static final List<Material> WEATHERED = List.of(Material.WEATHERED_COPPER, Material.WEATHERED_CUT_COPPER, Material.WEATHERED_CUT_COPPER_SLAB, Material.WEATHERED_CUT_COPPER_STAIRS);

    private static final List<Material> OXIDIZED = List.of(Material.OXIDIZED_COPPER, Material.OXIDIZED_CUT_COPPER, Material.OXIDIZED_CUT_COPPER_SLAB, Material.OXIDIZED_CUT_COPPER_SLAB);

    private static final List<Material> AXES = List.of(Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE);

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

        if (hand == null || !AXES.contains(hand.getType()))
            return;

        String playerPath = plugin.getDataFolder() + "/players/" + player.getName() + ".toml";
        String jobPath = plugin.getDataFolder() + "/jobs/scrape_copper.toml";

        Toml playerToml = new Toml().read(new File(playerPath));
        Toml jobToml = new Toml().read(new File(jobPath));

        ArrayList<String> difficulties = new ArrayList<>();
        Map<String, Object> rootMap = playerToml.toMap();

        Set<String> tableNames = rootMap.keySet();
        List<String> accepted_jobs = tableNames.stream().toList();

        for (String acceptedJob : accepted_jobs) {
            if (acceptedJob.startsWith("scrape_copper")) {
                difficulties.add(getDifficulty(acceptedJob));
            }
        }

        if (difficulties.isEmpty()) {
            return;
        }

        if (!EXPOSED.contains(clicked.getType()) && !WEATHERED.contains(clicked.getType()) &&
            !OXIDIZED.contains(clicked.getType())) {
            return;
        }

        double block_price = 0.0;

        double current_blocks = 0.0;
        double current_price = 0.0;

        for (String difficulty : difficulties)
        {
            current_blocks = playerToml.getDouble("scrape_copper_" + difficulty + ".blocks_done");
            current_price = playerToml.getDouble("scrape_copper_" + difficulty + ".price");
        }

        if (EXPOSED.contains(clicked.getType())) {
            block_price = jobToml.getDouble("scrape_copper.prices.exposed_block");
        }

        if (WEATHERED.contains(clicked.getType())) {
            block_price = jobToml.getDouble("scrape_copper.prices.weathered_block");
        }

        if (OXIDIZED.contains(clicked.getType())) {
            block_price = jobToml.getDouble("scrape_copper.prices.oxidized_block");
        }

        for (String difficulty : difficulties)
        {
            updateTomlVariable(player, playerPath, "scrape_copper_" + difficulty, "blocks_done", current_blocks + 1);
            updateTomlVariable(player, playerPath, "scrape_copper_" + difficulty, "price", current_price + block_price);
        }

        for (String difficulty : difficulties)
        {
            if (playerToml.getDouble("scrape_copper_" + difficulty + ".blocks_done") >= jobToml.getDouble("scrape_copper." + difficulty + ".blocks_to_do")) {
                DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE;
                LocalDateTime now = LocalDateTime.now();

                LocalDate date1 = LocalDate.parse(playerToml.getString("scrape_copper_" + difficulty + ".started"), dtf);
                LocalDate date2 = LocalDate.now();

                if (ChronoUnit.DAYS.between(date1, date2) >= jobToml.getDouble("scrape_copper." + difficulty + ".duration")) {
                    updateTomlVariable(player, playerPath, "scrape_copper_" + difficulty, "started", "");
                    updateTomlVariable(player, playerPath, "scrape_copper_" + difficulty, "ended", dtf.format(now));
                    updateTomlVariable(player, playerPath, "scrape_copper_" + difficulty, "price", 0.0);
                    updateTomlVariable(player, playerPath, "scrape_copper_" + difficulty, "blocks_done", 0.0);

                    getEconomy().depositPlayer(player, playerToml.getDouble("scrape_copper_" + difficulty + ".price"));
                    player.sendMessage(ChatColor.GREEN + "Congrats on finished work!" + ChatColor.RESET);
                }

                player.sendMessage(ChatColor.GREEN + "You finished for today!" + ChatColor.RESET);
                updateTomlVariable(player, playerPath, "scrape_copper_" + difficulty, "blocks_done", 0.0);
            }
        }
    }

    private static String getDifficulty(String inputString) {
        int firstOccurrence = inputString.indexOf('_');
        int secondOccurrence = inputString.indexOf('_', firstOccurrence + 1);

        if (firstOccurrence != -1 && secondOccurrence != -1) {
            return inputString.substring(secondOccurrence + 1);
        } else {
            return "Second occurrence not found.";
        }
    }

    private static void updateTomlVariable(Player player, String filePath, String tableName, String keyToUpdate, double newValue) {
        try {
            // Read all lines from the file
            Path path = Paths.get(filePath);
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

            // Find the line containing the key in the specified table
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.trim().startsWith("[" + tableName + "]")) {
                    // Table found, look for the key within the table
                    for (int j = i + 1; j < lines.size(); j++) {
                        String innerLine = lines.get(j);
                        if (innerLine.trim().startsWith(keyToUpdate + " =")) {
                            // Key found, update its value
                            lines.set(j, keyToUpdate + " = " + newValue);
                            break;
                        }
                    }
                    break;
                }
            }

            // Write the updated lines back to the file
            Files.write(path, lines, StandardCharsets.UTF_8);

        } catch (IOException e) {
            player.sendMessage(ChatColor.YELLOW + e.toString() + ChatColor.RESET);
        }
    }

    private static void updateTomlVariable(Player player, String filePath, String tableName, String keyToUpdate, String newValue) {
        try {
            // Read all lines from the file
            Path path = Paths.get(filePath);
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

            // Find the line containing the key in the specified table
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.trim().startsWith("[" + tableName + "]")) {
                    // Table found, look for the key within the table
                    for (int j = i + 1; j < lines.size(); j++) {
                        String innerLine = lines.get(j);
                        if (innerLine.trim().startsWith(keyToUpdate + " =")) {
                            // Key found, update its value
                            lines.set(j, keyToUpdate + " = \"" + newValue + "\"");
                            break;
                        }
                    }
                    break;
                }
            }

            // Write the updated lines back to the file
            Files.write(path, lines, StandardCharsets.UTF_8);

        } catch (IOException e) {
            player.sendMessage(ChatColor.YELLOW + e.toString() + ChatColor.RESET);
        }
    }
}

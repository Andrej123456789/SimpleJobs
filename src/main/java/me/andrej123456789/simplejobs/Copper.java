package me.andrej123456789.simplejobs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Copper implements Listener {
    private static final List<Material> EXPOSED = List.of(Material.EXPOSED_COPPER, Material.EXPOSED_CUT_COPPER, Material.EXPOSED_CUT_COPPER_SLAB, Material.EXPOSED_CUT_COPPER_STAIRS);
    private static final List<Material> WEATHERED = List.of(Material.WEATHERED_COPPER, Material.WEATHERED_CUT_COPPER, Material.WEATHERED_CUT_COPPER_SLAB, Material.WEATHERED_CUT_COPPER_STAIRS);

    private static final List<Material> OXIDIZED = List.of(Material.OXIDIZED_COPPER, Material.OXIDIZED_CUT_COPPER, Material.OXIDIZED_CUT_COPPER_SLAB, Material.OXIDIZED_CUT_COPPER_SLAB);

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

        int weight = 0;

        switch (clicked.getType()) {
            case EXPOSED:
                weight = 1;
                break;
            case WEATHERED:
                weight = 2;
                break;
            case OXIDIZED:
                weight = 3;
                break;
            default:
                break;
        }

        Bukkit.getLogger().info(Integer.toString(weight));
    }
}

package com.xraylookup;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class MiningListener implements Listener {
    private final XRayLookup plugin;

    public MiningListener(XRayLookup plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        Material type = event.getBlock().getType();
        String typeStr = type.name();

        if (typeStr.contains("_ORE")) {
            // Normalize Deepslate and regular ores
            String key = typeStr.replace("DEEPSLATE_", "");
            plugin.getDatabase().addOre(event.getPlayer().getUniqueId(), event.getPlayer().getName(), key);
        }
    }
}
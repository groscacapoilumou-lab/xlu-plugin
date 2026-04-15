package com.xraylookup;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import java.util.Map;

public class CommandHandler implements CommandExecutor {
    private final XRayLookup plugin;

    public CommandHandler(XRayLookup plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) return false;

        String prefix = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("prefix"));

        if (args[0].equalsIgnoreCase("stats") && args.length > 1) {
            Map<String, Integer> stats = plugin.getDatabase().getStats(args[1]);
            if (stats.isEmpty()) {
                sender.sendMessage(prefix + "§cNo data found for that player.");
                return true;
            }

            int total = stats.get("TOTAL_ORES");
            sender.sendMessage("§b--- Mining Stats: §f" + args[1] + " §b---");
            sender.sendMessage("§7Total Ores: §f" + total);
            stats.forEach((k, v) -> {
                if (!k.equals("TOTAL_ORES")) {
                    double pct = (total == 0) ? 0 : (v * 100.0 / total);
                    sender.sendMessage(String.format("§e%s: §f%d §8(§a%.1f%%§8)", k.replace("_ORE", ""), v, pct));
                }
            });
            return true;
        }

        if (args[0].equalsIgnoreCase("suspicious")) {
            sender.sendMessage("§c--- Suspicious Players (Diamond Ratio) ---");
            double threshold = plugin.getConfig().getDouble("diamond-ratio-threshold");
            int minOres = plugin.getConfig().getInt("min-total-ores");

            for (String entry : plugin.getDatabase().getAllStats()) {
                String[] parts = entry.split(":");
                int diamonds = Integer.parseInt(parts[1]);
                int total = Integer.parseInt(parts[2]);
                double ratio = (total == 0) ? 0 : (diamonds * 100.0 / total);

                if (total >= minOres && ratio >= threshold) {
                    sender.sendMessage(String.format("§f%s: §c%.1f%% §7(%d/%d)", parts[0], ratio, diamonds, total));
                }
            }
            return true;
        }

        return true;
    }
}
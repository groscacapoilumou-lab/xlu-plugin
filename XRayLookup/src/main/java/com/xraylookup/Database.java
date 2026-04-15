package com.xraylookup;

import java.sql.*;
import java.util.*;

public class Database {
    private final XRayLookup plugin;
    private Connection connection;

    public Database(XRayLookup plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) dataFolder.mkdirs();
            
            connection = DriverManager.getConnection("jdbc:sqlite:" + new File(dataFolder, "mining_stats.db"));
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS stats (" +
                    "uuid TEXT PRIMARY KEY, " +
                    "name TEXT, " +
                    "DIAMOND_ORE INTEGER DEFAULT 0, " +
                    "GOLD_ORE INTEGER DEFAULT 0, " +
                    "IRON_ORE INTEGER DEFAULT 0, " +
                    "COAL_ORE INTEGER DEFAULT 0, " +
                    "EMERALD_ORE INTEGER DEFAULT 0, " +
                    "REDSTONE_ORE INTEGER DEFAULT 0, " +
                    "LAPIS_ORE INTEGER DEFAULT 0, " +
                    "TOTAL_ORES INTEGER DEFAULT 0)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addOre(UUID uuid, String name, String type) {
        try {
            String query = "INSERT INTO stats (uuid, name, " + type + ", TOTAL_ORES) VALUES (?, ?, 1, 1) " +
                           "ON CONFLICT(uuid) DO UPDATE SET " + type + " = " + type + " + 1, TOTAL_ORES = TOTAL_ORES + 1, name = ?";
            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setString(1, uuid.toString());
            pstmt.setString(2, name);
            pstmt.setString(3, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Integer> getStats(String name) {
        Map<String, Integer> stats = new HashMap<>();
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM stats WHERE name = ? COLLATE NOCASE");
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String[] types = {"DIAMOND_ORE", "GOLD_ORE", "IRON_ORE", "COAL_ORE", "EMERALD_ORE", "REDSTONE_ORE", "LAPIS_ORE", "TOTAL_ORES"};
                for (String t : types) stats.put(t, rs.getInt(t));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return stats;
    }

    public List<String> getAllStats() {
        List<String> results = new ArrayList<>();
        try {
            ResultSet rs = connection.createStatement().executeQuery("SELECT name, DIAMOND_ORE, TOTAL_ORES FROM stats");
            while (rs.next()) {
                results.add(rs.getString("name") + ":" + rs.getInt("DIAMOND_ORE") + ":" + rs.getInt("TOTAL_ORES"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return results;
    }
}
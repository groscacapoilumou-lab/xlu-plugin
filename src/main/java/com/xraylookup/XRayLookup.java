package com.xraylookup;

import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public class XRayLookup extends JavaPlugin {
    private Database database;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        
        database = new Database(this);
        database.initialize();

        getServer().getPluginManager().registerEvents(new MiningListener(this), this);
        getCommand("xlu").setExecutor(new CommandHandler(this));

        getLogger().info("XRayLookup enabled successfully!");
    }

    public Database getDatabase() {
        return database;
    }
}
package com.str.oregen;


import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main extends JavaPlugin implements Listener {

    private List<OreGenSettings> oreGenSettings;

    @Override
    public void onEnable() {
        getLogger().info("Enabled " + getName());

        ConfigurationSerialization.registerClass(OreGenSettings.class, "OreGenSettings");

        FileConfiguration config = new YamlConfiguration();

        ArrayList<Material> sand = new ArrayList<>();

        sand.add(Material.SAND);

        config.set("oreGenSettings", new OreGenSettings[] {
            new OreGenSettings(Material.COAL_ORE,        3.5, 10, 10, 0.6000, 0.9),
            new OreGenSettings(Material.IRON_ORE,        2.5,  6,  6, 0.4000, 0.8),
            new OreGenSettings(Material.DIAMOND_ORE,     7.5,  2,  1, 0.0500, 0.3),
            new OreGenSettings(Material.DIAMOND_ORE,    16.0,  1,  1, 0.0005, 1.0),
            new OreGenSettings(Material.EMERALD_ORE,     8.5,  2,  1, 0.0500, 0.3),
            new OreGenSettings(Material.LAPIS_ORE,       2.5, 10, 10, 0.4000, 0.8),
            new OreGenSettings(Material.COPPER_ORE,      2.5,  8,  8, 0.3000, 0.8),
            new OreGenSettings(Material.REDSTONE_ORE,    2.5,  3,  3, 0.1000, 0.8),
            new OreGenSettings(Material.GOLD_ORE,        5.5,  3,  1, 0.2000, 0.5),
            new OreGenSettings(Material.ANCIENT_DEBRIS,  5.5,  3,  1, 0.2000, 0.5),
            new OreGenSettings(Material.SPONGE,          1.5, 10,  1, 0.3000, 1.0, sand)
        });

        try {
            getLogger().info("Saving config.yml");

            config.save("config.yml");

            getLogger().info("Saved config.yml");
        }
        catch(IOException ioe) {
            getLogger().info("Error saving config.yml" + ioe);
        }

        config = YamlConfiguration.loadConfiguration(new File("config.yml"));

        oreGenSettings = config.getList("oreGenSettings").stream().filter(raw -> raw instanceof OreGenSettings).map(raw -> (OreGenSettings)raw).filter(ogs -> ogs.isValid).toList();

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled " + getName());
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
        if (event.getWorld().getEnvironment() != World.Environment.NORMAL) return;

        getLogger().info("onWorldInit " + getName());

        for(OreGenSettings settings : oreGenSettings) {
            event.getWorld().getPopulators().add(new OrePopulator(settings, event.getWorld().getSeed(), event.getWorld().getMinHeight(), event.getWorld().getMaxHeight(), getLogger()));
        }

        List<BlockPopulator> populators = event.getWorld().getPopulators();

        getLogger().info("There are " + populators.size() + " populators.");
    }

}

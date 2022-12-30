package com.str.oregen;


import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;


public class Main extends JavaPlugin implements Listener {

    private YamlConfiguration config;

    private List<OreGenSettings> oreGenSettings;

    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(OreGenSettings.class, "OreGenSettings");

        loadSettings();

        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled " + getName());
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
        if (event.getWorld().getEnvironment() != World.Environment.NORMAL) return;

        for(OreGenSettings settings : oreGenSettings) {
            event.getWorld().getPopulators().add(new OrePopulator(settings, event.getWorld().getSeed(), event.getWorld().getMinHeight(), event.getWorld().getMaxHeight(), getLogger()));
        }

        List<BlockPopulator> populators = event.getWorld().getPopulators();

        getLogger().info("There are " + populators.size() + " populators.");
    }

    private void loadSettings() {
        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            buildDefaultSettings();

            saveSettings();
        }
        else config = new YamlConfiguration();

        try {
            config.load(file);

            List<?> list = config.getList("oreGenSettings");

            if (list == null) throw new Exception("STR OreGen Settings is not valid yaml.");

            oreGenSettings = list.stream().filter(raw -> raw instanceof OreGenSettings).map(raw -> (OreGenSettings)raw).filter(ogs -> ogs.isValid).toList();

            return;
        }
        catch(Exception ex) {
            getLogger().log(Level.WARNING, "STR OreGen Settings failed to load.", ex);
        }

        buildDefaultSettings();
    }

    private void buildDefaultSettings() {
        ArrayList<Material> sand = new ArrayList<>();
        ArrayList<Material> dirt = new ArrayList<>();

        sand.add(Material.SAND);

        dirt.add(Material.SAND);
        dirt.add(Material.DIRT);
        dirt.add(Material.COARSE_DIRT);
        dirt.add(Material.GRASS_BLOCK);
        dirt.add(Material.GRAVEL);

        config = new YamlConfiguration();

        config.set("oreGenSettings", new OreGenSettings[] {
          //
          // Ore, Radius, Tries per Chunk, Max Successes per Chunk, % Chance per Try, % Chance per Block, {Replaceable Blocks}, {Log Attempts}
          //
          new OreGenSettings(Material.COAL_ORE,        5.5,  4,  2, 0.40000000, 0.8),
          new OreGenSettings(Material.IRON_ORE,        5.5,  4,  2, 0.40000000, 0.8),
          new OreGenSettings(Material.DIAMOND_ORE,     4.5,  3,  1, 0.08500000, 0.4),
          new OreGenSettings(Material.DIAMOND_ORE,    16.0,  1,  1, 0.00005999, 1.0, true),
          new OreGenSettings(Material.EMERALD_ORE,     4.5,  3,  1, 0.08500000, 0.4),
          new OreGenSettings(Material.EMERALD_ORE,    16.0,  1,  1, 0.00005999, 1.0, true),
          new OreGenSettings(Material.LAPIS_ORE,       2.5,  5,  5, 0.50000000, 0.8),
          new OreGenSettings(Material.COPPER_ORE,      2.5,  8,  8, 0.40000000, 0.8),
          new OreGenSettings(Material.REDSTONE_ORE,    2.5,  3,  3, 0.10000000, 0.8),
          new OreGenSettings(Material.GOLD_ORE,        3.5,  3,  1, 0.20000000, 0.6),
          new OreGenSettings(Material.ANCIENT_DEBRIS,  5.5,  3,  1, 0.08500000, 0.4),
          new OreGenSettings(Material.SPONGE,          2.0,  5,  1, 0.20000000, 1.0, sand),
          new OreGenSettings(Material.CLAY,            3.0,  5,  1, 0.30000000, 1.0, dirt)
        });
    }

    private void saveSettings() {
        try {
            config.save(new File(getDataFolder(), "config.yml"));
        }
        catch(Exception ex) {
            getLogger().log(Level.WARNING, "STR OreGen Settings failed to save.", ex);
        }
    }

}

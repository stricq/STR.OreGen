package com.str.oregen;


import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@SerializableAs("OreGenSettings")
public class OreGenSettings implements ConfigurationSerializable {

    //region Private Fields

    private static final HashMap<Material, Material> deepslateOres = new HashMap<>();

    private static final ArrayList<Material> defaultReplacedBlocks = new ArrayList<>();

    private final ArrayList<Material> replacedBlocks;

    //endregion Private Fields

    //region Static Initializers

    static {
        deepslateOres.put(Material.COAL_ORE,     Material.DEEPSLATE_COAL_ORE);
        deepslateOres.put(Material.IRON_ORE,     Material.DEEPSLATE_IRON_ORE);
        deepslateOres.put(Material.COPPER_ORE,   Material.DEEPSLATE_COPPER_ORE);
        deepslateOres.put(Material.DIAMOND_ORE,  Material.DEEPSLATE_DIAMOND_ORE);
        deepslateOres.put(Material.EMERALD_ORE,  Material.DEEPSLATE_EMERALD_ORE);
        deepslateOres.put(Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE);
        deepslateOres.put(Material.LAPIS_ORE,    Material.DEEPSLATE_LAPIS_ORE);
        deepslateOres.put(Material.GOLD_ORE,     Material.DEEPSLATE_GOLD_ORE);

        defaultReplacedBlocks.add(Material.GRANITE);
        defaultReplacedBlocks.add(Material.STONE);
        defaultReplacedBlocks.add(Material.ANDESITE);
        defaultReplacedBlocks.add(Material.DIORITE);
        defaultReplacedBlocks.add(Material.CALCITE);
        defaultReplacedBlocks.add(Material.DEEPSLATE);
        defaultReplacedBlocks.add(Material.TUFF);
    }

    //endregion Static Initializers

    //region Constructors

    public OreGenSettings(Material ore, double radius, int triesPerChunk, int maxPerChunk, double percentChancePerTry, double percentChancePerBlock) {
        this(ore, radius, triesPerChunk, maxPerChunk, percentChancePerTry, percentChancePerBlock, new ArrayList<>(), false);
    }

    public OreGenSettings(Material ore, double radius, int triesPerChunk, int maxPerChunk, double percentChancePerTry, double percentChancePerBlock, boolean logAttempts) {
        this(ore, radius, triesPerChunk, maxPerChunk, percentChancePerTry, percentChancePerBlock, new ArrayList<>(), logAttempts);
    }

    public OreGenSettings(Material ore, double radius, int triesPerChunk, int maxPerChunk, double percentChancePerTry, double percentChancePerBlock, ArrayList<Material> replacedBlocks) {
        this(ore, radius, triesPerChunk, maxPerChunk, percentChancePerTry, percentChancePerBlock, replacedBlocks, false);
    }

    public OreGenSettings(Material ore, double radius, int triesPerChunk, int maxPerChunk, double percentChancePerTry, double percentChancePerBlock, ArrayList<Material> replacedBlocks, boolean logAttempts) {
        this.ore = ore;

        this.radius = radius;

        this.triesPerChunk = triesPerChunk;
        this.maxPerChunk   = maxPerChunk;

        this.percentChancePerTry   = percentChancePerTry;
        this.percentChancePerBlock = percentChancePerBlock;

        isValid = true;

        this.replacedBlocks = replacedBlocks;

        this.logAttempts = logAttempts;
    }

    @SuppressWarnings("unused")
    public OreGenSettings(Map<String, Object> map) {
        replacedBlocks = new ArrayList<>();

        logAttempts = false;

        ore = Material.getMaterial(((String)map.get("ore")).toUpperCase());

        if (ore == null) {
            isValid = false;

            return;
        }

        radius = (double)map.get("radius");

        triesPerChunk = (int)map.get("triesPerChunk");
        maxPerChunk   = (int)map.get("maxPerChunk");

        if (triesPerChunk <  1) triesPerChunk = 1;
        if (triesPerChunk > 50) triesPerChunk = 50;

        if (maxPerChunk <  1) maxPerChunk = 1;
        if (maxPerChunk > 50) maxPerChunk = 50;

        percentChancePerTry   = ((double)map.get("percentChancePerTry"))   / 100.0d;
        percentChancePerBlock = ((double)map.get("percentChancePerBlock")) / 100.0d;

        if (percentChancePerTry   <= 0 || percentChancePerTry   > 1) percentChancePerTry   = 0.5d;
        if (percentChancePerBlock <= 0 || percentChancePerBlock > 1) percentChancePerBlock = 0.5d;

        if (map.containsKey("replacedBlocks")) {
            String[] blocks = ((String)map.get("replacedBlocks")).split("\\s*,\\s*");

            for(String block : blocks){
                Material material = Material.getMaterial(block.toUpperCase());

                if (material == null) continue;

                replacedBlocks.add(material);
            }
        }

        if (map.containsKey("logAttempts")) {
            logAttempts = Boolean.parseBoolean((String)map.get("logAttempts"));
        }

        isValid = true;
    }

    //endregion Constructors

    //region Public Fields

    public boolean isValid;

    public Material ore;

    public double radius;

    public int triesPerChunk;

    public int maxPerChunk;

    public double percentChancePerTry;

    public double percentChancePerBlock;

    public boolean logAttempts;

    //endregion Public Fields

    //region ConfigurationSerializable Implementation

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("ore", ore.toString());

        map.put("radius", radius);

        map.put("triesPerChunk", triesPerChunk);
        map.put("maxPerChunk",   maxPerChunk);

        map.put("percentChancePerTry",   percentChancePerTry   * 100.0d);
        map.put("percentChancePerBlock", percentChancePerBlock * 100.0d);

        if (!replacedBlocks.isEmpty()) {
            StringBuilder builder = new StringBuilder();

            for(Material mat : replacedBlocks) builder.append(mat.name()).append(",");

            String csv = builder.toString();

            map.put("replacedBlocks", csv.substring(0, csv.length() - 1));
        }

        if (logAttempts) {
            map.put("logAttempts", String.valueOf(true));
        }

        return map;
    }

    //endregion ConfigurationSerializable Implementation

    //region Public Methods

    public Material getOre(Material original) {
        return original == Material.DEEPSLATE || original == Material.TUFF ? getDeepSlateOre() : ore;
    }

    public Material getDeepSlateOre() {
        Material deepslateOre = deepslateOres.get(ore);

        return deepslateOre == null ? ore : deepslateOre;
    }

    public ArrayList<Material> getReplacedBlocks() {
        return replacedBlocks.isEmpty() ? defaultReplacedBlocks : replacedBlocks;
    }

    //endregion Public Methods

}

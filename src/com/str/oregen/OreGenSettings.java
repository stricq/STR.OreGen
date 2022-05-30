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
        defaultReplacedBlocks.add(Material.DEEPSLATE);
    }

    //endregion Static Initializers

    //region Constructors

    public OreGenSettings(Material ore, double radius, int triesPerChunk, int maxPerChunk, double percentChancePerTry, double percentChancePerBlock) {
        this.ore = ore;

        this.radius = radius;

        this.triesPerChunk = triesPerChunk;
        this.maxPerChunk   = maxPerChunk;

        this.percentChancePerTry = percentChancePerTry;
        this.percentChancePerBlock = percentChancePerBlock;

        isValid = true;

        replacedBlocks = new ArrayList<>();
    }

    public OreGenSettings(Material ore, double radius, int triesPerChunk, int maxPerChunk, double percentChancePerTry, double percentChancePerBlock, ArrayList<Material> replacedBlocks) {
        this.ore = ore;

        this.radius = radius;

        this.triesPerChunk = triesPerChunk;
        this.maxPerChunk   = maxPerChunk;

        this.percentChancePerTry = percentChancePerTry;
        this.percentChancePerBlock = percentChancePerBlock;

        isValid = true;

        this.replacedBlocks = replacedBlocks;
    }

    @SuppressWarnings("unused")
    public OreGenSettings(Map<String, Object> map) {
        replacedBlocks = new ArrayList<>();

        ore = Material.getMaterial(((String)map.get("ore")).toUpperCase());

        if (ore == null) {
            isValid = false;

            return;
        }

        radius = (double)map.get("radius");

        triesPerChunk = (int)map.get("triesPerChunk");
        maxPerChunk   = (int)map.get("maxPerChunk");

        percentChancePerTry   = (double)map.get("percentChancePerTry");
        percentChancePerBlock = (double)map.get("percentChancePerBlock");

        if (map.containsKey("replacedBlocks")) {
            String[] blocks = ((String)map.get("replacedBlocks")).split("\\s*,\\s*");

            for(String block : blocks){
                Material material = Material.getMaterial(block.toUpperCase());

                if (material == null) continue;

                replacedBlocks.add(material);
            }
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

    //endregion Public Fields

    //region ConfigurationSerializable Implementation

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("ore", ore.toString());

        map.put("radius", radius);

        map.put("triesPerChunk", triesPerChunk);
        map.put("maxPerChunk",   maxPerChunk);

        map.put("percentChancePerTry", percentChancePerTry);
        map.put("percentChancePerBlock", percentChancePerBlock);

        if (!replacedBlocks.isEmpty()) {
            StringBuilder builder = new StringBuilder();

            for(Material mat : replacedBlocks) builder.append(mat.name()).append(",");

            String csv = builder.toString();

            map.put("replacedBlocks", csv.substring(0, csv.length() - 1));
        }

        return map;
    }

    //endregion ConfigurationSerializable Implementation

    //region Public Methods

    public Material getOre(Material original) {
        return original == Material.DEEPSLATE ? getDeepSlateOre() : ore;
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

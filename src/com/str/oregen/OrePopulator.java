package com.str.oregen;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;

import java.util.Random;
import java.util.logging.Logger;


public class OrePopulator extends BlockPopulator {

    //region Private Fields

    private final int maxHeight;

    private final int offsetY;

    private final long baseSeed;

    private final Location center;
    private final Location point;

    private final Random random;

    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final Logger logger;

    private final OreGenSettings oreGenSettings;

    //endregion Private Fields

    //region Constructor

    public OrePopulator(OreGenSettings oreGenSettings, long seed, int worldMinHeight, int worldMaxHeight, Logger logger) {
        this.oreGenSettings = oreGenSettings;

        this.logger = logger;

        maxHeight = worldMaxHeight;

        String forHash = String.format("%s|%f|%d|%d|%f|%f", oreGenSettings.ore.name(), oreGenSettings.radius, oreGenSettings.triesPerChunk, oreGenSettings.maxPerChunk, oreGenSettings.percentChancePerTry, oreGenSettings.percentChancePerBlock);

        baseSeed = seed + ((long)forHash.hashCode() << 12);

        offsetY = worldMinHeight < 0 ? Math.abs(worldMinHeight) : 0;

        random = new Random();

        center = new Location(null, 0, 0, 0);
        point  = center.clone();

        logger.info("OrePopulator created for " + oreGenSettings.ore.name());
    }

    //endregion Constructor

    //region Overrides

    @Override
    public void populate(WorldInfo worldInfo, Random r, int chunkX, int chunkZ, LimitedRegion limitedRegion) {
//      logger.info("OrePopulator called for X:" + chunkX + ", Z:" + chunkZ + " with type " + material + ".");

        random.setSeed(baseSeed + ((long)chunkX << 24) + (long)chunkZ);

        final int maxRadius = (limitedRegion.getBuffer() * 2 + 16) / 2;

        final double radius = oreGenSettings.radius > maxRadius ? maxRadius : Math.max(oreGenSettings.radius, .5);

        int successCount = 0;

        for(int i = 0; i < oreGenSettings.triesPerChunk; ++i) {
            if (random.nextDouble() > oreGenSettings.percentChancePerTry) continue;

            int startX = random.nextInt(16) + (chunkX * 16);
            int startZ = random.nextInt(16) + (chunkZ * 16);

            int startY = random.nextInt(0, maxHeight + offsetY) - offsetY;

            center.setX(startX);
            center.setY(startY);
            center.setZ(startZ);

            if (!limitedRegion.isInRegion(center)) continue;

            boolean success = false;

            for(double x = -radius; x < radius; ++x) {
                for(double y = -radius; y < radius; ++y) {
                    for(double z = -radius; z < radius; ++z) {
                        point.setX(center.getX() + x);
                        point.setY(center.getY() + y);
                        point.setZ(center.getZ() + z);

                        // (x-cx) ^2 + (y-cy) ^2 + (z-cz) ^2 > r ^2
                        if (Math.pow(point.getX() - center.getX(), 2) + Math.pow(point.getY() - center.getY(), 2) + Math.pow(point.getZ() - center.getZ(), 2) > Math.pow(radius, 2)) continue;

                        if (random.nextDouble() > oreGenSettings.percentChancePerBlock) continue;

                        if (!limitedRegion.isInRegion(point)) continue;

                        Material startMaterial = limitedRegion.getType(point);

                        if (!oreGenSettings.getReplacedBlocks().contains(startMaterial)) continue;

                        limitedRegion.setType(point, oreGenSettings.getOre(startMaterial));

                        success = true;
                    }
                }
            }

            if (!success) continue;

//          logger.info("Try success for " + oreGenSettings.ore.name() + " at CX:" + chunkX + ", CZ:" + chunkZ + "; " + startX + "," + startY + "," + startZ);

            if (++successCount == oreGenSettings.maxPerChunk) break;
        }
    }

    //endregion Overrides

}

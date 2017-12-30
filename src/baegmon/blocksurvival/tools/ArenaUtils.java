package baegmon.blocksurvival.tools;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.BlockVector;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ArenaUtils {

    public static Location getRandomLocation(String world, BlockVector pos1, BlockVector pos2) {

        double minX = Math.min(pos1.getX(), pos2.getX());
        double minY = Math.min(pos1.getY() + 1, pos2.getY() + 1);
        double minZ = Math.min(pos1.getZ(), pos2.getZ());

        double maxX = Math.max(pos1.getX(), pos2.getX());
        double maxY = Math.max(pos1.getY() + 1, pos2.getY() + 1);
        double maxZ = Math.max(pos1.getZ(), pos2.getZ());

        boolean safe = false;
        Location location = null;

        while(!safe){
            location = new Location(Bukkit.getWorld(world), randomDouble(minX, maxX), randomDouble(minY, maxY), randomDouble(minZ, maxZ));
            safe = isSafeLocation(location);
        }

        return location;
    }


    public static Block getRandomBlock(String world, BlockVector pos1, BlockVector pos2) {

        double minX = Math.min(pos1.getX(), pos2.getX());
        double minY = Math.min(pos1.getY(), pos2.getY());
        double minZ = Math.min(pos1.getZ(), pos2.getZ());

        double maxX = Math.max(pos1.getX(), pos2.getX());
        double maxY = Math.max(pos1.getY(), pos2.getY());
        double maxZ = Math.max(pos1.getZ(), pos2.getZ());

        Location location = new Location(Bukkit.getWorld(world), randomDouble(minX, maxX), randomDouble(minY, maxY), randomDouble(minZ, maxZ));
        boolean notAir = false;

        while(!notAir){
            if(location.getBlock().getType() == Material.AIR){
                location = new Location(Bukkit.getWorld(world), randomDouble(minX, maxX), randomDouble(minY, maxY), randomDouble(minZ, maxZ));
            } else {
                notAir = true;
            }
        }

        return location.getBlock();
    }

    public static boolean insideArena(Location playerLocation, BlockVector pos1, BlockVector pos2) {
        double minX = Math.min(pos1.getX(), pos2.getX());
        double minY = Math.min(pos1.getY(), pos2.getY());
        double minZ = Math.min(pos1.getZ(), pos2.getZ());

        double maxX = Math.max(pos1.getX(), pos2.getX());
        double maxY = Math.max(pos1.getY(), pos2.getY());
        double maxZ = Math.max(pos1.getZ(), pos2.getZ());

        double px = playerLocation.getX();
        double py = playerLocation.getY();
        double pz = playerLocation.getZ();


        if( (px >= minX && px <= maxX) && (py >= minY) && (pz >= minZ && pz <= maxZ) ){
            return true;
        } else {
            return false;
        }
    }

    private static double randomDouble(double min, double max) {
        return min + ThreadLocalRandom.current().nextDouble(Math.abs(max - min + 1));
    }

    private static boolean isSafeLocation(Location location) {
        Block feet = location.getBlock();
        if (!feet.getType().isTransparent() && !feet.getType().isTransparent()) {
            return false; // not transparent (will suffocate)
        }
        Block head = feet.getRelative(BlockFace.UP);
        if (!head.getType().isTransparent()) {
            return false; // not transparent (will suffocate)
        }
        Block ground = feet.getRelative(BlockFace.DOWN);
        return ground.getType().isSolid();
    }

    public static ArrayList<BlockState> blocksFromTwoPoints(Location loc1, Location loc2)
    {
        ArrayList<BlockState> blocks = new ArrayList<>();

        int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
        int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());

        int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
        int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());

        int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
        int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());

        for(int x = bottomBlockX; x <= topBlockX; x++)
        {
            for(int z = bottomBlockZ; z <= topBlockZ; z++)
            {
                for(int y = bottomBlockY; y <= topBlockY; y++)
                {
                    Block block = loc1.getWorld().getBlockAt(x, y, z);

                    if(block.getType() != Material.AIR || block.getType() == Material.WATER || block.getType() == Material.LAVA){
                        blocks.add(block.getState());
                    }

                }
            }
        }

        return blocks;
    }

    public static FireworkMeta randomFireWorkEffect(Firework f){
        FireworkMeta fm = f.getFireworkMeta();
        fm.setPower(0);

        Random random = new Random();
        int effectAmount = random.nextInt(3) + 1;
        for(int i = 0; i < effectAmount; i++) {
            FireworkEffect.Builder b = FireworkEffect.builder();
            int colorAmount = random.nextInt(3) + 1;
            for(int ii = 0; ii < colorAmount; ii++) {
                b.withColor(Color.fromBGR(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            }
            b.with(FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)]);
            b.flicker(random.nextInt(2) != 0);
            b.trail(random.nextInt(2) != 0);
            fm.addEffect(b.build());
        }

        return fm;
    }

}

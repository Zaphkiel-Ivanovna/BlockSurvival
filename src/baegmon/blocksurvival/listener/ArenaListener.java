package baegmon.blocksurvival.listener;

import baegmon.blocksurvival.configuration.ConfigurationManager;
import baegmon.blocksurvival.game.*;
import baegmon.blocksurvival.manager.ArenaManager;
import baegmon.blocksurvival.tools.ArenaUtils;
import baegmon.blocksurvival.tools.Strings;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.util.Iterator;

public class ArenaListener implements Listener {

    /*
    Listens for whenever players break blocks and check
    if the block to break exists within the BlockSurvival Arena
    and cancels the event.

    TODO: Disable modifications caused from other plugins such as WorldEdit
    */

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event){

        Block b = event.getBlock();

        if(b.getType() == Material.SIGN || b.getType() == Material.SIGN_POST || b.getType() == Material.WALL_SIGN){

            for (final Iterator iterator = Global.INSTANCE.getSigns().iterator(); iterator.hasNext(); ) {
                ArenaSign sign = (ArenaSign) iterator.next();

                if(sign != null && sign.compareLocation(b.getLocation())){

                    Player player = event.getPlayer();

                    if(player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_ARENA_SETUP)){
                        player.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Sign has been removed successfully!");

                        FileConfiguration gameConfiguration = ConfigurationManager.INSTANCE.getConfiguration();
                        gameConfiguration.set("Game.Signs." + sign.getId(), null);

                        ConfigurationManager.INSTANCE.saveConfiguration();

                        iterator.remove();

                    } else {
                        event.setCancelled(true);
                    }
                }
            }

        } else {

            for(Arena arena : ArenaManager.INSTANCE.getArenas().values()){
                if(arena != null && arena.getArenaState() == ArenaState.ENABLED){
                    BlockVector pos1 = arena.getPos1();
                    BlockVector pos2 = arena.getPos2();

                    Vector v1 = BlockVector.getMaximum(pos1, pos2);
                    Vector v2 = BlockVector.getMinimum(pos1, pos2);

                    Vector v = new Vector(b.getX(), b.getY(), b.getZ());
                    boolean insideArena = v.isInAABB(v2, v1);

                    if(insideArena){
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    /* Use this if falling blocks is enabled later
    @EventHandler
    public void onFallingBlock(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.FALLING_BLOCK) {
            FallingBlock block = (FallingBlock) event.getEntity();
            if(block.hasMetadata("BlockSurvival")){
                event.setCancelled(true);
            }
        }
    }
    */

    @EventHandler
    public void onPlayerDamaged(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            for(Arena arena : ArenaManager.INSTANCE.getArenas().values()){
                if(arena != null && arena.playerInsideArena(player)){

                    // if player is waiting inside of an arena they should take no damage
                    if(arena.getGameState() == GameState.WAITING || arena.getGameState() == GameState.COUNTDOWN){
                        event.setCancelled(true);
                        player.teleport(ArenaUtils.getRandomLocation(
                                arena.getWorld(),
                                arena.getPos1(),
                                arena.getPos2()));
                    } else if (arena.getGameState() == GameState.STARTED){

                        String type = arena.getType();

                        if(type.equalsIgnoreCase("VOID") && event.getCause() == EntityDamageEvent.DamageCause.VOID){
                            arena.getGame().eliminatePlayer(player.getUniqueId());
                        } else if (type.equalsIgnoreCase("LAVA") && event.getCause() == EntityDamageEvent.DamageCause.LAVA){
                            arena.getGame().eliminatePlayer(player.getUniqueId());
                        }
                    }
                }
            }
        }
    }

    // Players inside of the lobby / game should not lose hunger
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if(event.getEntity() instanceof  Player){
            Player player = (Player) event.getEntity();
            for(Arena arena : ArenaManager.INSTANCE.getArenas().values()){
                if(arena != null && arena.playerInsideArena(player)){
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerFall(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        for(Arena arena : ArenaManager.INSTANCE.getArenas().values()){
            // check arena exists
            // check player inside of arena
            // check player is not a spectator
            // check arena hs started
            // check arena using floor
            // check player below y-level limit set by arena
            // check player not spectator
            if(arena != null && arena.playerInsideArena(player) && arena.getGameState() == GameState.STARTED && player.getGameMode() != GameMode.SPECTATOR && arena.usingFloor() && player.getLocation().getY() <= arena.getFloor()) {
                arena.getGame().eliminatePlayer(player.getUniqueId());
            }
        }
    }
}

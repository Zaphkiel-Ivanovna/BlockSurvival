package baegmon.blocksurvival.listener;

import baegmon.blocksurvival.game.Arena;
import baegmon.blocksurvival.game.ArenaState;
import baegmon.blocksurvival.game.GameState;
import baegmon.blocksurvival.manager.ArenaManager;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

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
        Player player = event.getPlayer();

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

    @EventHandler
    public void onFallingBlock(EntityChangeBlockEvent event) {
        if (event.getEntityType() == EntityType.FALLING_BLOCK) {
            FallingBlock block = (FallingBlock) event.getEntity();
            if(block.hasMetadata("BlockSurvival")){
                event.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onPlayerDamaged(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player) event.getEntity();
            for(Arena arena : ArenaManager.INSTANCE.getArenas().values()){
                if(arena != null && arena.playerInsideArena(player) && (arena.getGameState() == GameState.WAITING || arena.getGameState() == GameState.COUNTDOWN) ){
                    event.setCancelled(true);
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

}

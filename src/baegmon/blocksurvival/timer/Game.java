package baegmon.blocksurvival.timer;

import baegmon.blocksurvival.BlockPlugin;
import baegmon.blocksurvival.game.Arena;
import baegmon.blocksurvival.game.GameState;
import baegmon.blocksurvival.tools.ArenaUtils;
import baegmon.blocksurvival.tools.Strings;

import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Game extends BukkitRunnable {

    private Arena arena;
    private ArrayList<BlockState> blocks = new ArrayList<>();

    private Elimination elimination = new Elimination();

    private int dropCounter = 0;

    public Game(Arena arena){
        this.arena = arena;
    }

    // 0.1s = 2 tick
    // 0.25s = 5 tick
    // 0.5s = 10 tick
    // 1s = 20 tick
    // 2s = 40 tick

    void start(int difficulty){

        long tick;

        switch(difficulty){
            case 0:
                tick = 40L;
                break;
            case 1:
                tick = 20L;
                break;
            case 2:
                tick = 10L;
                break;
            case 3:
                tick = 5L;
                break;
            case 4:
                tick = 2L;
                break;
            case 5:
                tick = 1L;
                break;
            default:
                tick = 20L;
                break;
        }

        arena.setGameState(GameState.STARTED);
        arena.setRemaining(new HashSet<>(arena.getPlayers()));
        blocks = arena.getArenaBlocks();

        for(UUID id : arena.getPlayers()){
            Player player = Bukkit.getPlayer(id);
            player.setScoreboard(arena.generateGameScoreboard());

            // warp players back into arena that somehow managed to exit the arena area
            if(!ArenaUtils.insideArena(player.getLocation(), arena.getPos1(), arena.getPos2())){
                player.teleport(ArenaUtils.getRandomLocation(arena.getWorld(), arena.getPos1(), arena.getPos2()));
            }
        }

        this.runTaskTimer(BlockPlugin.getPlugin(BlockPlugin.class), 0L, tick);
        this.elimination.runTaskTimer(BlockPlugin.getPlugin(BlockPlugin.class), 0L, 5L);

    }

    @Override
    public void run() {

        if(arena.getGameState() == GameState.FINISHED){

            arena.setGameState(GameState.RESTORING);

            arena.removeScoreboard();

            Player winner = Bukkit.getPlayer(arena.getRemaining().iterator().next());
            arena.sendTitle(winner.getDisplayName() + " has won!", "", 120);
            winner.setGameMode(GameMode.SPECTATOR);

            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(BlockPlugin.getPlugin(BlockPlugin.class), new Runnable(){
                int counter = 0;

                @Override
                public void run() {
                    if(counter < 3){
                        Firework firework = winner.getWorld().spawn(winner.getLocation(), Firework.class);
                        firework.setFireworkMeta(ArenaUtils.randomFireWorkEffect(firework));
                        counter++;
                    } else {
                        cancel();
                    }
                }

            }, 0, 20L);

            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(BlockPlugin.getPlugin(BlockPlugin.class), () -> {
                elimination.cancel();
                cancel();
                arena.reset();
            }, 140L);

        }

        if(blocks.size() > 0 && arena.getGameState() == GameState.STARTED){

            if(arena.getSnapshotBlocks().size() > dropCounter){

                BlockState state = blocks.get(ThreadLocalRandom.current().nextInt(blocks.size()));

                Location location = state.getLocation();
                location.setX(location.getBlockX() + 0.5);
                location.setZ(location.getBlockZ() + 0.5);

                blocks.remove(state);

                MaterialData data = new MaterialData(state.getType());
                state.getBlock().setType(Material.AIR);

                FallingBlock fa = Bukkit.getWorld(arena.getWorld()).spawnFallingBlock(
                        location,
                        data);

                fa.setDropItem(false);
                fa.setMetadata("BlockSurvival", new FixedMetadataValue(BlockPlugin.getPlugin(BlockPlugin.class), "BlockSurvival"));

                dropCounter++;

            }

        }

    }

    public class Elimination extends BukkitRunnable {

        @Override
        public void run() {

            // only check if the game is currently running
            if(arena.getGameState() == GameState.STARTED && arena.usingFloor()){

                for (Iterator<UUID> iterator = arena.getRemaining().iterator(); iterator.hasNext();) {

                    UUID id = iterator.next();

                    Player player = Bukkit.getPlayer(id);

                    if(player.getLocation().getY() <= arena.getFloor()){
                        iterator.remove();
                        arena.getSpectating().add(id);
                        player.setGameMode(GameMode.SPECTATOR);

                        arena.sendMessage(Strings.PREFIX + ChatColor.WHITE + player.getDisplayName() + ChatColor.AQUA + " has been eliminated!");

                    }
                }

                arena.updateScoreboard();

                if(arena.getRemaining().size() == 1){
                    arena.setGameState(GameState.FINISHED);
                }

            }
        }
    }
}

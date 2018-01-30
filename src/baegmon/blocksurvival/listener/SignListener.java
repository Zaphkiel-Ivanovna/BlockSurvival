package baegmon.blocksurvival.listener;

import baegmon.blocksurvival.configuration.ConfigurationManager;
import baegmon.blocksurvival.game.Arena;
import baegmon.blocksurvival.game.ArenaSign;
import baegmon.blocksurvival.game.Global;
import baegmon.blocksurvival.manager.ArenaManager;
import baegmon.blocksurvival.tools.SignType;
import baegmon.blocksurvival.tools.Strings;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignListener implements Listener {

    @EventHandler
    public void onPlayerClickSign(PlayerInteractEvent event){

        if(event != null){

            if( event.getAction() == Action.RIGHT_CLICK_BLOCK && (event.getClickedBlock().getType() == Material.SIGN ||
                    event.getClickedBlock().getType() == Material.SIGN_POST ||
                    event.getClickedBlock().getType() == Material.WALL_SIGN) ){

                Sign sign = (Sign) event.getClickedBlock().getState();

                for(ArenaSign a : Global.INSTANCE.getSigns()){
                    if(a.compareLocation(sign.getLocation())){
                        Player player = event.getPlayer();

                        if(player.hasPermission(Strings.PERMISSION_ALL) ||
                                player.hasPermission(Strings.PERMISSION_USE_SIGN) ||
                                player.hasPermission(Strings.PERMISSION_ADMIN) ||
                                player.hasPermission(Strings.PERMISSION_PLAYER)){

                            if(a.getType().equals(SignType.JOIN)){

                                boolean playerAlreadyInGame = false;

                                for(Arena arena : ArenaManager.INSTANCE.getArenas().values()){
                                    if(arena.playerInsideArena(player)){
                                        player.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: You cannot join an arena while inside of an arena!");
                                        playerAlreadyInGame = true;
                                        break;
                                    }
                                }

                                if(!playerAlreadyInGame){
                                    Arena arena = ArenaManager.INSTANCE.getArena(a.getArena());
                                    arena.join(player);

                                    arena.updateSigns();
                                }

                            } else if (a.getType().equals(SignType.LEAVE)){

                                boolean playerInsideArena = false;

                                for(Arena arena : ArenaManager.INSTANCE.getArenas().values()){
                                    if(arena.playerInsideArena(player)){

                                        arena.leave(player);

                                        player.sendMessage(Strings.PREFIX + ChatColor.AQUA + "You have left " + ChatColor.WHITE + arena.getName() + ChatColor.AQUA + " !");
                                        playerInsideArena = true;

                                        arena.updateSigns();

                                        break;
                                    }
                                }

                                if(!playerInsideArena){
                                    player.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: You have not joined an arena!");
                                }

                            }

                        } else {
                            player.sendMessage(Strings.PREFIX + ChatColor.RED + "You do not have the permission to use this sign");
                        }

                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void signCreation(SignChangeEvent event) {

        Player player = event.getPlayer();

        if( ( event.getLine(0).equals("[bs]") || event.getLine(0).equals("[BlockSurvival]") ) &&
            ( player.hasPermission(Strings.PERMISSION_ALL) ||
                    player.hasPermission(Strings.PERMISSION_ADMIN) ||
                    player.hasPermission(Strings.PERMISSION_ARENA_SETUP) )) {

            String line2 = event.getLine(1);
            String line3 = event.getLine(2);

            if(line2.equalsIgnoreCase( "LEAVE") && line3.isEmpty()){

                String[] lines = event.getLines();
                lines[0] = Strings.PREFIX;
                lines[1] = ChatColor.RED + "LEAVE";

                ArenaSign sign = new ArenaSign(
                        Global.INSTANCE.getSignID(),
                        SignType.LEAVE,
                        event.getBlock().getLocation()
                );

                Global.INSTANCE.addSign(sign);

                FileConfiguration gameConfiguration = ConfigurationManager.INSTANCE.getConfiguration();
                gameConfiguration.set("Game.Signs." + sign.getId() + ".Type", sign.getType().toString());
                gameConfiguration.set("Game.Signs." + sign.getId() + ".World", sign.getWorld());
                gameConfiguration.set("Game.Signs." + sign.getId() + ".x", sign.getX());
                gameConfiguration.set("Game.Signs." + sign.getId() + ".y", sign.getY());
                gameConfiguration.set("Game.Signs." + sign.getId() + ".z", sign.getZ());

                ConfigurationManager.INSTANCE.saveConfiguration();

            } else if (line2.equalsIgnoreCase( "JOIN") && ArenaManager.INSTANCE.doesArenaExist(line3)){

                String[] lines = event.getLines();

                Arena arena = ArenaManager.INSTANCE.getArena(line3);

                lines[0] = Strings.PREFIX;
                lines[1] = ChatColor.GREEN + "JOIN";
                lines[2] = arena.getGameState().toString();
                lines[3] = arena.getPlayers().size() + " / " + arena.getMaxPlayers();

                ArenaSign sign = new ArenaSign(
                        Global.INSTANCE.getSignID(),
                        line3,
                        SignType.JOIN,
                        event.getBlock().getLocation()
                );

                Global.INSTANCE.addSign(sign);

                FileConfiguration gameConfiguration = ConfigurationManager.INSTANCE.getConfiguration();
                gameConfiguration.set("Game.Signs." + sign.getId() + ".Type", sign.getType().toString());
                gameConfiguration.set("Game.Signs." + sign.getId() + ".Arena", sign.getArena());
                gameConfiguration.set("Game.Signs." + sign.getId() + ".World", sign.getWorld());
                gameConfiguration.set("Game.Signs." + sign.getId() + ".x", sign.getX());
                gameConfiguration.set("Game.Signs." + sign.getId() + ".y", sign.getY());
                gameConfiguration.set("Game.Signs." + sign.getId() + ".z", sign.getZ());

                ConfigurationManager.INSTANCE.saveConfiguration();

            }
        }
    }
}

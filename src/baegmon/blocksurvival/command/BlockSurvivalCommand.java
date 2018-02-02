package baegmon.blocksurvival.command;

import baegmon.blocksurvival.configuration.ConfigurationManager;
import baegmon.blocksurvival.game.Arena;
import baegmon.blocksurvival.game.ArenaState;
import baegmon.blocksurvival.game.GameState;
import baegmon.blocksurvival.game.Global;
import baegmon.blocksurvival.manager.ArenaManager;
import baegmon.blocksurvival.tools.NumberUtils;
import baegmon.blocksurvival.tools.Strings;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

public class BlockSurvivalCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if(commandSender instanceof Player){

            Player player = (Player) commandSender;

            if(strings == null || strings.length == 0) {
                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "VERSION " + ChatColor.AQUA + Strings.VERSION);
                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "CREATED BY " + Strings.CREATOR);
                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Available Commands:");
                commandSender.sendMessage(Strings.PREFIX + ChatColor.AQUA  + "admin" + ChatColor.WHITE + " - display admin commands");
                commandSender.sendMessage(Strings.PREFIX + ChatColor.AQUA  + "player" + ChatColor.WHITE + " - display player commands");
                commandSender.sendMessage(Strings.PREFIX + ChatColor.AQUA  + "arena" + ChatColor.WHITE +  " - display arena setup commands");
            } else {

                String usage = strings[0];

                if(usage.equalsIgnoreCase("arena") && strings.length == 1){

                    commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Arena Setup Commands");

                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "setmainlobby: " + ChatColor.WHITE + "set current player position as the return point when a game finishes or player exits an arena");

                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "status <arena>: " + ChatColor.WHITE + "check the setup status of an arena");

                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "create <arena>: " + ChatColor.WHITE + "create an <arena>");
                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "delete <arena>: " + ChatColor.WHITE + "delete an <arena>");

                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "enable <arena>: " + ChatColor.WHITE + "enable an <arena>");
                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "disable <arena>: " +  ChatColor.WHITE + "disable an <arena>");

                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "lobby <arena>: " + ChatColor.WHITE + "set position as the waiting lobby of <arena>");

                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "pos1 <arena>: " + ChatColor.WHITE + "set position #1 of arena field");
                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "pos2 <arena>: " + ChatColor.WHITE + "set position #2 of arena field");

                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "setminplayers <arena> <amount>: " + ChatColor.WHITE + "set minimum number of players for the <arena>");
                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "setmaxplayers <arena> <amount>: " + ChatColor.WHITE + "set maximum number of players for the <arena>");

                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "difficulty <arena> <level (1 - 5)>: " + ChatColor.WHITE + "difficulty of the <arena> (1 = slowest, 5 = fastest)");
                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "usefloor <arena>: " + ChatColor.WHITE + "turns on/off height elimination of <arena>");
                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "floor <arena>: " + ChatColor.WHITE + "set current player height as an elimination zone of the <arena>");
                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "death <arena> <type>: " + ChatColor.WHITE + "set death floor of <arena> with <type:NONE/VOID/LAVA>");

                } else if (usage.equalsIgnoreCase("admin") && strings.length == 1){

                    commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Administrator Commands");

                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "forcestart <arena>: " + ChatColor.WHITE + "force-start <arena>");
                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "forcestop <arena>: " + ChatColor.WHITE + "force-stop <arena>");

                } else if (usage.equalsIgnoreCase("player") && strings.length == 1){

                    commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Player Commands");
                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "list: " + ChatColor.WHITE + "list all available arenas");
                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "join <arena>: " + ChatColor.WHITE + "join <arena>");
                    commandSender.sendMessage(Strings.COMMAND_PREFIX + "leave: " + ChatColor.WHITE + "leave current arena");

                } else {

                    ////////////////////////////////////////////////////////////////////////////
                    // LIST
                    ////////////////////////////////////////////////////////////////////////////

                    if(usage.equalsIgnoreCase("list")){

                        if(player.hasPermission(Strings.PERMISSION_ALL) ||
                                player.hasPermission(Strings.PERMISSION_ADMIN) ||
                                player.hasPermission(Strings.PERMISSION_PLAYER) ||
                                player.hasPermission(Strings.PERMISSION_LIST)){

                            if(strings.length == 1){

                                if(ArenaManager.INSTANCE.getArenas().isEmpty()){
                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "No arenas have been created.");
                                } else {

                                    StringBuilder builder = new StringBuilder();
                                    builder.append(Strings.PREFIX).append(ChatColor.WHITE).append("Available Arenas: ");

                                    for(Arena arena : ArenaManager.INSTANCE.getArenas().values()){
                                        builder.append(arena.getName()).append(" ");
                                    }

                                    commandSender.sendMessage(builder.toString());
                                }

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "list");
                            }

                        } else {
                            commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "You do not have the permission to run this command.");
                        }

                        return true;
                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // STATUS
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("status")){

                        if(player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_ARENA_SETUP)){
                            if(strings.length == 2){

                                String arenaName = strings[1];

                                if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: Arena " + ChatColor.WHITE + arenaName + ChatColor.RED + " does not exist.");
                                } else {

                                    Arena arena = ArenaManager.INSTANCE.getArenas().get(arenaName);

                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + " Arena Information: " + ChatColor.AQUA + arena.getName());

                                    if(Global.INSTANCE.isLobbyValid()){
                                        commandSender.sendMessage(ChatColor.WHITE + "Main Lobby: " + ChatColor.GOLD + Global.INSTANCE.getStringLobby() + ChatColor.GREEN + " [READY]");
                                    } else {
                                        commandSender.sendMessage(ChatColor.WHITE + "Main Lobby: " + ChatColor.GOLD + Global.INSTANCE.getStringLobby() + ChatColor.RED + " [NOT READY]");
                                    }

                                    if(arena.getArenaState() == ArenaState.ENABLED){
                                        commandSender.sendMessage(ChatColor.WHITE + "Active: " + ChatColor.GOLD + arena.getArenaState() + ChatColor.GREEN + " [READY]");
                                    } else {
                                        commandSender.sendMessage(ChatColor.WHITE + "Active: " + ChatColor.GOLD + arena.getArenaState() + ChatColor.RED + " [NOT READY]");
                                    }

                                    if(arena.isWorldValid()){
                                        commandSender.sendMessage(ChatColor.WHITE + "World: " + ChatColor.GOLD + arena.getWorld() + ChatColor.GREEN + " [READY]");
                                    } else {
                                        commandSender.sendMessage(ChatColor.WHITE + "World: " + ChatColor.GOLD + arena.getWorld() + ChatColor.RED + " [NOT READY]");
                                    }

                                    if(arena.getDifficulty() > 0){
                                        commandSender.sendMessage(ChatColor.WHITE + "Difficulty: " + ChatColor.GOLD + arena.getDifficulty() + ChatColor.GREEN + " [READY]");
                                    } else {
                                        commandSender.sendMessage(ChatColor.WHITE + "Difficulty: " + ChatColor.GOLD + arena.getDifficulty() + ChatColor.RED + " [NOT READY]");
                                    }

                                    if(arena.isLobbyValid()){
                                        commandSender.sendMessage(ChatColor.WHITE + "Using Lobby: " + ChatColor.GOLD + arena.getLobbyString() + ChatColor.GREEN + " [READY]");
                                    } else {
                                        commandSender.sendMessage(ChatColor.WHITE + "Using Lobby: " + ChatColor.RED + " [FALSE]");
                                    }

                                    if(arena.isPos1Valid()){
                                        commandSender.sendMessage(ChatColor.WHITE + "Position 1: " + ChatColor.GOLD + arena.getStringPos1() + ChatColor.GREEN + " [READY]");
                                    } else {
                                        commandSender.sendMessage(ChatColor.WHITE + "Position 1: " + ChatColor.GOLD + arena.getStringPos1() + ChatColor.RED + " [NOT READY]");
                                    }

                                    if(arena.isPos2Valid()){
                                        commandSender.sendMessage(ChatColor.WHITE + "Position 2: " + ChatColor.GOLD + arena.getStringPos2() + ChatColor.GREEN + " [READY]");
                                    } else {
                                        commandSender.sendMessage(ChatColor.WHITE + "Position 2: " + ChatColor.GOLD + arena.getStringPos2() + ChatColor.RED + " [NOT READY]");
                                    }

                                    if(arena.usingFloor()){
                                        commandSender.sendMessage(ChatColor.WHITE + "Using Floor: " + ChatColor.GREEN + "[TRUE]");
                                        commandSender.sendMessage(ChatColor.WHITE + "Floor (Y-Value): " + ChatColor.GOLD + arena.getFloor());
                                    } else {
                                        commandSender.sendMessage(ChatColor.WHITE + "Using Floor: " + ChatColor.RED + "[FALSE]");
                                    }

                                    if(!arena.getType().isEmpty()){
                                        commandSender.sendMessage(ChatColor.WHITE + "Death Floor Type: " + ChatColor.GOLD + arena.getType());
                                    }

                                    if(arena.isPlayerRequirementsValid()){
                                        commandSender.sendMessage(ChatColor.WHITE + "Minimum Players Required: " + ChatColor.GOLD + arena.getMinPlayers() + ChatColor.GREEN + " [READY]");
                                        commandSender.sendMessage(ChatColor.WHITE + "Maximum Players Required: " + ChatColor.GOLD + arena.getMaxPlayers() + ChatColor.GREEN + " [READY]");
                                    } else {

                                        if(arena.getMinPlayers() > 0){
                                            commandSender.sendMessage(ChatColor.WHITE + "Minimum Players Required: " + ChatColor.GOLD + arena.getMinPlayers() + ChatColor.GREEN + " [READY]");
                                        } else {
                                            commandSender.sendMessage(ChatColor.WHITE + "Minimum Players Required: " + ChatColor.GOLD + arena.getMinPlayers() + ChatColor.RED + " [NOT READY]");
                                        }

                                        if(arena.getMaxPlayers() > 0 && arena.getMaxPlayers() >= arena.getMinPlayers()){
                                            commandSender.sendMessage(ChatColor.WHITE + "Maximum Players Required: " + ChatColor.GOLD + arena.getMaxPlayers() + ChatColor.GREEN + " [READY]");
                                        } else {
                                            commandSender.sendMessage(ChatColor.WHITE + "Maximum Players Required: " + ChatColor.GOLD + arena.getMaxPlayers() + ChatColor.RED + " [NOT READY]");
                                        }

                                        if(arena.getMinPlayers() >= arena.getMaxPlayers()){
                                            commandSender.sendMessage(ChatColor.RED + "Minimum players required cannot be higher than maximum players required!");
                                        }
                                    }

                                }

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "status <arena>");
                            }
                        } else {
                            commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "You do not have the permission to run this command.");
                        }

                        return true;
                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // CREATE
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("create")){

                        if(player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_ARENA_SETUP)){
                            if(strings.length == 2){

                                String arenaName = strings[1];
                                String worldName = player.getWorld().getName();

                                if(ArenaManager.INSTANCE.getArena(arenaName) == null){

                                    Arena arena = new Arena(arenaName);
                                    arena.setWorld(worldName);

                                    ArenaManager.INSTANCE.addArena(arenaName, arena);
                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.GREEN + "Arena " + ChatColor.WHITE + arenaName + ChatColor.GREEN + " created successfully.");

                                    FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                    arenaConfiguration.set("Arenas." + arenaName + ".Enabled", false);
                                    arenaConfiguration.set("Arenas." + arenaName + ".World", worldName);
                                    arenaConfiguration.set("Arenas." + arenaName + ".LobbyTime", 30);
                                    arenaConfiguration.set("Arenas." + arenaName + ".Difficulty", 1);
                                    arenaConfiguration.set("Arenas." + arenaName + ".UsingFloor", false);
                                    arenaConfiguration.set("Arenas." + arenaName + ".Floor", 0);
                                    arenaConfiguration.set("Arenas." + arenaName + ".Type", "NONE");

                                    ConfigurationManager.INSTANCE.saveArenaConfiguration();
                                } else {
                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Arena " + ChatColor.WHITE + arenaName +  ChatColor.RED + " already exists.");
                                }

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "create <arena>");
                            }
                        } else {
                            commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "You do not have the permission to run this command.");
                        }

                        return true;
                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // DELETE
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("delete")){

                        if(player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_ARENA_SETUP)){
                            if(strings.length == 2){

                                String arenaName = strings[1];

                                if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: Arena " + ChatColor.WHITE + " " + arenaName +
                                            ChatColor.RED + " does not exist.");


                                } else {
                                    ArenaManager.INSTANCE.removeArena(arenaName);
                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.GREEN + "Arena " + ChatColor.WHITE + arenaName + ChatColor.GREEN + " deleted.");


                                    FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                    arenaConfiguration.set("Arenas." + arenaName, null);

                                    ConfigurationManager.INSTANCE.saveArenaConfiguration();

                                }

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "delete <arena>");
                            }
                        } else {
                            commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "You do not have the permission to run this command.");
                        }
                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // ENABLE
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("enable")){

                        if(player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_ARENA_SETUP)){
                            if(strings.length == 2){

                                String arenaName = strings[1];

                                if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: Arena " + ChatColor.WHITE + " " + arenaName +
                                            ChatColor.RED + " does not exist.");
                                } else {

                                    Arena arena = ArenaManager.INSTANCE.getArena(arenaName);

                                    if(arena.getArenaState() == ArenaState.DISABLED){
                                        arena.setArenaState(ArenaState.ENABLED);
                                        commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Arena " + ChatColor.GOLD + arenaName + ChatColor.GREEN + " enabled.");

                                        FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                        arenaConfiguration.set("Arenas." + arenaName + ".Enabled", true);
                                        ConfigurationManager.INSTANCE.saveArenaConfiguration();

                                        arena.saveArenaBlocks();

                                    } else {
                                        commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Arena " + ChatColor.GOLD + arenaName + ChatColor.RED + " is already enabled!");
                                    }

                                }

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "enable <arena>");
                            }
                        } else {
                            commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "You do not have the permission to run this command.");
                        }
                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // DISABLE
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("disable")){

                        if(player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_ARENA_SETUP)){
                            if(strings.length == 2){

                                String arenaName = strings[1];

                                if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: Arena " + ChatColor.WHITE + " " + arenaName +
                                            ChatColor.RED + " does not exist.");
                                } else {
                                    Arena arena = ArenaManager.INSTANCE.getArena(arenaName);

                                    if(arena.getArenaState() == ArenaState.DISABLED){
                                        commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Arena " + ChatColor.GOLD + arenaName + ChatColor.RED + " is already disabled!");
                                    } else {
                                        arena.setArenaState(ArenaState.DISABLED);
                                        commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Arena " + ChatColor.GOLD + arenaName + ChatColor.RED + " disabled.");

                                        FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                        arenaConfiguration.set("Arenas." + arenaName + ".Enabled", false);
                                        ConfigurationManager.INSTANCE.saveArenaConfiguration();
                                    }

                                }

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "disable <arena>");
                            }
                        } else {
                            commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "You do not have the permission to run this command.");
                        }

                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // FORCE START
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("forcestart")){

                        if(player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_FORCE_START)){
                            if(strings.length == 2){

                                String arenaName = strings[1];

                                if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: Arena " + ChatColor.WHITE + " " + arenaName +
                                            ChatColor.RED + " does not exist.");
                                } else {
                                    Arena arena = ArenaManager.INSTANCE.getArena(arenaName);

                                    if(arena.getArenaState().equals(ArenaState.ENABLED) && arena.getGameState().equals(GameState.WAITING)){
                                        if(arena.getPlayers().size() >= 2){
                                            arena.forceStart();
                                            commandSender.sendMessage(Strings.PREFIX + ChatColor.GOLD + arenaName +  ChatColor.WHITE + " has been force-started!");
                                        } else {
                                            commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: You can only force-start an arena if it has at least two players.");
                                        }
                                    } else {
                                        commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: You can only force-start an arena that is enabled and is waiting.");
                                    }

                                }

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "forcestart <arena>");
                            }
                        } else {
                            commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "You do not have the permission to run this command.");
                        }
                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // FORCE STOP
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("forcestop")){

                        if(player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_FORCE_STOP)){
                            if(strings.length == 2){

                                String arenaName = strings[1];

                                if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: Arena " + ChatColor.WHITE + " " + arenaName +
                                            ChatColor.RED + " does not exist.");
                                } else {
                                    Arena arena = ArenaManager.INSTANCE.getArena(arenaName);

                                    if(arena.getArenaState().equals(ArenaState.ENABLED) && arena.getGameState().equals(GameState.STARTED)){
                                        arena.reset();
                                        commandSender.sendMessage(Strings.PREFIX + ChatColor.GOLD + arenaName +  ChatColor.WHITE + " has been force-stopped!");
                                    } else {
                                        commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: You can only force-stop an arena that is enabled and is in-game.");
                                    }

                                }

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "forcestop <arena>");
                            }
                        } else {
                            commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "You do not have the permission to run this command.");
                        }
                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // SET MAIN LOBBY
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("setmainlobby")){

                        if(player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_ARENA_SETUP)){
                            if(strings.length == 1){

                                BlockVector lobby = player.getLocation().toVector().toBlockVector();
                                String worldName = player.getWorld().getName();

                                Global.INSTANCE.setWorld(worldName);
                                Global.INSTANCE.setLobby(lobby);

                                FileConfiguration configuration = ConfigurationManager.INSTANCE.getConfiguration();
                                configuration.set("Game.Lobby.World", worldName);
                                configuration.set("Game.Lobby.x", lobby.getX());
                                configuration.set("Game.Lobby.y", lobby.getY());
                                configuration.set("Game.Lobby.z", lobby.getZ());

                                ConfigurationManager.INSTANCE.saveConfiguration();

                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Main Lobby set to " + ChatColor.GOLD + Global.INSTANCE.getStringLobby());

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "setmainlobby");
                            }

                        } else {
                            commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "You do not have the permission to run this command.");
                        }

                        return true;
                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // SET LOBBY
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("lobby")){

                        if(player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_ARENA_SETUP)){
                            String arenaName = strings[1];

                            if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: Arena " + ChatColor.WHITE + arenaName + ChatColor.RED + " does not exist.");
                            } else {

                                Arena arena = ArenaManager.INSTANCE.getArena(arenaName);

                                Location lobby = player.getLocation();
                                arena.setLobby(lobby);

                                commandSender.sendMessage(Strings.PREFIX + ChatColor.GREEN + "Lobby of " + ChatColor.WHITE + arenaName +  ChatColor.GREEN + " set to " + arena.getLobbyString());

                                FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                arenaConfiguration.set("Arenas." + arenaName + ".Lobby.World", lobby.getWorld().getName());
                                arenaConfiguration.set("Arenas." + arenaName + ".Lobby.x", lobby.getBlockX());
                                arenaConfiguration.set("Arenas." + arenaName + ".Lobby.y", lobby.getBlockY());
                                arenaConfiguration.set("Arenas." + arenaName + ".Lobby.z", lobby.getBlockZ());

                                ConfigurationManager.INSTANCE.saveArenaConfiguration();
                            }

                        } else {
                            commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "You do not have the permission to run this command.");
                        }

                        return true;
                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // DIFFICULTY
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("difficulty")){

                        if(player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_ARENA_SETUP)){
                            if(strings.length == 3){

                                String arenaName = strings[1];
                                String number = strings[2];
                                boolean isInteger = NumberUtils.isInteger(number);

                                if(isInteger){
                                    int difficulty = Integer.parseInt(number);

                                    if(difficulty < 0){
                                        commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Difficulty of the arena cannot be lower than 0!");
                                    } else if (difficulty > 5) {
                                        commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Difficulty of the arena cannot be greater than 5!");
                                    } else {

                                        if(ArenaManager.INSTANCE.getArena(arenaName) ==  null){
                                            commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: Arena " + ChatColor.WHITE + arenaName +
                                                    ChatColor.RED + " does not exist.");
                                        } else {

                                            ArenaManager.INSTANCE.getArena(arenaName).setDifficulty(difficulty);
                                            commandSender.sendMessage(Strings.PREFIX + ChatColor.GREEN + "Difficulty of " + ChatColor.WHITE + arenaName
                                                    + ChatColor.GREEN + " set to " + difficulty);

                                            FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                            arenaConfiguration.set("Arenas." + arenaName + ".Difficulty", difficulty);
                                            ConfigurationManager.INSTANCE.saveArenaConfiguration();
                                        }

                                    }

                                } else {
                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: " + ChatColor.WHITE + number + ChatColor.RED + " is not a valid number.");
                                }

                                return true;

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "difficulty <arena> <amount>");
                            }
                        } else {
                            commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "You do not have the permission to run this command.");
                        }
                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // POS1
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("pos1")){

                        if(player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_ARENA_SETUP)){
                            if(strings.length == 2){

                                String arenaName = strings[1];

                                if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: Arena " + ChatColor.WHITE + arenaName +
                                            ChatColor.RED + " does not exist.");
                                } else {

                                    Arena arena = ArenaManager.INSTANCE.getArena(arenaName);

                                    BlockVector pos1 = player.getLocation().clone().subtract(0, 1, 0).toVector().toBlockVector();
                                    arena.setPos1(pos1);

                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.GREEN + "Pos1 of " + ChatColor.WHITE + arenaName +  ChatColor.GREEN + " set to " + arena.getStringPos1());

                                    FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                    arenaConfiguration.set("Arenas." + arenaName + ".Position1.x", pos1.getBlockX());
                                    arenaConfiguration.set("Arenas." + arenaName + ".Position1.y", pos1.getBlockY());
                                    arenaConfiguration.set("Arenas." + arenaName + ".Position1.z", pos1.getBlockZ());

                                    ConfigurationManager.INSTANCE.saveArenaConfiguration();

                                    arena.saveArenaBlocks();
                                }

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "pos1 <arena>");
                            }
                        } else {
                            commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "You do not have the permission to run this command.");
                        }

                        return true;
                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // POS2
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("pos2")){

                        if(player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_ARENA_SETUP)){
                            if(strings.length == 2){

                                String arenaName = strings[1];

                                if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: Arena " + ChatColor.WHITE + arenaName +
                                            ChatColor.RED + " does not exist.");
                                } else {
                                    Arena arena = ArenaManager.INSTANCE.getArena(arenaName);

                                    BlockVector pos2 = player.getLocation().clone().subtract(0, 1, 0).toVector().toBlockVector();
                                    arena.setPos2(pos2);

                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.GREEN + "Pos2 of " + ChatColor.WHITE + arenaName +  ChatColor.GREEN + " set to " + arena.getStringPos2());

                                    FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                    arenaConfiguration.set("Arenas." + arenaName + ".Position2.x", pos2.getBlockX());
                                    arenaConfiguration.set("Arenas." + arenaName + ".Position2.y", pos2.getBlockY());
                                    arenaConfiguration.set("Arenas." + arenaName + ".Position2.z", pos2.getBlockZ());

                                    ConfigurationManager.INSTANCE.saveArenaConfiguration();

                                    arena.saveArenaBlocks();

                                }

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "pos2 <arena>");
                            }
                        } else {
                            commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "You do not have the permission to run this command.");
                        }

                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // USE FLOOR
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("usefloor")){

                        if(player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_ARENA_SETUP)){
                            if(strings.length == 2){

                                String arenaName = strings[1];

                                if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: Arena " + ChatColor.WHITE + arenaName +
                                            ChatColor.RED + " does not exist.");
                                } else {
                                    Arena arena = ArenaManager.INSTANCE.getArena(arenaName);

                                    boolean usingFloor = arena.usingFloor();

                                    if(usingFloor){
                                        commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Height elimination zone of " + ChatColor.WHITE + arenaName +  ChatColor.RED + " has been turned off!");
                                    } else {
                                        commandSender.sendMessage(Strings.PREFIX + ChatColor.GREEN + "Height elimination zone of " + ChatColor.WHITE + arenaName +  ChatColor.GREEN + " has been turned on!");
                                    }

                                    arena.setFloorUsage(!usingFloor);

                                    FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                    arenaConfiguration.set("Arenas." + arenaName + ".UsingFloor", arena.usingFloor());

                                    ConfigurationManager.INSTANCE.saveArenaConfiguration();
                                }

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "usefloor <arena>");
                            }
                        } else {
                            commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "You do not have the permission to run this command.");
                        }

                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // FLOOR
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("floor")){

                        if(player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_ARENA_SETUP)){
                            if(strings.length == 2){

                                String arenaName = strings[1];

                                if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: Arena " + ChatColor.WHITE + arenaName +
                                            ChatColor.RED + " does not exist.");
                                } else {
                                    Arena arena = ArenaManager.INSTANCE.getArena(arenaName);
                                    arena.setFloorUsage(true);
                                    arena.setFloor(player.getLocation().getBlockY());

                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.GREEN + "Floor of " + ChatColor.WHITE + arenaName +  ChatColor.GREEN + " set to " + arena.getFloor());

                                    FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                    arenaConfiguration.set("Arenas." + arenaName + ".UsingFloor", true);
                                    arenaConfiguration.set("Arenas." + arenaName + ".Floor", arena.getFloor());

                                    ConfigurationManager.INSTANCE.saveArenaConfiguration();
                                }

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "floor <arena>");
                            }
                        } else {
                            commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "You do not have the permission to run this command.");
                        }

                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // DEATH
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("death")){

                        if(player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_ARENA_SETUP)) {
                            if(strings.length == 3){

                                String arenaName = strings[1];
                                String type = strings[2];

                                if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: Arena " + ChatColor.WHITE + arenaName + ChatColor.RED + " does not exist.");
                                } else {

                                    if(type.equalsIgnoreCase("VOID") || type.equalsIgnoreCase("NONE") || type.equalsIgnoreCase("LAVA")){
                                        Arena arena = ArenaManager.INSTANCE.getArena(arenaName);
                                        arena.setType(type);

                                        commandSender.sendMessage(Strings.PREFIX + ChatColor.GREEN + "Death floor type of " + ChatColor.WHITE + arenaName +  ChatColor.GREEN + " set to " + type);

                                        FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                        arenaConfiguration.set("Arenas." + arenaName + ".Type", arena.getType());

                                        ConfigurationManager.INSTANCE.saveArenaConfiguration();

                                    } else {
                                        commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: Type " + ChatColor.WHITE + type + ChatColor.RED + " is not a valid death type.");
                                    }
                                }

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "type <death> <type:NONE/VOID/LAVA>");
                            }
                        } else {
                            commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "You do not have the permission to run this command.");
                        }

                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // MINPLAYERS
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("setminplayers")) {

                        if (player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_ARENA_SETUP)) {
                            if (strings.length == 3) {

                                String arenaName = strings[1];
                                String number = strings[2];
                                boolean isInteger = NumberUtils.isInteger(number);

                                if (isInteger) {
                                    int amount = Integer.parseInt(strings[2]);

                                    if (ArenaManager.INSTANCE.getArena(arenaName) == null) {
                                        commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: Arena " + ChatColor.WHITE + arenaName +
                                                ChatColor.RED + " does not exist.");
                                    } else {

                                        if (amount <= 1) {
                                            commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: Minimum number of players cannot be lower than 1.");
                                        } else {
                                            Arena arena = ArenaManager.INSTANCE.getArena(arenaName);
                                            arena.setMinPlayers(amount);
                                            commandSender.sendMessage(Strings.PREFIX + ChatColor.GREEN + "Minimum number of players for " + ChatColor.WHITE + arenaName
                                                    + ChatColor.GREEN + " set to " + amount);

                                            FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                            arenaConfiguration.set("Arenas." + arenaName + ".MinimumPlayers", amount);
                                            ConfigurationManager.INSTANCE.saveArenaConfiguration();
                                        }
                                    }

                                } else {
                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: " + ChatColor.WHITE + number + ChatColor.RED + " is not a valid number.");
                                }

                                return true;

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "setminplayers <arena> <amount>");
                            }
                        } else {
                            commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "You do not have the permission to run this command.");
                        }

                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // MAXPLAYERS
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("setmaxplayers")){

                        if (player.hasPermission(Strings.PERMISSION_ALL) || player.hasPermission(Strings.PERMISSION_ADMIN) || player.hasPermission(Strings.PERMISSION_ARENA_SETUP)) {
                            if(strings.length == 3){

                                String arenaName = strings[1];
                                String number = strings[2];
                                boolean isInteger = NumberUtils.isInteger(number);

                                if(isInteger){
                                    int amount = Integer.parseInt(strings[2]);

                                    if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                        commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: Arena " + ChatColor.WHITE + arenaName + ChatColor.RED + " does not exist.");
                                    } else {

                                        if(amount <= 1){
                                            commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: Minimum number of players cannot be lower than 1.");
                                        }

                                        Arena arena = ArenaManager.INSTANCE.getArena(arenaName);
                                        arena.setMaxPlayers(amount);
                                        commandSender.sendMessage(Strings.PREFIX + ChatColor.GREEN + "Maximum number of players for " + ChatColor.WHITE + arenaName + ChatColor.GREEN + " set to " + amount);

                                        FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                        arenaConfiguration.set("Arenas." + arenaName + ".MaximumPlayers", amount);
                                        ConfigurationManager.INSTANCE.saveArenaConfiguration();
                                    }

                                } else {
                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: " + ChatColor.WHITE + number + ChatColor.RED + " is not a valid number.");
                                }

                                return true;

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "setmaxplayers <arena> <amount>");
                            }
                        } else {
                            commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "You do not have the permission to run this command.");
                        }

                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // JOIN
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("join")){

                        if (player.hasPermission(Strings.PERMISSION_ALL) ||
                                player.hasPermission(Strings.PERMISSION_ADMIN) ||
                                player.hasPermission(Strings.PERMISSION_PLAYER) ||
                                player.hasPermission(Strings.PERMISSION_JOIN)) {

                            if(strings.length == 2){

                                boolean playerAlreadyInGame = false;

                                for(Arena arena : ArenaManager.INSTANCE.getArenas().values()){
                                    if(arena.playerInsideArena(player)){
                                        commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: You cannot join an arena while inside of an arena!");
                                        playerAlreadyInGame = true;
                                        break;
                                    }
                                }

                                if(!playerAlreadyInGame){

                                    if(Global.INSTANCE.isLobbyValid()) {
                                        String arenaName = strings[1];

                                        if(ArenaManager.INSTANCE.getArenas().containsKey(arenaName)){
                                            Arena arena = ArenaManager.INSTANCE.getArenas().get(arenaName);

                                            if(arena.isArenaValid()){

                                                if(arena.joinable()){
                                                    if(arena.getPlayers().size() < arena.getMaxPlayers()){
                                                        arena.join(player);
                                                    } else {
                                                        commandSender.sendMessage(Strings.PREFIX + ChatColor.GOLD + arena.getName() + ChatColor.RED + " cannot be joined because it is full!");
                                                    }
                                                } else {
                                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.GOLD + arena.getName() + ChatColor.RED + " has already started!");
                                                }
                                            } else {
                                                commandSender.sendMessage(Strings.PREFIX + ChatColor.GOLD + arena.getName() + ChatColor.RED + " cannot be started because it is currently invalid!");
                                            }

                                        } else {
                                            commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: " + ChatColor.GOLD + arenaName + ChatColor.RED + " does not exist.");
                                        }

                                    } else{
                                        commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + Strings.LOBBY_INVALID);
                                    }
                                }

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "join <arena>");
                            }
                        } else {
                            commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "You do not have the permission to run this command.");
                        }

                        return true;
                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // LEAVE
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("leave")){

                        if (player.hasPermission(Strings.PERMISSION_ALL) ||
                                player.hasPermission(Strings.PERMISSION_ADMIN) ||
                                player.hasPermission(Strings.PERMISSION_PLAYER)||
                                player.hasPermission(Strings.PERMISSION_LEAVE)) {

                            if(strings.length == 1){

                                boolean playerInsideArena = false;

                                for(Arena arena : ArenaManager.INSTANCE.getArenas().values()){
                                    if(arena.playerInsideArena(player)){

                                        arena.leave(player);
                                        playerInsideArena = true;
                                        break;
                                    }
                                }

                                if(!playerInsideArena){
                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Error: You have not joined an arena!");
                                }

                            } else {
                                commandSender.sendMessage(Strings.INCORRECT_ARGUMENTS);
                                commandSender.sendMessage(Strings.PREFIX + ChatColor.WHITE + "Usage: " + Strings.COMMAND_PREFIX + ChatColor.WHITE + "leave");
                            }
                        } else {
                            commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "You do not have the permission to run this command.");
                        }

                        return true;
                    }

                    // No such command exists
                    else {
                        commandSender.sendMessage( Strings.PREFIX + ChatColor.RED + "Error: " + ChatColor.WHITE + usage + ChatColor.RED + " is not a valid command");
                    }

                }

            }

        } else {
            commandSender.sendMessage(Strings.PREFIX + ChatColor.RED + "Commands can only be executed as a player!");
        }

        return true;
    }

}

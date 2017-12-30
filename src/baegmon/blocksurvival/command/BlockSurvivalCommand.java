package baegmon.blocksurvival.command;

import baegmon.blocksurvival.configuration.ConfigurationManager;
import baegmon.blocksurvival.game.Arena;
import baegmon.blocksurvival.game.ArenaState;
import baegmon.blocksurvival.game.GlobalSettings;
import baegmon.blocksurvival.manager.ArenaManager;
import baegmon.blocksurvival.tools.ArenaUtils;
import baegmon.blocksurvival.tools.NumberUtils;
import baegmon.blocksurvival.tools.Strings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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
                commandSender.sendMessage("Available commands:");
                commandSender.sendMessage(ChatColor.GREEN + "/BlockSurvival admin - display all admin commands");
                commandSender.sendMessage(ChatColor.GREEN + "/BlockSurvival player - display all player commands");
                commandSender.sendMessage(ChatColor.GREEN + "/BlockSurvival arena - display all arena setup commands");
            } else {

                String usage = strings[0];

                if(usage.equalsIgnoreCase("arena") && strings.length == 1){

                    commandSender.sendMessage(ChatColor.GREEN + "/BlockSurvival status <arena> - check the setup status of an arena");
                    commandSender.sendMessage(ChatColor.GREEN + "/BlockSurvival list - list all available arenas");

                    commandSender.sendMessage(ChatColor.GREEN + "/BlockSurvival create <arena> - create an <arena>");
                    commandSender.sendMessage(ChatColor.GREEN + "/BlockSurvival delete <arena> - delete an <arena>");

                    commandSender.sendMessage(ChatColor.GREEN + "/BlockSurvival enable <arena> - enable an <arena>");
                    commandSender.sendMessage(ChatColor.GREEN + "/BlockSurvival disable <arena> - disable an <arena>");

                    commandSender.sendMessage(ChatColor.GREEN + "/BlockSurvival delete <arena> - delete an <arena>");
                    commandSender.sendMessage(ChatColor.GREEN + "/BlockSurvival setmainlobby - set current player position as point to return when game finish or player exits an arena");
                    commandSender.sendMessage(ChatColor.GREEN + "/BlockSurvival pos1 <arena> - set position #1 of arena field");
                    commandSender.sendMessage(ChatColor.GREEN + "/BlockSurvival pos2 <arena> - set position #2 of arena field");
                    commandSender.sendMessage(ChatColor.GREEN + "/BlockSurvival floor <arena> - set current Y-value (height) as the floor of the <arena>");
                    commandSender.sendMessage(ChatColor.GREEN + "/BlockSurvival setminplayers <arena> <amount> - set minimum number of players for the <arena>");
                    commandSender.sendMessage(ChatColor.GREEN + "/BlockSurvival setmaxplayers <arena> <amount> - set maximum number of players for the <arena>");
                    commandSender.sendMessage(ChatColor.GREEN + "/BlockSurvival difficulty <arena> <level (1 ~ 5)> - difficulty of the <arena> (1 = slowest, 5 = fastest)");

                } else if (usage.equalsIgnoreCase("admin") && strings.length == 1){
                    commandSender.sendMessage("Available administrator commands:");
                    commandSender.sendMessage(ChatColor.GREEN + "/BlockSurvival forcestart <name> - forcestart arena <name>");
                } else if (usage.equalsIgnoreCase("player") && strings.length == 1){
                    commandSender.sendMessage("Available player commands:");
                    commandSender.sendMessage(ChatColor.GREEN + "/BlockSurvival join <arena> - join <arena>");
                    commandSender.sendMessage(ChatColor.GREEN + "/BlockSurvival leave - leave current arena");
                    commandSender.sendMessage(ChatColor.GREEN + "/BlockSurvival stats - display player stats");
                } else {

                    ////////////////////////////////////////////////////////////////////////////
                    // LIST
                    ////////////////////////////////////////////////////////////////////////////

                    if(usage.equalsIgnoreCase("list")){

                        if(strings.length == 1){

                            if(ArenaManager.INSTANCE.getArenas().isEmpty()){
                                commandSender.sendMessage(ChatColor.RED + "No arenas have been created.");
                            } else {

                                StringBuilder builder = new StringBuilder();
                                builder.append(ChatColor.WHITE).append("Available arenas: ");

                                for(Arena arena : ArenaManager.INSTANCE.getArenas().values()){
                                    builder.append(arena.getName()).append(" ");
                                }

                                commandSender.sendMessage(ChatColor.GREEN + builder.toString());

                            }

                        } else {
                           commandSender.sendMessage(ChatColor.RED + "Error: Incorrect Arguments");
                           commandSender.sendMessage(ChatColor.RED + "Usage: /blocksurvival list");
                        }

                        return true;
                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // STATUS
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("status")){

                        if(strings.length == 2){

                            String arenaName = strings[1];

                            if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                commandSender.sendMessage(ChatColor.RED + "Error: Arena " + ChatColor.WHITE + arenaName + ChatColor.RED + " does not exist.");
                            } else {

                                Arena arena = ArenaManager.INSTANCE.getArenas().get(arenaName);

                                if(GlobalSettings.INSTANCE.isLobbyValid()){
                                    commandSender.sendMessage(ChatColor.WHITE + "Main Lobby: " + ChatColor.GOLD + GlobalSettings.INSTANCE.getStringLobby() + ChatColor.GREEN + " [READY]");
                                } else {
                                    commandSender.sendMessage(ChatColor.WHITE + "Main Lobby: " + ChatColor.GOLD + GlobalSettings.INSTANCE.getStringLobby() + ChatColor.RED + " [NOT READY]");
                                }

                                if(arena.isArenaValid()){
                                    commandSender.sendMessage(ChatColor.WHITE + "Arena: " + ChatColor.GOLD + arena.getName() + ChatColor.GREEN + " [READY]");
                                } else {
                                    commandSender.sendMessage(ChatColor.WHITE + "Arena: " + ChatColor.GOLD + arena.getName() + ChatColor.RED + " [NOT READY]");
                                }

                                if(arena.getArenaState() == ArenaState.ENABLED){
                                    commandSender.sendMessage(ChatColor.WHITE + "Arena: " + ChatColor.GOLD + arena.getArenaState() + ChatColor.GREEN + " [READY]");
                                } else {
                                    commandSender.sendMessage(ChatColor.WHITE + "Arena: " + ChatColor.GOLD + arena.getArenaState() + ChatColor.RED + " [NOT READY]");
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
                            commandSender.sendMessage(ChatColor.RED + "Error: Incorrect Arguments");
                            commandSender.sendMessage(ChatColor.RED + "Usage: /blocksurvival status <arena>");
                        }

                        return true;
                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // CREATE
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("create")){

                        if(strings.length == 2){

                            String arenaName = strings[1];
                            String worldName = player.getWorld().getName();

                            if(ArenaManager.INSTANCE.getArena(arenaName) == null){

                                Arena arena = new Arena(arenaName);
                                arena.setWorld(worldName);

                                ArenaManager.INSTANCE.addArena(arenaName, arena);
                                commandSender.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arenaName + ChatColor.GREEN + " created successfully.");

                                FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                arenaConfiguration.set("Arenas." + arenaName + ".Enabled", false);
                                arenaConfiguration.set("Arenas." + arenaName + ".World", worldName);
                                arenaConfiguration.set("Arenas." + arenaName + ".LobbyTime", 30);
                                arenaConfiguration.set("Arenas." + arenaName + ".Difficulty", 1);
                                arenaConfiguration.set("Arenas." + arenaName + ".UsingFloor", false);
                                arenaConfiguration.set("Arenas." + arenaName + ".Floor", 0);

                                ConfigurationManager.INSTANCE.saveArenaConfiguration();
                            } else {
                                commandSender.sendMessage(ChatColor.RED + "Arena " + ChatColor.WHITE + arenaName +  ChatColor.RED + " already exists.");
                            }

                        } else {
                            commandSender.sendMessage(ChatColor.RED + "Error: Incorrect Arguments");
                            commandSender.sendMessage(ChatColor.RED + "Usage: /blocksurvival create <arena>");
                        }

                        return true;
                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // DELETE
                    ////////////////////////////////////////////////////////////////////////////

                    // TODO: REMOVE ARENA FROM CONFIG

                    else if(usage.equalsIgnoreCase("delete")){

                        if(strings.length == 2){

                            String arenaName = strings[1];

                            if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                commandSender.sendMessage(ChatColor.RED + "Error: Arena " + ChatColor.WHITE + " " + arenaName +
                                        ChatColor.RED + " does not exist.");
                            } else {
                                ArenaManager.INSTANCE.removeArena(arenaName);
                                commandSender.sendMessage(ChatColor.GREEN + "Arena " + ChatColor.WHITE + arenaName + ChatColor.GREEN + " deleted.");
                            }

                        } else {
                            commandSender.sendMessage(ChatColor.RED + "Error: Incorrect Arguments");
                            commandSender.sendMessage(ChatColor.RED + "Usage: /blocksurvival create <arena>");
                        }

                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // ENABLE
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("enable")){

                        if(strings.length == 2){

                            String arenaName = strings[1];

                            if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                commandSender.sendMessage(ChatColor.RED + "Error: Arena " + ChatColor.WHITE + " " + arenaName +
                                        ChatColor.RED + " does not exist.");
                            } else {

                                Arena arena = ArenaManager.INSTANCE.getArena(arenaName);

                                if(arena.getArenaState() == ArenaState.DISABLED){
                                    arena.setArenaState(ArenaState.ENABLED);
                                    commandSender.sendMessage(ChatColor.WHITE + "Arena " + ChatColor.GOLD + arenaName + ChatColor.GREEN + " enabled.");

                                    FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                    arenaConfiguration.set("Arenas." + arenaName + ".Enabled", true);
                                    ConfigurationManager.INSTANCE.saveArenaConfiguration();
                                } else {
                                    commandSender.sendMessage(ChatColor.WHITE + "Arena " + ChatColor.GOLD + arenaName + ChatColor.RED + " is already enabled!");
                                }

                            }

                        } else {
                            commandSender.sendMessage(ChatColor.RED + "Error: Incorrect Arguments");
                            commandSender.sendMessage(ChatColor.RED + "Usage: /BlockSurvival enable <arena>");
                        }

                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // DISABLE
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("disable")){

                        if(strings.length == 2){

                            String arenaName = strings[1];

                            if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                commandSender.sendMessage(ChatColor.RED + "Error: Arena " + ChatColor.WHITE + " " + arenaName +
                                        ChatColor.RED + " does not exist.");
                            } else {
                                Arena arena = ArenaManager.INSTANCE.getArena(arenaName);

                                if(arena.getArenaState() == ArenaState.DISABLED){
                                    commandSender.sendMessage(ChatColor.WHITE + "Arena " + ChatColor.GOLD + arenaName + ChatColor.RED + " is already disabled!");
                                } else {
                                    arena.setArenaState(ArenaState.DISABLED);
                                    commandSender.sendMessage(ChatColor.WHITE + "Arena " + ChatColor.GOLD + arenaName + ChatColor.RED + " disabled.");

                                    FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                    arenaConfiguration.set("Arenas." + arenaName + ".Enabled", false);
                                    ConfigurationManager.INSTANCE.saveArenaConfiguration();
                                }

                            }

                        } else {
                            commandSender.sendMessage(ChatColor.RED + "Error: Incorrect Arguments");
                            commandSender.sendMessage(ChatColor.RED + "Usage: /BlockSurvival disable <arena>");
                        }

                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // SET MAIN LOBBY
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("setmainlobby")){

                        if(strings.length == 1){

                            BlockVector lobby = player.getLocation().toVector().toBlockVector();
                            String worldName = player.getWorld().getName();

                            GlobalSettings.INSTANCE.setWorld(worldName);
                            GlobalSettings.INSTANCE.setLobby(lobby);

                            FileConfiguration configuration = ConfigurationManager.INSTANCE.getConfiguration();
                            configuration.set("Game.World", worldName);
                            configuration.set("Game.Lobby.x", lobby.getX());
                            configuration.set("Game.Lobby.y", lobby.getY());
                            configuration.set("Game.Lobby.z", lobby.getZ());

                            ConfigurationManager.INSTANCE.saveConfiguration();

                            commandSender.sendMessage(ChatColor.WHITE + "Main Lobby set to " + ChatColor.GOLD + GlobalSettings.INSTANCE.getStringLobby());

                        } else {
                            commandSender.sendMessage(ChatColor.RED + "Error: Incorrect Arguments");
                            commandSender.sendMessage(ChatColor.RED + "Usage: /BlockSurvival setmainlobby");
                        }

                        return true;
                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // DIFFICULTY
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("difficulty")){

                        if(strings.length == 3){

                            String arenaName = strings[1];
                            String number = strings[2];
                            boolean isInteger = NumberUtils.isInteger(number);

                            if(isInteger){
                                int difficulty = Integer.parseInt(number);

                                if(difficulty < 0){
                                    commandSender.sendMessage(ChatColor.RED + "Difficulty of the arena cannot be lower than 0!");
                                } else if (difficulty > 5) {
                                    commandSender.sendMessage(ChatColor.RED + "Difficulty of the arena cannot be greater than 5!");
                                } else {

                                    if(ArenaManager.INSTANCE.getArena(arenaName) ==  null){
                                        commandSender.sendMessage(ChatColor.RED + "Error: Arena " + ChatColor.WHITE + arenaName +
                                                ChatColor.RED + " does not exist.");
                                    } else {

                                        ArenaManager.INSTANCE.getArena(arenaName).setDifficulty(difficulty);
                                        commandSender.sendMessage(ChatColor.GREEN + "Difficulty of " + ChatColor.WHITE + arenaName
                                                + ChatColor.GREEN + " set to " + difficulty);

                                        FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                        arenaConfiguration.set("Arenas." + arenaName + ".Difficulty", difficulty);
                                        ConfigurationManager.INSTANCE.saveArenaConfiguration();
                                    }

                                }

                            } else {
                                commandSender.sendMessage(ChatColor.RED + "Error: " + ChatColor.WHITE + number + ChatColor.RED + " is not a valid number.");
                            }

                            return true;

                        } else {
                            commandSender.sendMessage(ChatColor.RED + "Error: Incorrect Arguments");
                            commandSender.sendMessage(ChatColor.RED + "Usage: /BlockSurvival difficulty <arena> <amount>");
                        }

                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // POS1
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("pos1")){

                        if(strings.length == 2){

                            String arenaName = strings[1];

                            if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                commandSender.sendMessage(ChatColor.RED + "Error: Arena " + ChatColor.WHITE + arenaName +
                                        ChatColor.RED + " does not exist.");
                            } else {

                                Arena arena = ArenaManager.INSTANCE.getArena(arenaName);

                                BlockVector pos1 = player.getLocation().clone().subtract(0, 1, 0).toVector().toBlockVector();
                                arena.setPos1(pos1);

                                commandSender.sendMessage(ChatColor.GREEN + "Pos1 of " + ChatColor.WHITE + arenaName +  ChatColor.GREEN + " set to " + arena.getStringPos1());

                                FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                arenaConfiguration.set("Arenas." + arenaName + ".Position1.x", pos1.getBlockX());
                                arenaConfiguration.set("Arenas." + arenaName + ".Position1.y", pos1.getBlockY());
                                arenaConfiguration.set("Arenas." + arenaName + ".Position1.z", pos1.getBlockZ());

                                ConfigurationManager.INSTANCE.saveArenaConfiguration();
                            }

                        } else {
                            commandSender.sendMessage(ChatColor.RED + "Error: Incorrect Arguments");
                            commandSender.sendMessage(ChatColor.RED + "Usage: /blocksurvival pos1 <arena>");
                        }

                        return true;
                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // POS2
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("pos2")){

                        if(strings.length == 2){

                            String arenaName = strings[1];

                            if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                commandSender.sendMessage(ChatColor.RED + "Error: Arena " + ChatColor.WHITE + arenaName +
                                        ChatColor.RED + " does not exist.");
                            } else {
                                Arena arena = ArenaManager.INSTANCE.getArena(arenaName);

                                BlockVector pos2 = player.getLocation().clone().subtract(0, 1, 0).toVector().toBlockVector();
                                arena.setPos2(pos2);

                                commandSender.sendMessage(ChatColor.GREEN + "Pos2 of " + ChatColor.WHITE + arenaName +  ChatColor.GREEN + " set to " + arena.getStringPos2());

                                FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                arenaConfiguration.set("Arenas." + arenaName + ".Position2.x", pos2.getBlockX());
                                arenaConfiguration.set("Arenas." + arenaName + ".Position2.y", pos2.getBlockY());
                                arenaConfiguration.set("Arenas." + arenaName + ".Position2.z", pos2.getBlockZ());

                                ConfigurationManager.INSTANCE.saveArenaConfiguration();
                            }

                        } else {
                            commandSender.sendMessage(ChatColor.RED + "Error: Incorrect Arguments");
                            commandSender.sendMessage(ChatColor.RED + "Usage: /blocksurvival pos2 <arena>");
                        }

                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // FLOOR
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("floor")){

                        if(strings.length == 2){

                            String arenaName = strings[1];

                            if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                commandSender.sendMessage(ChatColor.RED + "Error: Arena " + ChatColor.WHITE + arenaName +
                                        ChatColor.RED + " does not exist.");
                            } else {
                                Arena arena = ArenaManager.INSTANCE.getArena(arenaName);
                                arena.setFloorUsage(true);
                                arena.setFloor(player.getLocation().getBlockY());

                                commandSender.sendMessage(ChatColor.GREEN + "Floor of " + ChatColor.WHITE + arenaName +  ChatColor.GREEN + " set to " + arena.getFloor());

                                FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                arenaConfiguration.set("Arenas." + arenaName + ".UsingFloor", true);
                                arenaConfiguration.set("Arenas." + arenaName + ".Floor", arena.getFloor());

                                ConfigurationManager.INSTANCE.saveArenaConfiguration();
                            }

                        } else {
                            commandSender.sendMessage(ChatColor.RED + "Error: Incorrect Arguments");
                            commandSender.sendMessage(ChatColor.RED + "Usage: /BlockSurvival floor <arena>");
                        }

                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // MINPLAYERS
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("setminplayers")){

                        if(strings.length == 3){

                            String arenaName = strings[1];
                            String number = strings[2];
                            boolean isInteger = NumberUtils.isInteger(number);

                            if(isInteger){
                                int amount = Integer.parseInt(strings[2]);

                                if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                    commandSender.sendMessage(ChatColor.RED + "Error: Arena " + ChatColor.WHITE + arenaName +
                                            ChatColor.RED + " does not exist.");
                                } else {
                                    Arena arena = ArenaManager.INSTANCE.getArena(arenaName);
                                    arena.setMinPlayers(amount);
                                    commandSender.sendMessage(ChatColor.GREEN + "Minimum number of players for " + ChatColor.WHITE + arenaName
                                            + ChatColor.GREEN + " set to " + amount);

                                    FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                    arenaConfiguration.set("Arenas." + arenaName + ".MinimumPlayers", amount);
                                    ConfigurationManager.INSTANCE.saveArenaConfiguration();
                                }

                            } else {
                                commandSender.sendMessage(ChatColor.RED + "Error: " + ChatColor.WHITE + number + ChatColor.RED + " is not a valid number.");
                            }

                            return true;

                        } else {
                            commandSender.sendMessage(ChatColor.RED + "Error: Incorrect Arguments");
                            commandSender.sendMessage(ChatColor.RED + "Usage: /BlockSurvival setminplayers <arena> <amount>");
                        }

                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // MAXPLAYERS
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("setmaxplayers")){

                        if(strings.length == 3){

                            String arenaName = strings[1];
                            String number = strings[2];
                            boolean isInteger = NumberUtils.isInteger(number);

                            if(isInteger){
                                int amount = Integer.parseInt(strings[2]);

                                if(ArenaManager.INSTANCE.getArena(arenaName) == null){
                                    commandSender.sendMessage(ChatColor.RED + "Error: Arena " + ChatColor.WHITE + arenaName + ChatColor.RED + " does not exist.");
                                } else {
                                    Arena arena = ArenaManager.INSTANCE.getArena(arenaName);
                                    arena.setMaxPlayers(amount);
                                    commandSender.sendMessage(ChatColor.GREEN + "Maximum number of players for " + ChatColor.WHITE + arenaName + ChatColor.GREEN + " set to " + amount);

                                    FileConfiguration arenaConfiguration = ConfigurationManager.INSTANCE.getArenaConfiguration();
                                    arenaConfiguration.set("Arenas." + arenaName + ".MaximumPlayers", amount);
                                    ConfigurationManager.INSTANCE.saveArenaConfiguration();
                                }

                            } else {
                                commandSender.sendMessage(ChatColor.RED + "Error: " + ChatColor.WHITE + number + ChatColor.RED + " is not a valid number.");
                            }

                            return true;

                        } else {
                            commandSender.sendMessage(ChatColor.RED + "Error: Incorrect Arguments");
                            commandSender.sendMessage(ChatColor.RED + "Usage: /BlockSurvival setmaxplayers <arena> <amount>");
                        }

                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // JOIN
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("join")){

                        if(strings.length == 2){

                            boolean playerAlreadyInGame = false;

                            for(Arena arena : ArenaManager.INSTANCE.getArenas().values()){
                                if(arena.playerInsideArena(player)){
                                    commandSender.sendMessage(ChatColor.RED + "Error: You cannot join an arena while inside of an arena!");
                                    playerAlreadyInGame = true;
                                    break;
                                }
                            }

                            if(!playerAlreadyInGame){

                                if(GlobalSettings.INSTANCE.isLobbyValid()) {
                                    String arenaName = strings[1];

                                    if(ArenaManager.INSTANCE.getArenas().containsKey(arenaName)){
                                        Arena arena = ArenaManager.INSTANCE.getArenas().get(arenaName);

                                        if(arena.isArenaValid()){

                                            if(arena.joinable()){
                                                arena.join(player);

                                                player.setGameMode(GameMode.ADVENTURE);

                                                player.teleport(ArenaUtils.getRandomLocation(
                                                        arena.getWorld(),
                                                        arena.getPos1(),
                                                        arena.getPos2()));

                                            } else {
                                                commandSender.sendMessage(ChatColor.RED + "Arena " + ChatColor.GOLD + arena.getName() + ChatColor.RED + " has already started!");
                                            }
                                        } else {
                                            commandSender.sendMessage(ChatColor.RED + "Arena " + ChatColor.GOLD + arena.getName() + ChatColor.RED + " cannot be started because it is currently invalid!");
                                        }

                                    } else {
                                        commandSender.sendMessage(ChatColor.RED + "Error: Arena " + ChatColor.WHITE + " " + arenaName +
                                                ChatColor.RED + " does not exist.");
                                    }

                                } else{
                                    commandSender.sendMessage(ChatColor.RED + Strings.LOBBY_INVALID);
                                }
                            }

                        } else {
                            commandSender.sendMessage(ChatColor.RED + "Error: Incorrect Arguments");
                            commandSender.sendMessage(ChatColor.RED + "Usage: /BlockSurvival join <arena>");
                        }

                        return true;
                    }

                    ////////////////////////////////////////////////////////////////////////////
                    // LEAVE
                    ////////////////////////////////////////////////////////////////////////////

                    else if(usage.equalsIgnoreCase("leave")){

                        if(strings.length == 1){

                            boolean playerInsideArena = false;

                            for(Arena arena : ArenaManager.INSTANCE.getArenas().values()){
                                if(arena.playerInsideArena(player)){

                                    arena.leave(player);

                                    player.teleport(
                                            new Location(
                                                    Bukkit.getWorld(GlobalSettings.INSTANCE.getWorld()),
                                                    GlobalSettings.INSTANCE.getLobby().getX(),
                                                    GlobalSettings.INSTANCE.getLobby().getY(),
                                                    GlobalSettings.INSTANCE.getLobby().getZ()
                                            )
                                    );

                                    commandSender.sendMessage(Strings.PREFIX + ChatColor.AQUA + "You have left " + ChatColor.WHITE + arena.getName() + ChatColor.AQUA + " !");
                                    playerInsideArena = true;
                                    break;
                                }
                            }

                            if(!playerInsideArena){
                                commandSender.sendMessage(ChatColor.RED + "Error: You have not joined an arena!");
                            }

                        } else {
                            commandSender.sendMessage(ChatColor.RED + "Error: Incorrect Arguments");
                            commandSender.sendMessage(ChatColor.RED + "Usage: /BlockSurvival leave");
                        }

                        return true;
                    }

                    // No such command exists
                    else {
                        commandSender.sendMessage(ChatColor.RED + "Error: " + ChatColor.WHITE + usage + ChatColor.RED + " is not a valid command.");
                    }

                }

            }

        } else {
            commandSender.sendMessage(ChatColor.RED + "Commands can only be executed as a player!");
        }

        return true;
    }

}

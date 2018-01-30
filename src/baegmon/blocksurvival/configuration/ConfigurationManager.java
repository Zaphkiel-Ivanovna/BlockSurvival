package baegmon.blocksurvival.configuration;

import baegmon.blocksurvival.BlockPlugin;
import baegmon.blocksurvival.game.Arena;
import baegmon.blocksurvival.game.ArenaSign;
import baegmon.blocksurvival.game.Global;
import baegmon.blocksurvival.manager.ArenaManager;
import baegmon.blocksurvival.tools.NumberUtils;
import baegmon.blocksurvival.tools.SignType;
import baegmon.blocksurvival.tools.Strings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.BlockVector;

import java.io.File;
import java.io.IOException;

public enum ConfigurationManager {

    INSTANCE;

    private static BlockPlugin plugin = BlockPlugin.getPlugin(BlockPlugin.class);

    private FileConfiguration configuration;
    private FileConfiguration arenaConfiguration;

    private File arenaFile;
    private File configFile;

    public void setup(){

        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdir();
        }

        configFile = new File(plugin.getDataFolder(), "config.yml");
        arenaFile = new File(plugin.getDataFolder(), "arenas.yml");

        if(!configFile.exists()){
            try{
                configFile.createNewFile();
            } catch (IOException e){
                Bukkit.getServer().getConsoleSender().sendMessage(Strings.PREFIX + ChatColor.RED + "Could not create config.yml");
            }
        }

        if(!arenaFile.exists()){
            try{
                arenaFile.createNewFile();
            } catch (IOException e){
                Bukkit.getServer().getConsoleSender().sendMessage(Strings.PREFIX + ChatColor.RED + "Could not create arenas.yml");
            }
        }

        configuration = YamlConfiguration.loadConfiguration(configFile);
        arenaConfiguration = YamlConfiguration.loadConfiguration(arenaFile);

        Bukkit.getServer().getConsoleSender().sendMessage( Strings.PREFIX + ChatColor.GREEN + "Plugin configuration file (config.yml) was loaded!");
        Bukkit.getServer().getConsoleSender().sendMessage(Strings.PREFIX + ChatColor.GREEN + "Arenas configuration file (arenas.yml) was loaded!");

        if(configuration.contains("Game")){
            Global.INSTANCE.setWorld(configuration.getString("Game.Lobby.World"));
            Global.INSTANCE.setLobby(
                    new BlockVector(
                            configuration.getInt("Game.Lobby.x"),
                            configuration.getInt("Game.Lobby.y"),
                            configuration.getInt("Game.Lobby.z")
                    )
            );

            if(configuration.contains("Game.Signs")){
                for(String key : configuration.getConfigurationSection("Game.Signs").getKeys(false)){
                    boolean flag = NumberUtils.isInteger(key);

                    if(flag){

                        ArenaSign sign = new ArenaSign(Integer.parseInt(key));

                        String type = configuration.getString("Game.Signs." + key + ".Type");

                        if(type.equalsIgnoreCase("LEAVE")){
                            sign.setType(SignType.LEAVE);
                            sign.setWorld(configuration.getString("Game.Signs." + key + ".World"));
                            sign.setX(configuration.getInt("Game.Signs." + key + ".x"));
                            sign.setY(configuration.getInt("Game.Signs." + key + ".y"));
                            sign.setZ(configuration.getInt("Game.Signs." + key + ".z"));
                        } else if (type.equalsIgnoreCase("JOIN")){
                            sign.setType(SignType.JOIN);
                            sign.setWorld(configuration.getString("Game.Signs." + key + ".World"));
                            sign.setArena(configuration.getString("Game.Signs." + key + ".Arena"));
                            sign.setX(configuration.getInt("Game.Signs." + key + ".x"));
                            sign.setY(configuration.getInt("Game.Signs." + key + ".y"));
                            sign.setZ(configuration.getInt("Game.Signs." + key + ".z"));
                        }

                        Global.INSTANCE.addSign(sign);
                    }

                }
            }

        } else {
            saveConfiguration();
        }

        if(arenaConfiguration.contains("Arenas")){
            for(String key : arenaConfiguration.getConfigurationSection("Arenas").getKeys(false)){

                Arena arena = new Arena(key);

                arena.setArenaState(arenaConfiguration.getBoolean("Arenas." + key + ".Enabled"));
                arena.setWorld(arenaConfiguration.getString("Arenas." + key + ".World"));
                arena.setDifficulty(arenaConfiguration.getInt("Arenas." + key + ".Difficulty"));
                arena.setMinPlayers(arenaConfiguration.getInt("Arenas." + key + ".MinimumPlayers"));
                arena.setMaxPlayers(arenaConfiguration.getInt("Arenas." + key + ".MaximumPlayers"));

                arena.setPos1(
                        new BlockVector(
                                arenaConfiguration.getInt("Arenas." + key + ".Position1.x"),
                                arenaConfiguration.getInt("Arenas." + key + ".Position1.y"),
                                arenaConfiguration.getInt("Arenas." + key + ".Position1.z")
                        )
                );
                arena.setPos2(
                        new BlockVector(
                                arenaConfiguration.getInt("Arenas." + key + ".Position2.x"),
                                arenaConfiguration.getInt("Arenas." + key + ".Position2.y"),
                                arenaConfiguration.getInt("Arenas." + key + ".Position2.z")
                        )
                );

                arena.setWait(arenaConfiguration.getInt("Arenas." + key + ".LobbyTime"));
                arena.setFloorUsage(arenaConfiguration.getBoolean("Arenas." + key + ".UsingFloor"));
                arena.setFloor(arenaConfiguration.getDouble("Arenas." + key + ".Floor"));
                arena.setType(arenaConfiguration.getString("Arenas." + key + ".Type"));

                arena.saveArenaBlocks();

                ArenaManager.INSTANCE.addArena(arena.getName(), arena);

            }
        }
    }

    public FileConfiguration getConfiguration() {
        return configuration;
    }

    public FileConfiguration getArenaConfiguration() {
        return arenaConfiguration;
    }

    public void saveConfiguration(){
        try {
            configuration.save(configFile);
        } catch (IOException e){
            Bukkit.getServer().getConsoleSender().sendMessage(Strings.PREFIX + ChatColor.RED + "Plugin configuration file (config.yml) could not be saved!");
        }
    }

    public void saveArenaConfiguration(){
        try {
            arenaConfiguration.save(arenaFile);
        } catch (IOException e){
            Bukkit.getServer().getConsoleSender().sendMessage(Strings.PREFIX + ChatColor.RED + "Arenas configuration file (arenas.yml) could not be saved!");
        }
    }
}

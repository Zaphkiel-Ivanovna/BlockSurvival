package baegmon.blocksurvival.configuration;

import baegmon.blocksurvival.BlockPlugin;
import baegmon.blocksurvival.game.Arena;
import baegmon.blocksurvival.game.GlobalSettings;
import baegmon.blocksurvival.manager.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.BlockVector;

import java.io.File;
import java.io.IOException;

public enum ConfigurationManager {

    INSTANCE;

    private BlockPlugin plugin = BlockPlugin.getPlugin(BlockPlugin.class);

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
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[BlockSurvival] Could not create config.yml");
            }
        }

        if(!arenaFile.exists()){
            try{
                arenaFile.createNewFile();
            } catch (IOException e){
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[BlockSurvival] Could not create arenas.yml");
            }
        }


        configuration = YamlConfiguration.loadConfiguration(configFile);
        arenaConfiguration = YamlConfiguration.loadConfiguration(arenaFile);

        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[BlockSurvival] Plugin configuration file (config.yml) was loaded!");
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[BlockSurvival] Arenas configuration file (arenas.yml) was loaded!");

        if(configuration.contains("Game")){
            GlobalSettings.INSTANCE.setBlocksDrop(configuration.getBoolean("Game.BlocksDrop"));
            GlobalSettings.INSTANCE.setWorld(configuration.getString("Game.World"));
            GlobalSettings.INSTANCE.setLobby(
                    new BlockVector(
                            configuration.getInt("Game.Lobby.x"),
                            configuration.getInt("Game.Lobby.y"),
                            configuration.getInt("Game.Lobby.z")
                    )
            );
        } else {
            configuration.set("Game.BlocksDrop", GlobalSettings.INSTANCE.doBlocksDrop());
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
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[BlockSurvival] Plugin configuration file (config.yml) was created!");
        }
    }

    public void saveArenaConfiguration(){
        try {
            arenaConfiguration.save(arenaFile);
        } catch (IOException e){
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[BlockSurvival] Arenas configuration file (arenas.yml) was created!");
        }
    }

    public void reload(){
        if(configuration != null && arenaConfiguration != null){
            configuration = YamlConfiguration.loadConfiguration(configFile);
            arenaConfiguration = YamlConfiguration.loadConfiguration(arenaFile);
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[BlockSurvival] Plugin configuration file (config.yml) was created!");
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[BlockSurvival] Arenas configuration file (arenas.yml) was created!");
        } else {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[BlockSurvival] Plugin configuration file (config.yml) was created!");
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[BlockSurvival] Arenas configuration file (arenas.yml) was created!");
        }
    }

}

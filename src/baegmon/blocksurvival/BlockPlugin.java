package baegmon.blocksurvival;

import baegmon.blocksurvival.command.BlockSurvivalCommand;
import baegmon.blocksurvival.configuration.ConfigurationManager;
import baegmon.blocksurvival.listener.ArenaListener;
import baegmon.blocksurvival.listener.SignListener;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockPlugin extends JavaPlugin  {

    @Override
    public void onEnable() {
        this.getCommand("blocksurvival").setExecutor(new BlockSurvivalCommand());
        this.getServer().getPluginManager().registerEvents(new ArenaListener(), this);
        this.getServer().getPluginManager().registerEvents(new SignListener(), this);

        // This line allows the server to finish loading all plugins before the configuration files are loaded
        // Allows custom worlds plugins such as Multiverse etc to be compatible
        getServer().getScheduler().scheduleSyncDelayedTask(this, this::loadConfiguration, 20);

    }

    @Override
    public void onDisable() {

    }

    private void loadConfiguration(){
        ConfigurationManager.INSTANCE.setup();
    }

}

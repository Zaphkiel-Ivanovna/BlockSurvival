package baegmon.blocksurvival;

import baegmon.blocksurvival.command.BlockSurvivalCommand;
import baegmon.blocksurvival.configuration.ConfigurationManager;
import baegmon.blocksurvival.listener.ArenaListener;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockPlugin extends JavaPlugin  {

    @Override
    public void onEnable() {
        this.getCommand("blocksurvival").setExecutor(new BlockSurvivalCommand());
        this.getServer().getPluginManager().registerEvents(new ArenaListener(), this);

        loadConfiguration();
    }

    @Override
    public void onDisable() {

    }

    private void loadConfiguration(){
        ConfigurationManager.INSTANCE.setup();
    }

}

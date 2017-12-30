package baegmon.blocksurvival.game;

import org.bukkit.util.BlockVector;

public enum GlobalSettings {

    INSTANCE;

    private BlockVector lobby; // location of the main lobby
    private String world; // world of the main lobby

    // this variable checks if the blocks will drop or just turn to air
    private boolean blocksDrop = true;

    public BlockVector getLobby() {
        return lobby;
    }

    public String getWorld() {
        return world;
    }

    public boolean doBlocksDrop() {
        return blocksDrop;
    }

    public void setLobby(BlockVector lobby) {
        this.lobby = lobby;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public void setBlocksDrop(boolean blocksDrop) {
        this.blocksDrop = blocksDrop;
    }

    public String getStringLobby(){
        return "[" + lobby.getBlockX() + ", " + lobby.getBlockY() + ", " + lobby.getBlockZ() + "]";
    }

    public boolean isLobbyValid(){
        return !world.isEmpty() && lobby != null && lobby.getBlockX() != 0 && lobby.getBlockY() != 0 && lobby.getBlockZ() != 0;
    }
}

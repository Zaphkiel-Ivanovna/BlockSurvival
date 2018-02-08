package baegmon.blocksurvival.game;

import org.bukkit.util.BlockVector;

import java.util.ArrayList;

public enum Global {

    INSTANCE;

    private BlockVector lobby; // location of the main lobby
    private String world; // world of the main lobby

    private ArrayList<ArenaSign> signs = new ArrayList<>();

    public BlockVector getLobby() {
        return lobby;
    }

    public String getWorld() {
        return world;
    }

    public void setLobby(BlockVector lobby) {
        this.lobby = lobby;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public String getStringLobby(){
        if(lobby == null){
            return "[LOBBY HAS NOT BEEN SET]";
        }
        return "[" + lobby.getBlockX() + ", " + lobby.getBlockY() + ", " + lobby.getBlockZ() + "]";
    }

    public boolean isLobbyValid(){
        return world != null && !world.isEmpty() && lobby != null && !(lobby.getBlockX() == 0 && lobby.getBlockY() == 0 && lobby.getBlockZ() == 0);
    }

    public ArrayList<ArenaSign> getSigns() {
        return signs;
    }

    public int getSignID(){ return signs.size(); }

    public void addSign(ArenaSign sign){
        signs.add(sign);
    }

}

package baegmon.blocksurvival.manager;

import baegmon.blocksurvival.game.Arena;

import java.util.HashMap;

public enum ArenaManager {

    INSTANCE;

    private HashMap<String, Arena> arenas = new HashMap<>();

    public HashMap<String, Arena> getArenas() {
        return arenas;
    }

    public Arena getArena(String arenaName){
        return arenas.get(arenaName);
    }

    public void addArena(String arenaName, Arena arena){
        arenas.put(arenaName, arena);
    }

    public void removeArena(String arenaName){
        arenas.remove(arenaName);
    }

}

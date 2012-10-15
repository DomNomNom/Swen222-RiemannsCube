package world.events;

import utils.Int3;

public class PlayerSpawning extends Action {
    
    public final int playerID;
    public final String playerName;
    public final Int3 pos;  // change in position TODO this isn't used anymore
    
    
    public PlayerSpawning(int playerID, Int3 pos, String playerName){
        this.playerID = playerID;
        this.playerName = playerName;
        
        if (pos == null) throw new IllegalArgumentException("trying to spawn a player at no position"); 
        this.pos = pos;
    }
}

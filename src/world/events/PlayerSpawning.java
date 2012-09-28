package world.events;

import utils.Int3;

public class PlayerSpawning extends Action {
    
    public final int playerID;
    public final Int3 pos;  // change in position
    
    
    public PlayerSpawning(int playerID, Int3 pos){
        this.playerID = playerID;
        
        if (pos == null) throw new IllegalArgumentException("trying to spawn a player at no position"); 
        this.pos = pos;
    }
}

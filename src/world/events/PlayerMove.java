package world.events;

import java.io.Serializable;

public class PlayerMove extends Action implements Serializable{

    private static final long serialVersionUID = -8441050302008653548L;
    
    public final int playerID;
    public final int x, y, z;  // movement (change)
        
    public PlayerMove(int playerID, int x, int y, int z){
        this.playerID = playerID;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
   
}

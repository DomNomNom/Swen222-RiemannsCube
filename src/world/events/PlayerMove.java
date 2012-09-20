package world.events;

import java.io.Serializable;

public class PlayerMove extends Action implements Serializable{

    private static final long serialVersionUID = -8441050302008653548L;
    
    
    public final int x, y, z;  // to-position
    
    public PlayerMove(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
   
}

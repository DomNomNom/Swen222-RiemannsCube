package world.events;

import java.io.Serializable;

import utils.Int3;

/**
 * A action that says "Player 2 is moving UP"
 * 
 * ensures movement != null
 * when successfully applied, player.pos()+player.relPos is the same value before and after
 *
 * @author schmiddomi
 */
public class PlayerMove extends Action implements Serializable{

    private static final long serialVersionUID = -8441050302008653548L;
    
    public final int playerID;
    public final Int3 movement;  // change in position
    
    /**
     * @param playerID The ID of the player
     * @param movement The change in position
     */
    public PlayerMove(int playerID, Int3 movement){
        this.playerID = playerID;
        
        if (movement == null) this.movement = new Int3();
        else                  this.movement = movement;
    }
    
    public String toString() {
    	return playerID+" moves "+movement;
    }
    
}

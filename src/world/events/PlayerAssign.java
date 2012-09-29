package world.events;


public class PlayerAssign extends Event {
    public final int playerID;
    
    public PlayerAssign(int playerID) {
        this.playerID = playerID;
    }

}

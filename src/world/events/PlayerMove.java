package world.events;

public class PlayerMove extends Action {

    public final int x, y, z;  // to-position
    
    public PlayerMove(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
}

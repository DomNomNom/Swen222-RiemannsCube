package world.objects;
import java.awt.Color;

import utils.Int3;
import utils.Float3;
import world.cubes.Cube;
import world.items.*;

public class Player extends GameObject {

    public final Float3 relPos = new Float3(); // relative position to the centre of the tile (only used by the UI... maybe)
    public final Float3 rotation = new Float3(0, 0, 0); //the rotation of the player in degrees
    
    private GameItem item;
    public GameItem item() {  return item; }
    public void setItem(GameItem item) { this.item = item; }

    private LightSource torch;
    public LightSource torch() { return torch;  }
    public void setTorch(LightSource torch) { this.torch = torch; }
    
    public final int id;
    public int id() { return id; }
    
    
    
    public Player(Cube pos, int id){
        super(pos);
        this.id = id;
    }
    
    
    
    @Override
    public String getClassName() {
        return "Player";
    }

    public String toString(){return "Player "+id;}
    
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        
        Player p = (Player) obj; // safe as it is checked above
        if (id != p.id) return false;
        if ((item==null)? p.item!=null : !item.equals(p.item())) return false;
        
        return true;
    }
}

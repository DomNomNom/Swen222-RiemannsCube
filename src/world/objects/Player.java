package world.objects;
import java.awt.Color;

import utils.Int3;
import utils.Float3;
import world.items.*;

public class Player extends GameObject {

    public final Float3 relPos = new Float3(); // relative position to the center of the tile (only used by  ) 
    
    private GameItem item;
    public GameItem item() {  return item; }
    public void setItem(GameItem item) { this.item = item; }

    private LightSource torch;
    public LightSource torch() { return torch;  }
    public void setTorch(LightSource torch) { this.torch = torch; }
    
    private int id;
    public int id() {
        return id;
    }
    
    
    
    public Player(Int3 pos, int id){
        this.id = id;
        this.pos.set(pos);
    }
    
    
    
    @Override
    public String getClassName() {
        return "Player";
    }

    public String toString(){return "Player "+id;}
}

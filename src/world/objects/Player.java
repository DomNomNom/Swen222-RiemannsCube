package world.objects;
import java.awt.Color;

import utils.Int3;
import utils.Float3;
import world.cubes.Cube;
import world.objects.items.*;

public class Player extends GameObject {
    public final Float3 relPos = new Float3(); // relative position to the centre of the tile (only used by the UI... maybe)
    public Float3 rotation = new Float3(0, 0, 0); //the rotation of the player in degrees
    private int orientation = 0; //the orientation of the player 0: ground, 1: roof, 2: left wall 3: right wall
    //4: front wall, 5: back wall. I'm not using an enum because java enums are stupid :S
    //They are stupid, aren't they! Thank you.
    public int orientation() {return orientation;}
    public void orientation(int o) {orientation = o;}
    
    private int score = 0;
    public void incrementScore(int i){score+=i;}
    public int score(){return score;}
    
    private GameItem item;
    public GameItem item() {  return item; }
    public boolean isHoldingItem() { return item != null;  }
    public void setItem(GameItem item) { this.item = item; }

    private LightSource torch;
    public LightSource torch() { return torch;  }
    public void setTorch(LightSource torch) { this.torch = torch; }
    
    public final int id;
    public int id() { return id; }
    public final String name;
    public String name(){ return name;}
    
    
    public Player(Cube pos, int id, String name){
        super(pos);
        this.id = id;
        this.name = name;
    }
    
    @Override
    public String getClassName() {
        return "Player";
    }

    public String myName(){return "[Player "+id+ "]";}
    
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        
        Player p = (Player) obj; // safe as it is checked above
        if (id != p.id) return false;
        if ((name==null)? p.name!=null : !name.equals(p.name())) return false;
        if ((item==null)? p.item!=null : !item.equals(p.item())) return false;
        
        return true;
    }
}
